package net.melove.demo.chat.application;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

/**
 * by lzan13 on 2015/7/13.
 * SDK初始化设置类
 */
public class MLEasemobOptions {

    /**
     * 环信SDK的一些初始化设置
     */
    public void initOption() {
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 设置初始化数据库DB时，每个会话要加载的Message数量
        options.setNumberOfMessagesLoaded(5);
        // 设置是否使用环信的好友体系
        options.setUseRoster(false);
        // 添加好友是否自动同意，如果是自动同意就不会收到好友请求，因为sdk会自动处理
        options.setAcceptInvitationAlways(false);

    }
}
