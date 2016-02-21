package net.melove.demo.chat.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import net.melove.demo.chat.util.MLDate;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.util.MLSPUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/13.
 */
public class MLEasemobHelper {

    // 上下文对象
    private Context mContext;

    // MLEasemobHelper 单例对象
    private static MLEasemobHelper instance;

    // 记录sdk是否初始化
    private boolean isInit;

    private List<Activity> mActivityList = new ArrayList<Activity>();


    /**
     * 单例类，用来初始化环信的sdk
     *
     * @return
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
     * @param context
     * @return 返回初始化状态是否成功
     */
    public synchronized boolean initEasemob(Context context) {
        mContext = context;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            // 则此application的onCreate 是被service 调用的，直接返回
            return true;
        }
        if (isInit) {
            return isInit;
        }
        mContext = context;

        // SDK初始化的一些配置
        EMOptions options = new EMOptions();
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 设置初始化数据库DB时，每个会话要加载的Message数量
        options.setNumberOfMessagesLoaded(5);
        // 添加好友是否自动同意，如果是自动同意就不会收到好友请求，因为sdk会自动处理
        options.setAcceptInvitationAlways(false);
        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(mContext, options);

        String APP_ID = "2882303761517430984";
        String APP_KEY = "5191743065984";
        EMClient.getInstance().setMipushConfig(APP_ID, APP_KEY);

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // 初始化sdk的一些设置
        MLEasemobOptions options = new MLEasemobOptions();
        options.initOption();

        // 初始化全局监听
        initGlobalListener();

