package net.melove.app.chat.connection;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.connection.MLConnectionEvent;
import net.melove.app.chat.util.MLLog;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2016/10/26.
 * 链接监听详细处理类
 */
public class MLConnectionListener implements EMConnectionListener {

    /**
     * 链接聊天服务器成功
     *
     */
    @Override public void onConnected() {
        MLLog.d("onConnected");
        MLHyphenate.getInstance().unBuildToken = true;
        // 设置链接监听变化状态
        MLConnectionEvent event = new MLConnectionEvent();
        event.setType(MLConstants.ML_CONNECTION_CONNECTED);
        // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
        EventBus.getDefault().post(event);
    }

    /**
     * 链接聊天服务器失败
     *
     * @param errorCode 连接失败错误码
     */
    @Override public void onDisconnected(final int errorCode) {
        MLLog.d("onDisconnected - %d", errorCode);
        // 在离线状态下，退出登录的时候需要设置为false，已经登录成功的状态要改为 false，这个在使用了推送功能时，调用logout需要传递
        MLHyphenate.getInstance().unBuildToken = false;
        MLConnectionEvent event = new MLConnectionEvent();
        if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            MLLog.d("user login another device - " + errorCode);
            MLHyphenate.getInstance().signOut(null);
            // 设置链接监听变化状态
            event.setType(MLConstants.ML_CONNECTION_USER_LOGIN_OTHER_DIVERS);
        } else if (errorCode == EMError.USER_REMOVED) {
            MLLog.d("user be removed - " + errorCode);
            MLHyphenate.getInstance().signOut(null);
            // 设置链接监听变化状态
            event.setType(MLConstants.ML_CONNECTION_USER_REMOVED);
        } else {
            MLLog.d("con't servers - " + errorCode);
            // 设置链接监听变化状态
            event.setType(MLConstants.ML_CONNECTION_DISCONNECTED);
        }
        // 发送订阅消息，通知网络监听有变化
        EventBus.getDefault().post(event);
    }
}
