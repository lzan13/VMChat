package net.melove.demo.chat.application;

import android.content.Context;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import net.melove.demo.chat.util.MLSPUtil;

/**
 * Created by lzan13 on 2015/7/13.
 */
public class MLEasemobHelper {

    private Context mContext;

    private static MLEasemobHelper instance;

    private MLEasemobOptions mOptions;

    // 记录sdk是否初始化
    private boolean isInit;

    private EMConnectionListener mConnectionListener;
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
        mOptions = new MLEasemobOptions();
        mOptions.initOption();

        // 初始化监听
        initListener();

        // 促使话完成
        isInit = true;
        return isInit;
    }


    /**
     * 初始化全局监听
     */
    public void initListener() {
        // 初始化消息监听
        initMessageListener();

        initGroupListener();
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
     * 初始化群组监听
     */
    protected void initGroupListener() {

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

}
