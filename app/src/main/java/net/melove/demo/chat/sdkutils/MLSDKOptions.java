package net.melove.demo.chat.sdkutils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

/**
 * Created by lzan13 on 2015/7/13.
 */
public class MLSDKOptions {

    private static MLSDKOptions instance;

    private MLSDKOptions() {

    }

    public static MLSDKOptions getInstance() {
        if (instance == null) {
            instance = new MLSDKOptions();
        }
        return instance;
    }

    public void initOption() {

        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 设置是否需要发送已读回执
        options.setRequireAck(false);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(false);
        // 设置初始化数据库DB时，每个会话要加载的Message数量
        options.setNumberOfMessagesLoaded(1);
        // 设置是否使用环信的好友体系
        options.setUseRoster(false);
        // 添加好友是否需要验证，SDK默认是不需要
        options.setAcceptInvitationAlways(true);

    }
}
