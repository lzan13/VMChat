package net.melove.demo.chat.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.easemob.chat.EMMessage;

import net.melove.demo.chat.R;

/**
 * Created by lzan13 on 2016/1/13.
 */
public class MLNotifier {
    private Context mContext;

    private static MLNotifier instance;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder mBuilder = null;

    private MLNotifier(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }

    public static MLNotifier getInstance(Context context) {
        if (instance == null) {
            instance = new MLNotifier(context);
        }
        return instance;
    }

    public void sendNotification(String message) {
        sendNotification(message, true);
    }

    public void sendNotification(String content, boolean autoCancel) {
        mBuilder.setContentTitle("好友申请")//设置通知栏标题
                .setContentText("申请添加你为好友") //设置通知栏显示内容
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                        // .setNumber(number) //设置通知集合的数量
                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                        // .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        // Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_emotion_smile_24dp);//设置通知小ICON
        mNotificationManager.notify(5121, mBuilder.build());
    }

    public void sendNotification(EMMessage message, boolean autoCancel) {

    }
}
