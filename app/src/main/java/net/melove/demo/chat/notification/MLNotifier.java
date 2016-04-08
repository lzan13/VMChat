package net.melove.demo.chat.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.conversation.MLChatActivity;
import net.melove.demo.chat.main.MLMainActivity;
import net.melove.demo.chat.invited.MLInvitedEntity;

/**
 * Created by lzan13 on 2016/1/13.
 */
public class MLNotifier {
    private Context mContext;

    private int mInvitedNotifyId = 5120;
    private int mMsgNotifyId = 5121;

    private static MLNotifier instance;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder mBuilder = null;

    private MLNotifier(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        init();
    }

    public static MLNotifier getInstance(Context context) {
        if (instance == null) {
            instance = new MLNotifier(context);
        }
        return instance;
    }

    /**
     * 初始化一些通知的通用设置
     */
    private void init() {
        //设置通知小ICON
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);

        //设置该通知优先级
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        //设置这个标志当用户单击面板就可以让通知将自动取消
        mBuilder.setAutoCancel(true);

        //向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);


        /**
         * 设置默认提醒，默认的有声音，振动，三色灯提醒
         * Notification.DEFAULT_VIBRATE    //添加默认震动提醒  需要 VIBRATE permission
         * Notification.DEFAULT_SOUND    // 添加默认声音提醒
         * Notification.DEFAULT_LIGHTS// 添加默认三色灯提醒
         * Notification.DEFAULT_ALL// 添加默认以上3种全部提醒
         */
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        // 设置自定义的振动提醒
//        mBuilder.setVibrate(new long[]{0, 180, 100, 300});
        // 设置自定义的三色灯提醒（有可能有的设备不支持）
//        mBuilder.setLights(0xffcc33, 500, 300);
    }

    /**
     * 发送好友请求通知
     *
     * @param invitedEntity 申请与通知的实体类，用来确定发送通知的内容
     */
    public void sendInvitedNotification(MLInvitedEntity invitedEntity) {

        String message = null;
        // 设置通知栏标题
        mBuilder.setContentTitle("环聊通知");
        switch (invitedEntity.getStatus()) {
            case BEAGREED:
                message = invitedEntity.getUserName() + " 同意了你的请求";
                break;
            case BEREFUSED:
                message = invitedEntity.getUserName() + " 拒绝了你的请求";
                break;
            case BEAPPLYFOR:
                message = invitedEntity.getUserName() + " 申请添加你为好友";
                break;
            case GROUPAPPLYFOR:

                break;
        }

        mBuilder.setContentText(message);

        // 设置状态栏显示内容（这里是一闪而过的，带有上升动画）
        mBuilder.setTicker("有条新的请求等你处理");

        // 设置通知栏点击意图（点击通知栏跳转到相应的页面）
        Intent intent = new Intent(mContext, MLMainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        mBuilder.setContentIntent(pIntent);

        // 发送通知
        mNotificationManager.notify(mInvitedNotifyId, mBuilder.build());
    }

    /**
     * 发送消息通知
     *
     * @param message 收到的消息，根据这个消息去发送通知
     */
    public void sendMessageNotification(EMMessage message) {
        String content = "";
        EMMessage.Type type = message.getType();
        switch (type) {
            case TXT:
                EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                content = body.getMessage().toString();
                break;
            case IMAGE:
                content = "[图片消息]";
                break;
            case FILE:
                content = "[文件消息]";
                break;
            case LOCATION:
                content = "[位置消息]";
                break;
            case VIDEO:
                content = "[视频消息]";
                break;
            case VOICE:
                content = "[语音消息]";
                break;
            case CMD:
                break;
        }
        mBuilder.setContentText(content);

        mBuilder.setContentTitle("环聊通知");
        // 设置通知栏点击意图（点击通知栏跳转到相应的页面）
        Intent intent = new Intent(mContext, MLChatActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pIntent);
        // 紧急事件，比如通话，跳过发送通知栏提醒，直接响应对应的事件
//        mBuilder.setFullScreenIntent(pIntent, true);

        // 设置通知集合的数量
//        mBuilder.setNumber(number);
        // 通知首次出现在通知栏，带上升动画效果的（这里是一闪而过的，带有上升动画）
        mBuilder.setTicker("有一条新消息，别忘记看~");
        // 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
        mBuilder.setWhen(System.currentTimeMillis());


        // 发送通知栏通知
        mNotificationManager.notify(mMsgNotifyId, mBuilder.build());
    }

    /**
     * 发送消息通知
     *
     * @param message
     */
    public void sendNotification(String message) {

    }
}
