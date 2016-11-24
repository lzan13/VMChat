package net.melove.app.chat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.HashMap;
import java.util.Map;
import net.melove.app.chat.module.database.MLUserDao;
import net.melove.app.chat.module.listener.MLConnectionListener;
import net.melove.app.chat.ui.MLBaseActivity;
import net.melove.app.chat.module.listener.MLMessageListener;
import net.melove.app.chat.module.listener.MLCallStateListener;
import net.melove.app.chat.module.listener.MLContactsListener;
import net.melove.app.chat.module.listener.MLGroupListener;
import net.melove.app.chat.ui.contacts.MLUserEntity;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.ui.call.MLCallReceiver;
import net.melove.app.chat.module.database.MLDBHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/13.
 * 自定义初始化类做一些环信sdk的初始化操作
 */
public class MLHyphenate {

    // 上下文对象
    private Context mContext;

    // MLHyphenate 单例对象
    private static MLHyphenate instance;

    // 保存当前运行的 activity 对象，可用来判断程序是否处于前台，以及完全退出app等操作
    private List<MLBaseActivity> mActivityList = new ArrayList<MLBaseActivity>();

    private Map<String, MLUserEntity> userMap = new HashMap<String, MLUserEntity>();

    // 记录sdk是否初始化
    private boolean isInit;

    // 通话广播监听器
    private MLCallReceiver mCallReceiver = null;
    // 通话状态监听
    private MLCallStateListener callStateListener;
    // 环信连接监听
    private MLConnectionListener mConnectionListener;
    // 环信的消息监听器
    private MLMessageListener mMessageListener;
    // 环信联系人监听
    private MLContactsListener mContactListener;
    // 环信群组变化监听
    private MLGroupListener mGroupChangeListener;

    // 表示是是否解绑Token，一般离线状态都要设置为false
    public boolean unBuildToken = true;

    /**
     * 单例类，用来初始化环信的sdk
     *
     * @return 返回当前类的实例
     */
    public static MLHyphenate getInstance() {
        if (instance == null) {
            instance = new MLHyphenate();
        }
        return instance;
    }

    /**
     * 私有的构造方法
     */
    private MLHyphenate() {
    }

    /**
     * 初始化环信的SDK
     *
     * @param context 上下文菜单
     * @return 返回初始化状态是否成功
     */
    public synchronized boolean initHyphenate(Context context) {
        MLLog.d("SDK init start -----");
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

        // 初始化通话相关设置
        initCallOptions();

        // 初始化全局监听
        initGlobalListener();

        // 初始化完成
        isInit = true;
        MLLog.d("SDK init end =====");
        return isInit;
    }

    /**
     * SDK 3.2.0 版本通话相关设置
     */
    private void initCallOptions() {
        // 设置视频通话比特率 默认是(150)
        EMClient.getInstance().callManager().getCallOptions().setVideoKbps(800);
        // 设置视频通话分辨率 默认是(320, 240)
        EMClient.getInstance().callManager().getCallOptions().setVideoResolution(640, 480);
        // 设置通话过程中对方如果离线是否发送离线推送通知
        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(false);
        // 设置音视频通话采样率
        //EMClient.getInstance().callManager().getCallOptions().
    }

    private EMOptions initOptions() {
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();
        // 是否启动 DNS 信息配置
        options.enableDNSConfig(true);
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        options.setAppKey("nixiwangluo#boostaging");
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
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(false);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(true);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);

        // Google GCM 推送 number
        options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
        // 华为推送 AppId
        options.setHuaweiPushAppId(MLConstants.ML_HUAWEI_APP_ID);
        // 小米推送 AppId
        options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

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
        if (mCallReceiver == null) {
            mCallReceiver = new MLCallReceiver();
        }
        //注册通话广播接收者
        mContext.registerReceiver(mCallReceiver, callFilter);
    }

    /**
     * 设置通话状态监听，监听通话状态，处理界面显示
     * 状态监听详细实现在{@link MLCallStateListener}
     */
    public void registerCallStateListener() {
        if (callStateListener == null) {
            callStateListener = new MLCallStateListener();
        }
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    /**
     * 删除通话状态监听
     */
    public void removeCallStateListener() {
        if (callStateListener != null) {
            EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
            callStateListener = null;
        }
    }

    /**
     * 注册链接监听，监听与服务器连接状况
     * 详细实现见{@link MLConnectionListener}
     */
    private void registerConnectionListener() {
        mConnectionListener = new MLConnectionListener();
        EMClient.getInstance().addConnectionListener(mConnectionListener);
    }

    /**
     * 注册全局的消息监听
     * 监听回调详细实现见{@link MLMessageListener}
     */
    protected void registerMessageListener() {
        mMessageListener = new MLMessageListener();
        // 注册消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    /**
     * 注册联系人监听，用来监听联系人的请求与变化等
     * 详细实现见{@link MLContactsListener}
     */
    private void registerContactListener() {
        if (mContactListener == null) {
            mContactListener = new MLContactsListener(mContext);
        }
        // 设置联系人变化监听
        EMClient.getInstance().contactManager().setContactListener(mContactListener);
    }

    /**
     * 注册群组变化监听，用来监听群组相关变化回调
     * 详细实现见{@link MLGroupListener}
     */
    private void registerGroupListener() {
        if (mGroupChangeListener == null) {
            mGroupChangeListener = new MLGroupListener();
        }
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
        MLDBHelper.getInstance(mContext).resetDBHelper();
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
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
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

    /**
     * 保存联系人列表
     */
    public void saveUserList(List<MLUserEntity> list) {
        MLUserDao.getInstance().saveUserList(list);
    }

    /**
     * 获取用户列表，这里为了省去每次都去数据库读取，将联系人保存在内存的一个集合中
     *
     * @return 返回用户列表
     */
    public Map<String, MLUserEntity> getUserList() {
        if (userMap.isEmpty()) {
            userMap = MLUserDao.getInstance().getUserList();
        }
        return userMap;
    }

    /**
     * 根据用户名获取用户对象
     *
     * @param username 需要获取的用户名
     * @return 返回获取到的用户对象
     */
    public MLUserEntity getUser(String username) {
        MLUserEntity userEntity = null;
        if (!userMap.isEmpty()) {
            userEntity = userMap.get(username);
        }
        if (userEntity == null) {
            userEntity = MLUserDao.getInstance().getUser(username);
        }
        return userEntity;
    }

    /**
     * 保存用户
     *
     * @param userEntity 需要保存的用户对象
     */
    public void saveUser(MLUserEntity userEntity) {
        if (userMap != null) {
            userMap.put(userEntity.getUserName(), userEntity);
        }
        MLUserDao.getInstance().saveUser(userEntity);
    }

    /**
     * 删除用户
     *
     * @param userEntity 要删除的用户对象
     */
    public void deleteUser(MLUserEntity userEntity) {
        if (userMap != null) {
            userMap.remove(userEntity);
        }
        MLUserDao.getInstance().deleteUser(userEntity.getUserName());
    }
}
