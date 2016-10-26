package net.melove.app.chat.ui.chat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import java.util.List;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.MLHyphenate;
import net.melove.app.chat.event.MLMessageEvent;
import net.melove.app.chat.notification.MLNotifier;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2016/10/26.
 * 消息监听实现类
 */

public class MLMessageListener implements EMMessageListener {
    /**
     * 收到新消息，离线消息也都是在这里获取
     * 这里在处理消息监听时根据收到的消息修改了会话对象的最后时间，是为了在会话列表中当清空了会话内容时，
     * 不用过滤掉空会话，并且能显示会话时间
     * {@link MLConversationExtUtils#setConversationLastTime(EMConversation)}
     *
     * @param list 收到的新消息集合，离线和在线都是走这个监听
     */
    @Override public void onMessageReceived(List<EMMessage> list) {
        // 判断当前活动界面是不是聊天界面，如果是，全局不处理消息
        if (MLHyphenate.getInstance().getActivityList().size() > 0) {
            if (MLHyphenate.getInstance()
                    .getTopActivity()
                    .getClass()
                    .getSimpleName()
                    .equals("MLChatActivity")) {
                return;
            }
        }
        // 遍历消息集合
        for (EMMessage message : list) {
            // 更新会话时间
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                MLConversationExtUtils.setConversationLastTime(
                        EMClient.getInstance().chatManager().getConversation(message.getFrom()));
            } else {
                MLConversationExtUtils.setConversationLastTime(
                        EMClient.getInstance().chatManager().getConversation(message.getTo()));
            }
            // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
            MLMessageEvent event = new MLMessageEvent();
            event.setMessage(message);
            event.setStatus(message.status());
            EventBus.getDefault().post(event);
        }
        if (list.size() > 1) {
            // 收到多条新消息，发送一条消息集合的通知
            MLNotifier.getInstance().sendNotificationMessageList(list);
        } else {
            // 只有一条消息，发送单条消息的通知
            MLNotifier.getInstance().sendNotificationMessage(list.get(0));
        }
    }

    /**
     * 收到新的 CMD 消息
     *
     * @param list 收到的透传消息集合
     */
    @Override public void onCmdMessageReceived(List<EMMessage> list) {
        // 判断当前活动界面是不是聊天界面，如果是，全局不处理消息
        if (MLHyphenate.getInstance().getActivityList().size() > 0) {
            if (MLHyphenate.getInstance()
                    .getTopActivity()
                    .getClass()
                    .getSimpleName()
                    .equals("MLChatActivity")) {
                return;
            }
        }
        for (EMMessage cmdMessage : list) {
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();

            // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
            MLMessageEvent event = new MLMessageEvent();
            event.setMessage(cmdMessage);
            event.setStatus(cmdMessage.status());
            EventBus.getDefault().post(event);

            // 判断是不是撤回消息的透传
            if (body.action().equals(MLConstants.ML_ATTR_RECALL)) {
                MLMessageUtils.receiveRecallMessage(cmdMessage);
            }
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
    @Override public void onMessageReadAckReceived(List<EMMessage> list) {

    }

    /**
     * 收到新的发送回执
     *
     * @param list 收到发送回执的消息集合
     */
    @Override public void onMessageDeliveryAckReceived(List<EMMessage> list) {

    }

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object 包含改变的消息
     */
    @Override public void onMessageChanged(EMMessage message, Object object) {
    }
};