        // 初始化完成
        isInit = true;
        return isInit;
    }


    /**
     * 初始化环信的一些监听
     */
    public void initGlobalListener() {
        // 初始化全局消息监听
        initMessageListener();
    }

    /**
     * 初始化全局的消息监听
     */
    protected void initMessageListener() {
        mEventListener = new EMEventListener() {
            EMMessage cmdOffline;

            @Override
            public void onEvent(EMNotifierEvent event) {
                switch (event.getEvent()) {
                    case EventNewMessage:
                        // 正常的新消息，包含：Txt、Image、File、Location、Voice、Video
                        EMMessage message = (EMMessage) event.getData();

                        MLLog.i("recall - EventNewMessage - msgId:%s", message.getMsgId());
                        break;
                    case EventOfflineMessage:
                        // 离线消息，离线消息得到的数据是一个 list 集合，不是一个单一的EMMessage对象
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        for (EMMessage msg : messages) {
                            MLLog.d("recall - 2 msgId %s, body %s", msg.getMsgId(), msg.getBody());
                        }
                        receiveRecallMessage(cmdOffline);
                        MLLog.i("recall - 2 EventOfflineMessage");
                        break;
                    case EventNewCMDMessage:
                        // 透传消息
                        EMMessage cmdMessage = (EMMessage) event.getData();
                        CmdMessageBody body = (CmdMessageBody) cmdMessage.getBody();
                        // 判断是不是撤回消息的透传
                        if (body.action.equals(MLConstants.ML_ATTR_TYPE_RECALL)) {
                            cmdOffline = cmdMessage;
                            MLLog.d("recall - 1 %s", cmdMessage.getStringAttribute(MLConstants.ML_ATTR_MSG_ID, ""));
                            receiveRecallMessage(cmdMessage);
                        }
                        MLLog.i("recall - 1 EventNewCMDMessage");
                        break;
                    case EventReadAck:
                        // 已读回执，表示对反已经查看了消息

                        MLLog.i("EventReadAck");
                        break;
                    case EventDeliveryAck:
                        // 发送回执，表示对方已经收到

                        MLLog.i("EventDeliveryAck");
                        break;
                }
            }
        };
        // 注册消息监听
        EMChatManager.getInstance().registerEventListener(mEventListener);
    }

    /**
     * 发送一条撤回消息的透传，这里需要和接收方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param message  需要撤回的消息
     * @param callBack 发送消息的回调，通知调用方发送撤回消息的结果
     */
    public void sendRecallMessage(final EMMessage message, final EMCallBack callBack) {
        boolean result = false;
        // 获取当前时间，用来判断后边撤回消息的时间点是否合法，这个判断不需要在接收方做，
        // 因为如果接收方之前不在线，很久之后才收到消息，将导致撤回失败
        long currTime = MLDate.getCurrentMillisecond();
        long msgTime = message.getMsgTime();
        if (currTime - msgTime > 120000) {
            callBack.onError(0, "time");
            return;
        }
        String msgId = message.getMsgId();
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            cmdMessage.setChatType(EMMessage.ChatType.GroupChat);
        }
        cmdMessage.setReceipt(message.getTo());
        // 创建CMD 消息的消息体 并设置 action 为 recall
        CmdMessageBody body = new CmdMessageBody(MLConstants.ML_ATTR_TYPE_RECALL);
        cmdMessage.addBody(body);
        cmdMessage.setAttribute(MLConstants.ML_ATTR_MSG_ID, msgId);
        // 确认无误，开始发送撤回消息的透传
        EMChatManager.getInstance().sendMessage(cmdMessage, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 更改要撤销的消息的内容，替换为消息已经撤销的提示内容
                TextMessageBody body = new TextMessageBody("此条消息已撤回");
                message.addBody(body);
                // 这里需要把消息类型改为 TXT 类型
                message.setType(EMMessage.Type.TXT);
                // 设置扩展为撤回消息类型，是为了区分消息的显示
                message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_ATTR_TYPE_RECALL);
                // 返回修改消息结果
                EMChatManager.getInstance().updateMessageBody(message);
                callBack.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i, s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 收到撤回消息，这里需要和发送方协商定义，通过一个透传，并加上扩展去实现消息的撤回
     *
     * @param recallMsg 收到的透传消息，包含需要撤回的消息的 msgId
     * @return 返回撤回结果是否成功
     */
    public boolean receiveRecallMessage(EMMessage recallMsg) {
        boolean result = false;
        // 从cmd扩展中获取要撤回消息的id
        String msgId = recallMsg.getStringAttribute(MLConstants.ML_ATTR_MSG_ID, null);
        if (msgId == null) {
            MLLog.d("recall - 3 %s", msgId);
            return result;
        }
        // 根据得到的msgId 去本地查找这条消息，如果本地已经没有这条消息了，就不用撤回
        EMMessage message = EMChatManager.getInstance().getMessage(msgId);
        if (message == null) {
            MLLog.d("recall - 3 message is null %s", msgId);
            return result;
        }
        // 更改要撤销的消息的内容，替换为消息已经撤销的提示内容
        TextMessageBody body = new TextMessageBody("此条消息已被 " + message.getUserName() + " 撤回");
        message.addBody(body);
        // 这里需要把消息类型改为 TXT 类型
        message.setType(EMMessage.Type.TXT);
        // 设置扩展为撤回消息类型，是为了区分消息的显示
        message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_ATTR_TYPE_RECALL);
        // 返回修改消息结果
        result = EMChatManager.getInstance().updateMessageBody(message);
        return result;
    }

    /**
     * 退出登录环信
     *
     * @param callback 退出登录的回调函数，用来给上次回调退出状态
     */
    public void signOut(final EMCallBack callback) {
        MLSPUtil.remove(mContext, MLConstants.ML_SHARED_USERNAME);
        MLSPUtil.remove(mContext, MLConstants.ML_SHARED_PASSWORD);
        EMChatManager.getInstance().logout(true, new EMCallBack() {
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
     * 判断是否登录成功过
     *
     * @return 返回一个boolean值 表示是否登录成功过
     */
    public boolean isLogined() {
        return EMChat.getInstance().isLoggedIn();
    }

    /**
     * 添加 Activity 到集合，为了给全局监听用来判断当前是否在 Activity 界面
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(0, activity);
        }
    }

    /**
     * 将 Activity 从集合中移除
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
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
        PackageManager pm = mContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                MLLog.e(e.toString());
            }
        }
        return processName;
    }

}
