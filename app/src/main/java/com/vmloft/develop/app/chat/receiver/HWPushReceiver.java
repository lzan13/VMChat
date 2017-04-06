package com.vmloft.develop.app.chat.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.hyphenate.chat.EMHuaweiPushReceiver;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Create by lzan13 2016/6/6
 * 华为离线推送通知的接收实现类
 */
public class HWPushReceiver extends EMHuaweiPushReceiver {

    /**
     * 显示Push消息
     */
    public void showPushMessage(String msg) {
        VMLog.i("push message %s", msg);
    }

    @Override public void onToken(Context context, String token, Bundle extras) {
        super.onToken(context, token, extras);
        String belongId = extras.getString("belongId");
        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        VMLog.i("onToken %s", content);
    }

    @Override public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            VMLog.i(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "收到通知附加消息： " + extras.getString(BOUND_KEY.pushMsgKey);
            VMLog.d("HWPush %d, %s", notifyId, content);
            showPushMessage(content);
        } else if (Event.PLUGINRSP.equals(event)) {
            final int TYPE_LBS = 1;
            final int TYPE_TAG = 2;
            int reportType = extras.getInt(BOUND_KEY.PLUGINREPORTTYPE, -1);
            boolean isSuccess = extras.getBoolean(BOUND_KEY.PLUGINREPORTRESULT, false);
            String message = "";
            if (TYPE_LBS == reportType) {
                message = "LBS report result :";
            } else if (TYPE_TAG == reportType) {
                message = "TAG report result :";
            }
            VMLog.d("HWPush %d, %s", reportType, message);
            showPushMessage(message + isSuccess);
        }
        super.onEvent(context, event, extras);
    }
}
