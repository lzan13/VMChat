package net.melove.demo.chat.sdkutils;

import android.content.Context;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

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

        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 设置初始化数据库DB时，每个会话要加载的Message数量
        options.setNumberOfMessagesLoaded(1);
        // 设置是否使用环信的好友体系
        options.setUseRoster(true);
        // 添加好友是否需要验证，SDK默认是不需要
        options.setAcceptInvitationAlways(true);

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
