package net.melove.app.easechat.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;

import net.melove.app.easechat.R;
import net.melove.app.easechat.communal.util.MLCrypto;
import net.melove.app.easechat.communal.util.MLDate;
import net.melove.app.easechat.communal.util.MLLog;
import net.melove.app.easechat.communal.util.MLMessageUtils;
import net.melove.app.easechat.database.MLDBHelper;
import net.melove.app.easechat.invited.MLInvitedEntity;
import net.melove.app.easechat.contacts.MLContactsEntity;
import net.melove.app.easechat.conversation.MLConversationExtUtils;
import net.melove.app.easechat.database.MLInvitedDao;
import net.melove.app.easechat.database.MLContactsDao;
import net.melove.app.easechat.main.MLConflictActivity;
import net.melove.app.easechat.notification.MLNotifier;

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

    // 记录sdk是否初始化
    private boolean isInit;

    // 环信的消息监听器
    private EMMessageListener mMessageListener;

    // 环信联系人监听
    private EMContactListener mContactListener;
    // 环信连接监听
    private EMConnectionListener mConnectionListener;
    // 环信群组变化监听
    private EMGroupChangeListener mGroupChangeListener;

    // 表示是否登录成功状态，如果使用了推送，退出时需要要根据这个状态去传递参数
    private boolean isLogined = true;

    // App内广播管理器，为了安全，这里使用本地局域广播
    private LocalBroadcastManager mLocalBroadcastManager;

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

        // 获取App内广播接收器实例
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);

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

        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        // options.setAppKey("lzan13#hxsdkdemo");
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执，TODO 这个暂时有bug
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
        //        options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
        // 设置集成小米推送的appid和appkey
        // options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(mContext, options);

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // 初始化全局监听
        initGlobalListener();

        // 初始化完成
        isInit = true;
        MLLog.d("------- init easemob end --------------");
        return isInit;
    }


    /**
     * 初始化环信的一些监听
     */
    public void initGlobalListener() {
        MLLog.d("------- listener start --------------");
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
                MLLog.d("MLEasemobHelper - onConnected");
                // 发送app服务器链接变化的广播
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_CONNCETION));
            }

            /**
             * 链接聊天服务器失败
             *
             * @param errorCode 连接失败错误码
             */
            @Override
            public void onDisconnected(final int errorCode) {
                MLLog.d("MLEasemobHelper - onDisconnected - %d", errorCode);
                Activity activity = MLActivityManager.getInstance().getCurrActivity();
                Intent intent = new Intent();
                if (activity != null) {
                    intent.setClass(activity, MLConflictActivity.class);
                }
                if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    MLLog.d("user login another device - " + errorCode);
                    // 被踢了，已经登录成功的状态要改为 false，这个在使用了推送功能时，调用logout需要传递
                    isLogined = false;
                    signOut(null);
                    intent.putExtra(MLConstants.ML_EXTRA_USER_LOGIN_OTHER_DIVERS, true);
                    activity.startActivity(intent);
                } else if (errorCode == EMError.USER_REMOVED) {
                    MLLog.d("user be removed - " + errorCode);
                    // 被踢了，已经登录成功的状态要改为 false，这个在使用了推送功能时，调用logout需要传递
                    isLogined = false;
                    signOut(null);
                    intent.putExtra(MLConstants.ML_EXTRA_USER_REMOVED, true);
                    mContext.startActivity(intent);
                } else {
                    MLLog.d("con't servers - " + errorCode);
                    // 发送app服务器链接变化的广播
                    mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_CONNCETION));
                }
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
             * @param list 收到的新消息集合 TODO 2016-4-15 19:35 经测试不论是离线还是在线list大小都是 1
             */
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                EMConversation conversation = null;
                for (EMMessage message : list) {
                    // 根据消息类型来获取回话对象
                    if (message.getChatType() == EMMessage.ChatType.Chat) {
                        conversation = EMClient.getInstance().chatManager().getConversation(message.getFrom());
                    } else {
                        conversation = EMClient.getInstance().chatManager().getConversation(message.getTo());
                    }
                    // 设置会话的最后时间
                    MLConversationExtUtils.setConversationLastTime(conversation);
                }
                // 收到新消息，发送一条通知
                MLNotifier.getInstance(MLApplication.getContext()).sendNotificationMessageList(list);
                // 发送广播，通知需要刷新UI等操作的地方
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_MESSAGE));
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
                    // 判断是不是撤回消息的透传
                    if (body.action().equals(MLConstants.ML_ATTR_RECALL)) {
                        MLMessageUtils.receiveRecallMessage(mContext, cmdMessage);
                    }
                }
                // 发送广播，通知需要刷新UI等操作的地方
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_MESSAGE));
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
                MLContactsEntity contacts = new MLContactsEntity();
                contacts.setUserName(username);
                /**
                 * 调用{@link MLContactsDao#saveContacts(MLContactsEntity)} 去保存联系人，
                 * 这里将{@link MLContactsDao} 封装成了单例类
                 */
                MLContactsDao.getInstance().saveContacts(contacts);
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
                // 创建一条好友申请数据
                MLInvitedEntity invitedEntity = new MLInvitedEntity();
                // 当前用户
                String currUsername = EMClient.getInstance().getCurrentUser();
                // 根据根据对方的名字，加上当前用户的名字，加申请类型按照一定顺序组合，得到当前申请信息的唯一 ID
                String invitedId = MLCrypto.cryptoStr2MD5(username + currUsername + MLInvitedEntity.InvitedType.CONTACTS);
                // 设置此条信息的唯一ID
                invitedEntity.setInvitedId(invitedId);
                // 对方的username
                invitedEntity.setUserName(username);
                // invitedEntity.setNickName(mUserEntity.getNickName());
                // 设置申请理由
                invitedEntity.setReason(reason);
                // 设置申请状态为被申请
                invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEAPPLYFOR);
                // 设置申请信息为联系人申请
                invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
                // 设置申请信息的时间
                invitedEntity.setTime(MLDate.getCurrentMillisecond());

                /**
                 * 这里先读取本地的申请与通知信息，如果相同则直接 return，不进行操作
                 * 只有当新的好友请求发过来时才进行保存，并发送通知
                 * 这里进行一下筛选，如果已存在则去更新本地内容
                 * 同样的{@link MLInvitedDao}也是一个单例的操作类，封装了一些对于申请与邀请信息的增删改查的方法
                 */
                MLInvitedEntity temp = MLInvitedDao.getInstance().getInvitedEntiry(invitedEntity.getInvitedId());
                if (temp != null) {
                    if (temp.getReason().equals(invitedEntity.getReason())) {
                        // 这里判断当前保存的信息如果和新的一模一样不进行操作
                        return;
                    }
                    // 此条信息已经存在，更新修改时间
                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
                } else {
                    MLInvitedDao.getInstance().saveInvited(invitedEntity);
                }
                // 调用发送通知栏提醒方法，提醒用户查看申请通知
                MLNotifier.getInstance(MLApplication.getContext()).sendInvitedNotification(invitedEntity);
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_INVITED));
                // 发送广播，通知需要刷新UI等操作的地方
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_CONTACT));
            }

            /**
             * 对方同意了自己的申请
             *
             * @param username 对方的username
             */
            @Override
            public void onContactAgreed(String username) {
                MLLog.d("onContactAgreed - username:%s", username);
                // 这里进行一下筛选，如果已存在则去更新本地内容
                String currUsername = EMClient.getInstance().getCurrentUser();
                // 根据申请者的名字，加上当前用户的名字，加申请类型按照一定顺序组合，得到当前申请信息的唯一 ID
                String invitedId = MLCrypto.cryptoStr2MD5(currUsername + username + MLInvitedEntity.InvitedType.CONTACTS);
                // 先去数据库查找一下，有没有这一条申请信息，如果有直接修改，没有才新建插入
                MLInvitedEntity invitedEntity = MLInvitedDao.getInstance().getInvitedEntiry(invitedId);
                if (invitedEntity != null) {
                    // 更新当前消息处理时间
                    invitedEntity.setTime(MLDate.getCurrentMillisecond());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEAGREED);
                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
                } else {
                    // 创建一条好友申请数据
                    invitedEntity = new MLInvitedEntity();
                    // 设置此条信息的唯一ID
                    invitedEntity.setInvitedId(invitedId);
                    // 对方的username
                    invitedEntity.setUserName(username);
                    // invitedEntity.setNickName(mUserEntity.getNickName());
                    invitedEntity.setReason(MLApplication.getContext().getString(R.string.ml_add_contact_reason));
                    // 设置申请状态为被同意
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEAGREED);
                    // 设置申请信息为联系人申请
                    invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
                    // 设置申请信息的时间
                    invitedEntity.setTime(MLDate.getCurrentMillisecond());
                    MLInvitedDao.getInstance().saveInvited(invitedEntity);
                }
                /**
                 * 调用发送通知栏提醒方法，提醒用户查看申请通知
                 * 这里是自定义封装号的一个类{@link MLNotifier}
                 */
                MLNotifier.getInstance(MLApplication.getContext()).sendInvitedNotification(invitedEntity);
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_INVITED));
            }

            /**
             * 对方拒绝了联系人申请
             *
             * @param username 对方的username
             */
            @Override
            public void onContactRefused(String username) {
                MLLog.d("onContactRefused - username:%s", username);
                // 这里进行一下筛选，如果已存在则去更新本地内容
                String currUsername = EMClient.getInstance().getCurrentUser();
                // 根据申请者的名字，加上当前用户的名字，加申请类型按照一定顺序组合，得到当前申请信息的唯一 ID
                String invitedId = MLCrypto.cryptoStr2MD5(currUsername + username + MLInvitedEntity.InvitedType.CONTACTS);
                // 先去数据库查找一下，有没有这一条申请信息，如果有直接修改，没有才新建插入
                MLInvitedEntity invitedEntity = MLInvitedDao.getInstance().getInvitedEntiry(invitedId);
                if (invitedEntity != null) {
                    // 更新当前信息的时间
                    invitedEntity.setTime(MLDate.getCurrentMillisecond());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEREFUSED);
                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
                } else {
                    // 创建一条好友申请数据
                    invitedEntity = new MLInvitedEntity();
                    // 设置此条信息的唯一ID
                    invitedEntity.setInvitedId(invitedId);
                    // 对方的username
                    invitedEntity.setUserName(username);
                    // invitedEntity.setNickName(mUserEntity.getNickName());
                    invitedEntity.setReason(MLApplication.getContext().getString(R.string.ml_add_contact_reason));
                    // 设置申请状态为被同意
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEREFUSED);
                    // 设置申请信息为联系人申请
                    invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
                    // 设置申请信息的时间
                    invitedEntity.setTime(MLDate.getCurrentMillisecond());

                    MLInvitedDao.getInstance().saveInvited(invitedEntity);
                }

                /**
                 * 调用发送通知栏提醒方法，提醒用户查看申请通知
                 * 这里是自定义封装号的一个类{@link MLNotifier}
                 */
                MLNotifier.getInstance(MLApplication.getContext()).sendInvitedNotification(invitedEntity);
                mLocalBroadcastManager.sendBroadcast(new Intent(MLConstants.ML_ACTION_INVITED));
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
         * boolean 第一个是必须的，表示是否使用了推送，要解绑推送，如果被踢这个参数要设置为false
         * callback 可选参数，用来接收推出的登录的结果
         */
        EMClient.getInstance().logout(isLogined, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
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
    public boolean isLogined() {
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

}
