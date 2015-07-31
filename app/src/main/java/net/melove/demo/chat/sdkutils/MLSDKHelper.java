package net.melove.demo.chat.sdkutils;

import android.content.Context;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/13.
 */
public class MLSDKHelper {

    private Context mContext;

    private static MLSDKHelper instance;

    // 记录sdk是否初始化
    private boolean isInit;

    private EMConnectionListener mConnectionListener;
    private EMEventListener mEventListener;


    public static MLSDKHelper getInstance() {
        if (instance == null) {
            instance = new MLSDKHelper();
        }
        return instance;
    }

    protected MLSDKHelper() {

    }

    public synchronized boolean onInit(Context context) {
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
        MLSDKOptions.getInstance().initOption();
        // 设置全局监听
        initListener();

        // 促使话完成
        isInit = true;
        return isInit;
    }


    public void initListener() {
        initConnectionListener();
        //
        initMessageListener();
    }

    /**
     * 初始化链接监听
     */
    protected void initConnectionListener() {
        mConnectionListener = new EMConnectionListener() {
            @Override
            public void onConnected() {
                MLToast.makeToast("链接成功").show();
            }

            @Override
            public void onDisconnected(int i) {
                MLToast.makeToast("链接断开 " + i).show();
            }
        };
        EMChatManager.getInstance().addConnectionListener(mConnectionListener);
    }

    /**
     * 初始化消息监听
     */
    protected void initMessageListener() {
        mEventListener = new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;

            }
        };
    }

    public void signOut(final EMCallBack callback) {
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

}
