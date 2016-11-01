package net.melove.app.chat.module.listener;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.module.event.MLApplyForEvent;
import net.melove.app.chat.module.database.MLContactsDao;
import net.melove.app.chat.module.event.MLContactsEvent;
import net.melove.app.chat.module.notification.MLNotifier;
import net.melove.app.chat.ui.contacts.MLContacterEntity;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.util.MLLog;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2016/10/26.
 * 联系人监听详细处理类
 */

public class MLContactsListener implements EMContactListener {

    /**
     * 监听到添加联系人
     *
     * @param username 被添加的联系人
     */
    @Override public void onContactAdded(String username) {
        // 创建一个新的联系人对象，并保存到本地
        MLContacterEntity contacts = new MLContacterEntity();
        contacts.setUserName(username);
        /**
         * 调用{@link MLContactsDao#saveContacts(MLContacterEntity)} 去保存联系人，
         * 这里将{@link MLContactsDao} 封装成了单例类
         */
        MLContactsDao.getInstance().saveContacts(contacts);
        // 发送可被订阅的消息，通知订阅者联系人有变化
        EventBus.getDefault().post(new MLContactsEvent());
    }

    /**
     * 监听删除联系人
     *
     * @param username 被删除的联系人
     */
    @Override public void onContactDeleted(String username) {
        /**
         * 监听到联系人被删除，删除本地的数据
         * 调用{@link MLContactsDao#deleteContacts(String)} 去删除指定的联系人
         * 这里将{@link MLContactsDao} 封装成了单例类
         */
        MLContactsDao.getInstance().deleteContacts(username);
        // 发送可被订阅的消息，通知订阅者联系人有变化
        EventBus.getDefault().post(new MLContactsEvent());
    }

    /**
     * 收到对方联系人申请
     *
     * @param username 发送好友申请者username
     * @param reason 申请理由
     */
    @Override public void onContactInvited(String username, String reason) {
        MLLog.d("onContactInvited - username:%s, reaseon:%s", username, reason);

        // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息
        String msgId = username + EMClient.getInstance().getCurrentUser();

        // 首先查找这条申请消息是否为空
        EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
        if (message != null) {
            // 申请理由
            message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
            // 当前申请的消息状态
            message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_APPLY_FOR);
            // 更新消息时间
            message.setMsgTime(MLDateUtil.getCurrentMillisecond());
            message.setLocalTime(message.getMsgTime());
            // 更新消息到本地
            EMClient.getInstance().chatManager().updateMessage(message);
        } else {
            // 创建一条接收的消息，用来保存申请信息
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            EMTextMessageBody body = new EMTextMessageBody(username + " 申请加你好友");
            message.addBody(body);
            // 设置消息扩展，主要是申请信息
            message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
            // 申请者username
            message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
            // 申请理由
            message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
            // 当前申请的消息状态
            message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_APPLY_FOR);
            // 申请与通知类型
            message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
            // 设置消息发送方
            message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
            // 设置
            message.setMsgId(msgId);
            // 将消息保存到本地和内存
            EMClient.getInstance().chatManager().saveMessage(message);
        }
        // 调用发送通知栏提醒方法，提醒用户查看申请通知
        MLNotifier.getInstance().sendNotificationMessage(message);
        // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
        MLApplyForEvent event = new MLApplyForEvent();
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }

    /**
     * 对方同意了自己的申请
     *
     * @param username 对方的username
     */
    @Override public void onContactAgreed(String username) {
        MLLog.d("onContactAgreed - username:%s", username);
        // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息（申请者在前）
        String msgId = EMClient.getInstance().getCurrentUser() + username;

        // 首先查找这条申请消息是否为空
        EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
        if (message != null) {
            // 当前申请的消息状态
            message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
            // 更新消息到本地
            EMClient.getInstance().chatManager().updateMessage(message);
        } else {
            // 创建一条接收的消息，用来保存申请信息
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            EMTextMessageBody body = new EMTextMessageBody(username + " 同意了你的好友申请");
            message.addBody(body);
            // 设置消息扩展，主要是申请信息
            message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
            // 申请者username
            message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
            // 申请理由
            message.setAttribute(MLConstants.ML_ATTR_REASON, "我同意你的好友申请");
            // 当前申请的消息状态
            message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
            // 申请与通知类型
            message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
            // 设置消息发送方
            message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
            // 设置
            message.setMsgId(msgId);
            // 将消息保存到本地和内存
            EMClient.getInstance().chatManager().saveMessage(message);
        }
        // 调用发送通知栏提醒方法，提醒用户查看申请通知
        MLNotifier.getInstance().sendNotificationMessage(message);
        // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
        MLApplyForEvent event = new MLApplyForEvent();
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }

    /**
     * 对方拒绝了联系人申请
     *
     * @param username 对方的username
     */
    @Override public void onContactRefused(String username) {
        MLLog.d("onContactRefused - username:%s", username);
        // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息（申请者在前）
        String msgId = EMClient.getInstance().getCurrentUser() + username;

        // 首先查找这条申请消息是否为空
        EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
        if (message != null) {
            // 当前申请的消息状态
            message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
            // 更新消息到本地
            EMClient.getInstance().chatManager().updateMessage(message);
        } else {
            // 创建一条接收的消息，用来保存申请信息
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            EMTextMessageBody body = new EMTextMessageBody(username + " 同意了你的好友申请");
            message.addBody(body);
            // 设置消息扩展，主要是申请信息
            message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
            // 申请者username
            message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
            // 申请理由
            message.setAttribute(MLConstants.ML_ATTR_REASON, "我同意你的好友申请");
            // 当前申请的消息状态
            message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_AGREED);
            // 申请与通知类型
            message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
            // 设置消息发送方
            message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
            // 设置
            message.setMsgId(msgId);
            // 将消息保存到本地和内存
            EMClient.getInstance().chatManager().saveMessage(message);
        }
        // 调用发送通知栏提醒方法，提醒用户查看申请通知
        MLNotifier.getInstance().sendNotificationMessage(message);
        // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
        MLApplyForEvent event = new MLApplyForEvent();
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }
}