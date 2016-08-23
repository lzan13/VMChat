package net.melove.app.chat.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.app.chat.application.eventbus.MLCallEvent;
import net.melove.app.chat.application.eventbus.MLConnectionEvent;
import net.melove.app.chat.application.eventbus.MLContactsEvent;
import net.melove.app.chat.application.eventbus.MLApplyForEvent;
import net.melove.app.chat.application.eventbus.MLMessageEvent;
import net.melove.app.chat.communal.base.MLBaseActivity;
import net.melove.app.chat.communal.util.MLDateUtil;
import net.melove.app.chat.communal.util.MLLog;
import net.melove.app.chat.conversation.MLMessageUtils;
import net.melove.app.chat.conversation.call.MLCallReceiver;
import net.melove.app.chat.conversation.call.MLCallStatus;
import net.melove.app.chat.database.MLDBHelper;
import net.melove.app.chat.contacts.MLContacterEntity;
import net.melove.app.chat.conversation.MLConversationExtUtils;
import net.melove.app.chat.database.MLContactsDao;
import net.melove.app.chat.notification.MLNotifier;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/13.
 * 自定义初始化类做一些环信sdk的初始化操作
 */
public class MLEasemobHelper {

    // 上下文对象
    private Context mContext;

    // MLEasemobHelper 单例对象
    private static MLEasemobHelper instance;

    // 保存当前运行的 activity 对象，可用来判断程序是否处于前台，以及完全退出app等操作
    private List<MLBaseActivity> mActivityList = new ArrayList<MLBaseActivity>();


    // 记录sdk是否初始化
    private boolean isInit;

    // 通话广播监听器
    private MLCallReceiver mCallReceiver = null;
    // 通话状态监听
    private EMCallStateChangeListener callStateListener;
    // 是否正在通话中
    public int isBus;

    // 环信的消息监听器
    private EMMessageListener mMessageListener;
    // 环信联系人监听
    private EMContactListener mContactListener;
    // 环信连接监听
    private EMConnectionListener mConnectionListener;
    // 环信群组变化监听
    private EMGroupChangeListener mGroupChangeListener;


    // 表示是是否解绑Token，一般离线状态都要设置为false
    private boolean isUnbuildToken = true;

    /**
     * 单例类，用来初始化环信的sdk
     *
     * @return 返回当前类的实例
     */
    public static MLEasemobHelper getInstance() {
        if (instance == null) {
            instance = new MLEasemobHelper();
        }
        return instance;
    }

    /**
     * 私有的构造方法
     */
    private MLEasemobHelper() {
    }

    /**
     * 初始化环信的SDK
     *
     * @param context 上下文菜单
     * @return 返回初始化状态是否成功
     */
    public synchronized boolean initEasemob(Context context) {
        MLLog.d("------- init easemob start --------------");

        mContext = context;
        // 获取当前进程 id 并取得进程名
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            // 则此application的onCreate 是被service 调用的，直接返回
            return true;
        }
        if (isInit) {
            return isInit;
        }
        mContext = context;

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(mContext, initOptions());

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // 初始化全局监听
        initGlobalListener();

