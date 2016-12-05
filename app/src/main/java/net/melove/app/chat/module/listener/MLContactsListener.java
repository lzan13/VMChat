package net.melove.app.chat.module.listener;

import android.content.Context;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.R;
import net.melove.app.chat.module.event.MLApplyForEvent;
import net.melove.app.chat.module.event.MLUserEvent;
import net.melove.app.chat.module.notification.MLNotifier;
import net.melove.app.chat.ui.contacts.MLContactsManager;
import net.melove.app.chat.ui.contacts.MLUserEntity;
import net.melove.app.chat.util.MLLog;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2016/10/26.
 * 联系人监听详细处理类
 */

public class MLContactsListener implements EMContactListener {

    private Context mContext;

    public MLContactsListener(Context context) {
        mContext = context;
    }

    /**
     * 监听到好友被添加
     *
     * @param username 被添加的用户 username
     */
    @Override public void onContactAdded(String username) {
        MLUserEntity userEntity = new MLUserEntity(username);
        MLContactsManager.getInstance().saveUser(userEntity);

        // 发送可被订阅的消息，通知订阅者联系人有变化
        EventBus.getDefault().post(new MLUserEvent());
    }

    /**
     * 监听删除好友
     *
     * @param username 被删除的用户 username
     */
    @Override public void onContactDeleted(String username) {
        MLContactsManager.getInstance().deleteUser(username);

        // 发送可被订阅的消息，通知订阅者联系人有变化
        EventBus.getDefault().post(new MLUserEvent());
    }

    /**
     * x
     * 收到对方添加好友申请
     *
     * @param username 发送好友申请者 username
     * @param reason 申请理由
     */
    @Override public void onContactInvited(String username, String reason) {
        MLLog.d("onContactInvited - username:%s, reaseon:%s", username, reason);

        // 根据申请者的 username 和当前时间组成 msgId
        String msgId = username + System.currentTimeMillis();

        // 创建一条接收消息，用来保存申请信息
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        // 将理由保存为消息内容，用于直接显示
        EMTextMessageBody body = new EMTextMessageBody(mContext.getString(R.string.ml_have_apply));
        message.addBody(body);
        // 申请者username
        message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
        // 设置理由
        message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
        // 申请与通知类型
        message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_TYPE_USER);
        // 设置当前申请信息状态
        message.setAttribute(MLConstants.ML_ATTR_STATUS, "");
        // 设置消息发送方
        message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY);
        // 设置msgId
        message.setMsgId(msgId);
        // 将消息保存到本地和内存
        EMClient.getInstance().chatManager().saveMessage(message);

        // 调用发送通知栏提醒方法，提醒用户查看申请通知
        MLNotifier.getInstance().sendNotificationMessage(message);

        // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
        MLApplyForEvent event = new MLApplyForEvent();
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }

    /**
     * 对方同意了自己的好友申请
     *
     * @param username 对方的 username
     */
    @Override public void onContactAgreed(String username) {
        MLLog.d("onContactAgreed - username:%s", username);

        // 根据申请者的 username 和当前时间组成 msgId
        String msgId = username + System.currentTimeMillis();

        // 创建一条接收消息，用来保存申请信息
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        // 保存为消息内容，用于提示用户有新的申请与通知信息
        EMTextMessageBody body = new EMTextMessageBody(mContext.getString(R.string.ml_have_apply));
        message.addBody(body);
        // 申请者username
        message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
        // 设置理由
        message.setAttribute(MLConstants.ML_ATTR_REASON,
                mContext.getString(R.string.ml_be_agreed_user));
        // 申请与通知类型
        message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_TYPE_USER);
        // 设置当前申请信息状态
        message.setAttribute(MLConstants.ML_ATTR_STATUS, mContext.getString(R.string.ml_agreed));
        // 设置消息发送方
        message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY);
        // 设置msgId
        message.setMsgId(msgId);
        // 将消息保存到本地和内存
        EMClient.getInstance().chatManager().saveMessage(message);

        // 调用发送通知栏提醒方法，提醒用户查看申请通知
        MLNotifier.getInstance().sendNotificationMessage(message);

        // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
        MLApplyForEvent event = new MLApplyForEvent();
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }

    /**
     * 对方拒绝了自己的好友申请
     *
     * @param username 对方的 username
     */
    @Override public void onContactRefused(String username) {
        MLLog.d("onContactRefused - username:%s", username);

        // 根据申请者的 username 和当前时间组成 msgId
        String msgId = username + System.currentTimeMillis();

        // 创建一条接收消息，用来保存申请信息
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        // 将理由保存为消息内容，用于直接显示
        EMTextMessageBody body = new EMTextMessageBody(mContext.getString(R.string.ml_have_apply));
        message.addBody(body);
        // 申请者username
        message.setAttribute(MLConstants.ML_ATTR_USERNAME, username);
        // 设置理由
        message.setAttribute(MLConstants.ML_ATTR_REASON,
                mContext.getString(R.string.ml_be_agreed_user));
        // 申请与通知类型
        message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_TYPE_USER);
        // 设置当前申请信息状态
        message.setAttribute(MLConstants.ML_ATTR_STATUS, mContext.getString(R.string.ml_rejected));
        // 设置消息发送方
        message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY);
        // 设置msgId
        message.setMsgId(msgId);
        // 将消息保存到本地和内存
        EMClient.getInstance().chatManager().saveMessage(message);

        // 调用发送通知栏提醒方法，提醒用户查看申请通知
        MLNotifier.getInstance().sendNotificationMessage(message);

        // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
        MLApplyForEvent event = new MLApplyForEvent();
        event.setMessage(message);
        EventBus.getDefault().post(event);
    }
}