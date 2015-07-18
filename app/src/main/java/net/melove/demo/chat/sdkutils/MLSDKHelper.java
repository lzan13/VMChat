package net.melove.demo.chat.sdkutils;

import android.content.Context;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/13.
 */
public class MLSDKHelper {

    private Context mContext;

    private static MLSDKHelper instance;

    private boolean isInit;


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

        initSDKOption();

        isInit = true;
        return isInit;
    }


    private void initSDKOption() {
        // 设置自动登录
        EMChat.getInstance().setAutoLogin(true);

        // 设置开启debug模式
        EMChat.getInstance().setDebugMode(true);

    }


    private void signOut() {
        EMChatManager.getInstance().logout(new EMCallBack() {
            @Override
            public void onSuccess() {
                MLToast.makeToast("Sign Out Success!").show();
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

}
