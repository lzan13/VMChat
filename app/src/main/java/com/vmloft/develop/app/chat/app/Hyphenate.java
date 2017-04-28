package com.vmloft.develop.app.chat.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import com.vmloft.develop.app.chat.call.CallReceiver;
import com.vmloft.develop.app.chat.call.CallStateListener;
import com.vmloft.develop.app.chat.call.CallManager;
import com.vmloft.develop.app.chat.chat.MessageListener;
import com.vmloft.develop.app.chat.connection.ConnectionListener;
import com.vmloft.develop.app.chat.contacts.ContactsListener;
import com.vmloft.develop.app.chat.contacts.UserEntity;
import com.vmloft.develop.app.chat.database.DBHelper;
import com.vmloft.develop.app.chat.database.UserDao;
import com.vmloft.develop.app.chat.group.GroupListener;
import com.vmloft.develop.library.tools.utils.VMLog;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/13.
 * 自定义初始化类做一些环信sdk的初始化操作
 */
public class Hyphenate {

    // 上下文对象
    private Context context;

    // Hyphenate 单例对象
    private static Hyphenate instance;

    // 保存当前运行的 activity 对象，可用来判断程序是否处于前台，以及完全退出app等操作
    private List<AppActivity> activityList = new ArrayList<AppActivity>();

    private Map<String, UserEntity> userMap = new HashMap<String, UserEntity>();

    // 记录sdk是否初始化
    private boolean isInit;

    // 通话广播监听器
    private CallReceiver callReceiver = null;
    // 通话状态监听
    private CallStateListener callStateListener;
    // 环信连接监听
    private ConnectionListener connectionListener;
    // 环信的消息监听器
    private MessageListener messageListener;
    // 环信联系人监听
    private ContactsListener contactListener;
    // 环信群组变化监听
    private GroupListener groupChangeListener;

    // 表示是是否解绑Token，一般离线状态都要设置为false
    public boolean unBuildToken = true;

    /**
     * 单例类，用来初始化环信的sdk
     *
     * @return 返回当前类的实例
     */
    public static Hyphenate getInstance() {
        if (instance == null) {
            instance = new Hyphenate();
        }
        return instance;
    }

    /**
     * 私有的构造方法
     */
    private Hyphenate() {
    }

    /**
     * 初始化环信的SDK
     *
     * @param context 上下文菜单
     * @return 返回初始化状态是否成功
     */
    public synchronized boolean initHyphenate(Context context) {
        VMLog.d("SDK init start -----");
        this.context = context;
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
        this.context = context;

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(this.context, initOptions());

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // 通话管理类的初始化
        CallManager.getInstance().init(context);

        // 初始化全局监听
        initGlobalListener();

        // 初始化完成
        isInit = true;
        VMLog.d("SDK init end =====");
        return isInit;
    }

    /**
     * SDK 相关配置
     */
    private EMOptions initOptions() {
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();

        // 是否启动 DNS 信息配置，如果是私有化部署，这里要设置为 false
        options.enableDNSConfig(true);
        // 设置私有化 IM 地址
        //options.setIMServer("im1.easemob.com");
        // 设置私有化 IM 端口号
        //options.setImPort(443);
        // 设置私有化 Rest 地址+端口号
        //options.setRestServer("a1.easemob.com:80");
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        //options.setAppKey("fandou#knightepal");

        // 设置是否使用 https
        options.setUseHttps(false);
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否按照服务器时间排序，false按照本地时间排序，默认 true
        options.setSortMessageByServerTime(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(false);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(true);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);

        // Google 推送 Project Number
        options.setGCMNumber(Constants.GCM_NUMBER);
        // 华为推送 AppId
        options.setHuaweiPushAppId(Constants.HW_APP_ID);
        // 小米推送 AppId
        options.setMipushConfig(Constants.MI_APP_ID, Constants.MI_APP_KEY);

        return options;
    }

    /**
     * 初始化全局监听，其中包括：
     * 连接监听     {@link #registerConnectionListener()}
     * 消息监听     {@link #registerMessageListener()}
     * 联系人监听   {@link #registerContactListener()}
     * 群组监听     {@link #registerGroupListener()}
     */
    public void initGlobalListener() {
        // 注册通话广播监听
        registerCallReceiverListener();
        // 注册全局的连接监听
        registerConnectionListener();
        // 注册全局消息监听
        registerMessageListener();
        // 注册全局的联系人监听
        registerContactListener();
        // 注册全局的群组变化监听
        registerGroupListener();
    }

    /**
     * 设置通话广播监听
     */
    private void registerCallReceiverListener() {
        // 设置通话广播监听器过滤内容
        IntentFilter callFilter = new IntentFilter(
                EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        context.registerReceiver(callReceiver, callFilter);
    }

    /**
     * 注册链接监听，监听与服务器连接状况
     * 详细实现见{@link ConnectionListener}
     */
    private void registerConnectionListener() {
        connectionListener = new ConnectionListener();
        EMClient.getInstance().addConnectionListener(connectionListener);
    }

    /**
     * 注册全局的消息监听
     * 监听回调详细实现见{@link MessageListener}
     */
    protected void registerMessageListener() {
        messageListener = new MessageListener();
        // 注册消息监听
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * 注册联系人监听，用来监听联系人的请求与变化等
     * 详细实现见{@link ContactsListener}
     */
    private void registerContactListener() {
        if (contactListener == null) {
            contactListener = new ContactsListener(context);
        }
        // 设置联系人变化监听
        EMClient.getInstance().contactManager().setContactListener(contactListener);
    }

    /**
     * 注册群组变化监听，用来监听群组相关变化回调
     * 详细实现见{@link GroupListener}
     */
    private void registerGroupListener() {
        if (groupChangeListener == null) {
            groupChangeListener = new GroupListener();
        }
        // 添加群组改变监听
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
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
        EMClient.getInstance().logout(unBuildToken, new EMCallBack() {
            @Override public void onSuccess() {
                unBuildToken = true;
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override public void onError(int i, String s) {
                unBuildToken = true;
                if (callback != null) {
                    callback.onError(i, s);
                }
            }

            @Override public void onProgress(int i, String s) {
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
        UserDao.getInstance().resetUserDao();
        DBHelper.getInstance().resetDBHelper();
    }

    /**
     * 根据Pid获取当前进程的名字，一般就是当前app的包名
     *
     * @param pid 进程的id
     * @return 返回进程的名字
     */
    private String getAppName(int pid) {
        String processName = null;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info =
                    (ActivityManager.RunningAppProcessInfo) (i.next());
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
    public List<AppActivity> getActivityList() {
        return activityList;
    }

    /**
     * 获取当前运行的 activity
     *
     * @return 返回当前活动的activity
     */
    public AppActivity getTopActivity() {
        if (activityList.size() > 0) {
            return activityList.get(0);
        }
        return null;
    }

    /**
     * 添加当前activity到集合
     *
     * @param activity 需要添加的 activity
     */
    public void addActivity(AppActivity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    /**
     * 从 Activity 运行列表移除当前要退出的 activity
     *
     * @param activity 要移除的 activity
     */
    public void removeActivity(AppActivity activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }
}
