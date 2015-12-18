package net.melove.demo.chat.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import net.melove.demo.chat.util.MLSPUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/13.
 */
public class MLEasemobHelper {

    private Context mContext;

    private static MLEasemobHelper instance;

    // 记录sdk是否初始化
    private boolean isInit;

    private EMEventListener mEventListener;


    public static MLEasemobHelper getInstance() {
        if (instance == null) {
            instance = new MLEasemobHelper();
        }
        return instance;
    }

    protected MLEasemobHelper() {

    }

    public synchronized boolean onInit(Context context) {
        mContext = context;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return true;
        }
        if (isInit) {
            return isInit;
        }
        mContext = context;

        // 调用初始化方法初始化sdk
        EMChat.getInstance().init(mContext);

        // 设置自动登录
        EMChat.getInstance().setAutoLogin(true);

        // 设置开启debug模式
        EMChat.getInstance().setDebugMode(true);

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
     * 初始化消息监听
     */
    protected void initMessageListener() {
        mEventListener = new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                }
                switch (event.getEvent()) {
                    case EventNewMessage:

                        break;
                    case EventOfflineMessage:

                        break;
                }
            }
        };
        EMChatManager.getInstance().registerEventListener(mEventListener);
    }

    /**
     * 退出登录环信
     *
     * @param callback
     */
    public void signOut(final EMCallBack callback) {
        MLSPUtil.remove(mContext, MLConstants.ML_C_USERNAME);
        MLSPUtil.remove(mContext, MLConstants.ML_C_PASSWORD);
        EMChatManager.getInstance().logout(new EMCallBack() {
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

    public boolean isLogined() {
        return EMChat.getInstance().isLoggedIn();
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        PackageManager pm = mContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