        // 初始化完成
        isInit = true;
        MLLog.d("------- init easemob end --------------");
        return isInit;
    }

    private EMOptions initOptions() {
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        options.setAppKey("lzan13#hxsdkdemo");
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否按照服务器时间排序，false按照本地时间排序
        options.setSortMessageByServerTime(false);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 设置是否需要服务器收到消息确认
        options.setRequireServerAck(true);
        // TODO 设置初始化数据库DB时，每个会话要加载的Message数量，这个在后期会删除
        // options.setNumberOfMessagesLoaded(1);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(false);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(false);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);

        // 设置google GCM推送id，国内可以不用设置
        // options.setGCMNumber(MLConstants.ML_GCM_NUMBER);

        // 设置集成小米推送的appid和appkey
        options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

        // 设置华为推送appid
        options.setHuaweiPushAppId(MLConstants.ML_HUAWEI_APP_ID);
        // TODO 主动调用华为官方的注册华为推送 测试用，SDK内部已经调用
        // PushManager.requestToken(mContext);
        return options;
    }

    /**
     * 初始化全局监听，其中包括：
     * 连接监听 {@link #setConnectionListener()}
     * 消息监听 {@link #setMessageListener()}
     * 联系人监听 {@link #setContactListener()}
     * 群组监听 {@link #setGroupChangeListener()}
     */
    public void initGlobalListener() {
        MLLog.d("------- listener start --------------");
        // 设置通话广播监听
        setCallReceiverListener();
        // 通话状态监听，TODO 这里不直接调用，只需要在有通话时调用
        // setCallStateChangeListener();
        // 设置全局的连接监听
        setConnectionListener();
        // 初始化全局消息监听
        setMessageListener();
        // 设置全局的联系人变化监听
        setContactListener();
        // 设置全局的群组变化监听
        setGroupChangeListener();
        MLLog.d("------- listener end ----------------");
    }

    /**
     * 设置通话广播监听
     */
    private void setCallReceiverListener() {
        // 设置通话广播监听器过滤内容
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (mCallReceiver == null) {
            mCallReceiver = new MLCallReceiver();
        }
        //注册通话广播接收者
        mContext.registerReceiver(mCallReceiver, callFilter);
    }

    /**
     * 设置通话状态监听，监听通话状态，处理界面显示
     */
    public void setCallStateChangeListener() {
        if (callStateListener == null) {
            callStateListener = new EMCallStateChangeListener() {
                @Override
                public void onCallStateChanged(CallState callState, CallError callError) {

                    MLCallEvent event = new MLCallEvent();
                    event.setCallState(callState);
                    event.setCallError(callError);
                    EventBus.getDefault().post(event);

                    switch (callState) {
                    case CONNECTING: // 正在呼叫对方
                        MLLog.i("正在呼叫对方" + callError);
                        MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_CONNECTING);
                        break;
                    case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
                        MLLog.i("正在等待对方接受呼叫申请" + callError);
                        MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_CONNECTING);
                        break;
                    case ACCEPTED: // 通话已接通
                        MLLog.i("通话已接通");
                        MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_ACCEPTED);
                        break;
                    case DISCONNNECTED: // 通话已中断
                        MLLog.i("通话已结束" + callError);
                        // 通话结束，重置通话状态
                        MLCallStatus.getInstance().reset();
                        if (callError == EMCallStateChangeListener.CallError.ERROR_INAVAILABLE) {
                            MLLog.i("对方不在线" + callError);
                        } else if (callError == EMCallStateChangeListener.CallError.ERROR_BUSY) {
                            MLLog.i("对方正忙" + callError);
                        } else if (callError == EMCallStateChangeListener.CallError.REJECTED) {
                            MLLog.i("对方已拒绝" + callError);
                        } else if (callError == EMCallStateChangeListener.CallError.ERROR_NORESPONSE) {
                            MLLog.i("对方未响应，可能手机不在身边" + callError);
                        } else if (callError == EMCallStateChangeListener.CallError.ERROR_TRANSPORT) {
                            MLLog.i("连接建立失败" + callError);
                        } else if (callError == EMCallStateChangeListener.CallError.ERROR_LOCAL_VERSION_SMALLER) {
                            MLLog.i("双方通讯协议不同" + callError);
                        } else if (callError == EMCallStateChangeListener.CallError.ERROR_PEER_VERSION_SMALLER) {
                            MLLog.i("双方通讯协议不同" + callError);
                        } else {
                            MLLog.i("通话已结束，时长：%s，error %s", "10:35", callError);
                        }
                        // 结束通话时取消通话状态监听
                        MLEasemobHelper.getInstance().removeCallStateChangeListener();
                        break;
                    case NETWORK_UNSTABLE:
                        if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                            MLLog.i("没有通话数据" + callError);
                        } else {
                            MLLog.i("网络不稳定" + callError);
                        }
                        break;
                    case NETWORK_NORMAL:
                        MLLog.i("网络正常");
                        break;
                    case VIDEO_PAUSE:
                        MLLog.i("视频传输已暂停");
                        break;
                    case VIDEO_RESUME:
                        MLLog.i("视频传输已恢复");
                        break;
                    case VOICE_PAUSE:
                        MLLog.i("语音传输已暂停");
                        break;
                    case VOICE_RESUME:
                        MLLog.i("语音传输已恢复");
                        break;
                    default:
                        break;
                    }
                }
            };
            EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
        }
    }

    /**
     * 删除通话状态监听
     */
    public void removeCallStateChangeListener() {
        if (callStateListener != null) {
            EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
            callStateListener = null;
        }
    }

    /**
     * ------------------------------- Connection Listener ---------------------
     * 链接监听，监听与服务器连接状况
     */
    private void setConnectionListener() {
        mConnectionListener = new EMConnectionListener() {

            /**
             * 链接聊天服务器成功
             */
            @Override
            public void onConnected() {
                MLLog.d("onConnected");
                isUnbuildToken = true;
                // 设置链接监听变化状态
                MLConnectionEvent event = new MLConnectionEvent();
                event.setType(MLConstants.ML_CONNECTION_CONNECTED);
                // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
                EventBus.getDefault().post(event);
            }

            /**
             * 链接聊天服务器失败
             *
             * @param errorCode 连接失败错误码
             */
            @Override
            public void onDisconnected(final int errorCode) {
                MLLog.d("onDisconnected - %d", errorCode);
                // 在离线状态下，退出登录的时候需要设置为false，已经登录成功的状态要改为 false，这个在使用了推送功能时，调用logout需要传递
                isUnbuildToken = false;
                MLConnectionEvent event = new MLConnectionEvent();
                if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    MLLog.d("user login another device - " + errorCode);
                    signOut(null);
                    // 设置链接监听变化状态
                    event.setType(MLConstants.ML_CONNECTION_USER_LOGIN_OTHER_DIVERS);
                } else if (errorCode == EMError.USER_REMOVED) {
                    MLLog.d("user be removed - " + errorCode);
                    signOut(null);
                    // 设置链接监听变化状态
                    event.setType(MLConstants.ML_CONNECTION_USER_REMOVED);
                } else {
                    MLLog.d("con't servers - " + errorCode);
                    // 设置链接监听变化状态
                    event.setType(MLConstants.ML_CONNECTION_DISCONNECTED);
                }
                // 发送订阅消息，通知网络监听有变化
                EventBus.getDefault().post(event);
            }
        };
        EMClient.getInstance().addConnectionListener(mConnectionListener);
    }

    /**
     * ---------------------------------- Message Listener ----------------------------
     * 初始化全局的消息监听
     */
    protected void setMessageListener() {
        mMessageListener = new EMMessageListener() {
            /**
             * 收到新消息，离线消息也都是在这里获取
             * 这里在处理消息监听时根据收到的消息修改了会话对象的最后时间，是为了在会话列表中当清空了会话内容时，
             * 不用过滤掉空会话，并且能显示会话时间
             * {@link MLConversationExtUtils#setConversationLastTime(EMConversation)}
             *
             * @param list 收到的新消息集合，离线和在线都是走这个监听
             */
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                // 判断当前活动界面是不是聊天界面，如果是，全局不处理消息
                if (MLEasemobHelper.getInstance().getActivityList().size() > 0) {
                    if (MLEasemobHelper.getInstance().getTopActivity().getClass().getSimpleName().equals("MLChatActivity")) {
                        return;
                    }
                }
                // 遍历消息集合
                for (EMMessage message : list) {
                    // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
                    MLMessageEvent event = new MLMessageEvent();
                    event.setMessage(message);
                    event.setStatus(message.status());
                    EventBus.getDefault().post(event);
                }
                if (list.size() > 1) {
                    // 收到多条新消息，发送一条消息集合的通知
                    MLNotifier.getInstance().sendNotificationMessageList(list);
                } else {
                    // 只有一条消息，发送单条消息的通知
                    MLNotifier.getInstance().sendNotificationMessage(list.get(0));
                }
            }

            /**
             * 收到新的 CMD 消息
             *
             * @param list 收到的透传消息集合
             */
            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {
                for (EMMessage cmdMessage : list) {
                    EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();

                    // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
                    MLMessageEvent event = new MLMessageEvent();
                    event.setMessage(cmdMessage);
                    event.setStatus(cmdMessage.status());
                    EventBus.getDefault().post(event);

                    // 判断是不是撤回消息的透传
                    if (body.action().equals(MLConstants.ML_ATTR_RECALL)) {
                        // 判断当前活动界面是不是聊天界面，如果是，全局不处理消息，聊天界面已经处理了撤回
                        if (MLEasemobHelper.getInstance().getActivityList().size() > 0) {
                            if (MLEasemobHelper.getInstance().getTopActivity().getClass().getName().equals("MLChatActivity")) {
                                return;
                            }
                        }
                        MLMessageUtils.receiveRecallMessage(mContext, cmdMessage);
                    }
                }
            }

            /**
             * 收到新的已读回执
             *
             * @param list 收到消息已读回执
             */
            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {
            }

            /**
             * 收到新的发送回执
             *
             * @param list 收到发送回执的消息集合
             */
            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {
            }

            /**
             * 消息的状态改变
             *
             * @param message 发生改变的消息
             * @param object  包含改变的消息
             */
            @Override
            public void onMessageChanged(EMMessage message, Object object) {
            }
        };
        // 注册消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    /**
     * ---------------------------------- Contact Listener -------------------------------
     * 联系人监听，用来监听联系人的请求与变化等
     */
    private void setContactListener() {

        mContactListener = new EMContactListener() {

            /**
             * 监听到添加联系人
             *
             * @param username 被添加的联系人
             */
            @Override
            public void onContactAdded(String username) {
                // 创建一个新的联系人对象，并保存到本地
                MLContacterEntity contacts = new MLContacterEntity();
                contacts.setUserName(username);
                /**
                 * 调用{@link MLContactsDao#saveContacts(MLContacterEntity)} 去保存联系人，
                 * 这里将{@link MLContactsDao} 封装成了单例类
                 */
                MLContactsDao.getInstance().saveContacts(contacts);
                // 发送可被订阅的消息，通知订阅者联系人有变化
                EventBus.getDefault().post(new MLContactsEvent());
            }

            /**
             * 监听删除联系人
             *
             * @param username 被删除的联系人
             */
            @Override
            public void onContactDeleted(String username) {
                /**
                 * 监听到联系人被删除，删除本地的数据
                 * 调用{@link MLContactsDao#deleteContacts(String)} 去删除指定的联系人
                 * 这里将{@link MLContactsDao} 封装成了单例类
                 */
                MLContactsDao.getInstance().deleteContacts(username);
                // 发送可被订阅的消息，通知订阅者联系人有变化
                EventBus.getDefault().post(new MLContactsEvent());
            }

            /**
             * 收到对方联系人申请
             *
             * @param username 发送好友申请者username
             * @param reason 申请理由
             */
            @Override
            public void onContactInvited(String username, String reason) {
                MLLog.d("onContactInvited - username:%s, reaseon:%s", username, reason);

                // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息
                String msgId = username + EMClient.getInstance().getCurrentUser();

                // 首先查找这条申请消息是否为空
                EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                if (message != null) {
                    // 申请理由
                    message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
                    // 当前申请的消息状态
                    message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_APPLY_FOR);
                    // 更新消息时间
                    message.setMsgTime(MLDateUtil.getCurrentMillisecond());
                    message.setLocalTime(message.getMsgTime());
                    // 更新消息到本地
                    EMClient.getInstance().chatManager().updateMessage(message);
                } else {
                    // 创建一条接收的消息，用来保存申请信息
                    message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    EMTextMessageBody body = new EMTextMessageBody(username + " 申请加你好友");
                    message.addBody(body);
                    // 设置消息扩展，主要是申请信息
                    message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
                    // 申请者username
                    message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
                    // 申请理由
                    message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
                    // 当前申请的消息状态
                    message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_APPLY_FOR);
                    // 申请与通知类型
                    message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
                    // 设置消息发送方
                    message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
                    // 设置
                    message.setMsgId(msgId);
                    // 将消息保存到本地和内存
                    EMClient.getInstance().chatManager().saveMessage(message);
                }
                // 调用发送通知栏提醒方法，提醒用户查看申请通知
                MLNotifier.getInstance().sendNotificationMessage(message);
                // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
                MLApplyForEvent event = new MLApplyForEvent();
                event.setMessage(message);
                EventBus.getDefault().post(event);
            }

            /**
             * 对方同意了自己的申请
             *
             * @param username 对方的username
             */
            @Override
            public void onContactAgreed(String username) {
                MLLog.d("onContactAgreed - username:%s", username);
                // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息（申请者在前）
                String msgId = EMClient.getInstance().getCurrentUser() + username;

                // 首先查找这条申请消息是否为空
                EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                if (message != null) {
                    // 当前申请的消息状态
                    message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
                    // 更新消息到本地
                    EMClient.getInstance().chatManager().updateMessage(message);
                } else {
                    // 创建一条接收的消息，用来保存申请信息
                    message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    EMTextMessageBody body = new EMTextMessageBody(username + " 同意了你的好友申请");
                    message.addBody(body);
                    // 设置消息扩展，主要是申请信息
                    message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
                    // 申请者username
                    message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
                    // 申请理由
                    message.setAttribute(MLConstants.ML_ATTR_REASON, "我同意你的好友申请");
                    // 当前申请的消息状态
                    message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
                    // 申请与通知类型
                    message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
                    // 设置消息发送方
                    message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
                    // 设置
                    message.setMsgId(msgId);
                    // 将消息保存到本地和内存
                    EMClient.getInstance().chatManager().saveMessage(message);
                }
                // 调用发送通知栏提醒方法，提醒用户查看申请通知
                MLNotifier.getInstance().sendNotificationMessage(message);
                // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
                MLApplyForEvent event = new MLApplyForEvent();
                event.setMessage(message);
                EventBus.getDefault().post(event);
            }

            /**
             * 对方拒绝了联系人申请
             *
             * @param username 对方的username
             */
            @Override
            public void onContactRefused(String username) {
                MLLog.d("onContactRefused - username:%s", username);
                // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息（申请者在前）
                String msgId = EMClient.getInstance().getCurrentUser() + username;

                // 首先查找这条申请消息是否为空
                EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                if (message != null) {
                    // 当前申请的消息状态
                    message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
                    // 更新消息到本地
                    EMClient.getInstance().chatManager().updateMessage(message);
                } else {
                    // 创建一条接收的消息，用来保存申请信息
                    message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    EMTextMessageBody body = new EMTextMessageBody(username + " 同意了你的好友申请");
                    message.addBody(body);
                    // 设置消息扩展，主要是申请信息
                    message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
                    // 申请者username
                    message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
                    // 申请理由
                    message.setAttribute(MLConstants.ML_ATTR_REASON, "我同意你的好友申请");
                    // 当前申请的消息状态
                    message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
                    // 申请与通知类型
                    message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
                    // 设置消息发送方
                    message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
                    // 设置
                    message.setMsgId(msgId);
                    // 将消息保存到本地和内存
                    EMClient.getInstance().chatManager().saveMessage(message);
                }
                // 调用发送通知栏提醒方法，提醒用户查看申请通知
                MLNotifier.getInstance().sendNotificationMessage(message);
                // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
                MLApplyForEvent event = new MLApplyForEvent();
                event.setMessage(message);
                EventBus.getDefault().post(event);
            }
        };
        EMClient.getInstance().contactManager().setContactListener(mContactListener);
    }

    /**
     * ------------------------------------- Group Listener -------------------------------------
     * 群组变化监听，用来监听群组请求，以及其他群组情况
     */
    private void setGroupChangeListener() {
        mGroupChangeListener = new EMGroupChangeListener() {

            /**
             * 收到其他用户邀请加入群组
             *
             * @param groupId   要加入的群的id
             * @param groupName 要加入的群的名称
             * @param inviter   邀请者
             * @param reason    邀请理由
             */
            @Override
            public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
                EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            }

            /**
             * 用户申请加入群组
             *
             * @param groupId   要加入的群的id
             * @param groupName 要加入的群的名称
             * @param applyer   申请人的username
             * @param reason    申请加入的reason
             */
            @Override
            public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

            }

            /**
             * 加群申请被对方接受
             *
             * @param groupId 申请加入的群组id
             * @param groupName 申请加入的群组名称
             * @param accepter 同意申请的用户名（一般就是群主）
             */
            @Override
            public void onApplicationAccept(String groupId, String groupName, String accepter) {

            }

            /**
             * 加群申请被拒绝
             *
             * @param groupId 申请加入的群组id
             * @param groupName 申请加入的群组名称
             * @param decliner 拒绝者的用户名（一般就是群主）
             * @param reason 拒绝理由
             */
            @Override
            public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {

            }

            /**
             * 对方接受群组邀请
             *
             * @param groupId 邀请对方加入的群组
             * @param invitee 被邀请者
             * @param reason 理由
             */
            @Override
            public void onInvitationAccpted(String groupId, String invitee, String reason) {

            }

            /**
             * 对方拒绝群组邀请
             * @param groupId 邀请对方加入的群组
             * @param invitee 被邀请的人（拒绝群组邀请的人）
             * @param reason 拒绝理由
             */
            @Override
            public void onInvitationDeclined(String groupId, String invitee, String reason) {

            }

            /**
             * 当前登录用户被管理员移除出群组
             *
             * @param groupId 被移出的群组id
             * @param groupName 被移出的群组名称
             */
            @Override
            public void onUserRemoved(String groupId, String groupName) {

            }

            /**
             * 群组被解散。 sdk 会先删除本地的这个群组，之后通过此回调通知应用，此群组被删除了
             *
             * @param groupId 解散的群组id
             * @param groupName 解散的群组名称
             */
            @Override
            public void onGroupDestroy(String groupId, String groupName) {

            }


            /**
             * 自动同意加入群组 sdk会先加入这个群组，并通过此回调通知应用
             *
             * @param groupId 收到邀请加入的群组id
             * @param inviter 邀请者
             * @param inviteMessage 邀请信息
             */
            @Override
            public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

            }
        };
        // 添加群组改变监听
        EMClient.getInstance().groupManager().addGroupChangeListener(mGroupChangeListener);
    }

    /**
     * 退出登录环信
     *
     * @param callback 退出登录的回调函数，用来给上次回调退出状态
     */
    public void signOut(final EMCallBack callback) {
        resetApp();
        /**
         * 调用sdk的退出登录方法，此方法需要两个参数
         * boolean 第一个是必须的，表示要解绑Token，如果离线状态这个参数要设置为false
         * callback 可选参数，用来接收推出的登录的结果
         */
        EMClient.getInstance().logout(isUnbuildToken, new EMCallBack() {
            @Override
            public void onSuccess() {
                isUnbuildToken = true;
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                isUnbuildToken = true;
                if (callback != null) {
                    callback.onError(i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                if (callback != null) {
                    callback.onProgress(i, s);
                }
            }
        });
    }

    /**
     * 重置app操作，主要是在退出登录时清除内存
     */
    private void resetApp() {
        MLDBHelper.getInstance(mContext).resetDBHelper();
    }

    /**
     * 判断是否登录成功过，并且没有调用logout和被踢
     *
     * @return 返回一个boolean值 表示是否登录成功过
     */
    public boolean isLoginedInBefore() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 判断当前app是否连接聊天服务器
     *
     * @return 返回连接服务器状态
     */
    public boolean isConnection() {
        return EMClient.getInstance().isConnected();
    }

    /**
     * 根据Pid获取当前进程的名字，一般就是当前app的包名
     *
     * @param pid 进程的id
     * @return 返回进程的名字
     */
    private String getAppName(int pid) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    processName = info.processName;
                    // 返回当前进程名
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }

    /**
     * 获取当前运行启动的 activity 的列表
     *
     * @return 返回保存列表
     */
    public List<MLBaseActivity> getActivityList() {
        return mActivityList;
    }


    /**
     * 获取当前运行的 activity
     *
     * @return 返回当前活动的activity
     */
    public MLBaseActivity getTopActivity() {
        if (mActivityList.size() > 0) {
            return mActivityList.get(0);
        }
        return null;
    }

    /**
     * 添加当前activity到集合
     *
     * @param activity 需要添加的 activity
     */
    public void addActivity(MLBaseActivity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(0, activity);
        }
    }

    /**
     * 从 Activity 运行列表移除当前要退出的 activity
     *
     * @param activity 要移除的 activity
     */
    public void removeActivity(MLBaseActivity activity) {
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }
}
