package com.vmloft.develop.app.chat.connection;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.base.IMHelper;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2016/10/26.
 * 链接监听详细处理类
 */
public class ConnectionListener implements EMConnectionListener {

    /**
     * 链接聊天服务器成功
     *
     */
    @Override public void onConnected() {
        VMLog.d("onConnected");
        IMHelper.getInstance().unBuildToken = true;
        // 设置链接监听变化状态
        ConnectionEvent event = new ConnectionEvent();
        event.setType(AConstants.CONNECTION_CONNECTED);
        // 使用 EventBus 发布消息，可以被订阅此类型消息的订阅者监听到
//        EventBus.getDefault().post(event);
    }

    /**
     * 链接聊天服务器失败
     *
     * @param errorCode 连接失败错误码
     */
    @Override public void onDisconnected(final int errorCode) {
        VMLog.d("onDisconnected - %d", errorCode);
        // 在离线状态下，退出登录的时候需要设置为false，已经登录成功的状态要改为 false，这个在使用了推送功能时，调用logout需要传递
        IMHelper.getInstance().unBuildToken = false;
        ConnectionEvent event = new ConnectionEvent();
        if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            VMLog.d("user login another device - " + errorCode);
            IMHelper.getInstance().signOut(null);
            // 设置链接监听变化状态
            event.setType(AConstants.CONNECTION_USER_LOGIN_OTHER_DIVERS);
        } else if (errorCode == EMError.USER_REMOVED) {
            VMLog.d("user be removed - " + errorCode);
            IMHelper.getInstance().signOut(null);
            // 设置链接监听变化状态
            event.setType(AConstants.CONNECTION_USER_REMOVED);
        } else {
            VMLog.d("con't servers - " + errorCode);
            // 设置链接监听变化状态
            event.setType(AConstants.CONNECTION_DISCONNECTED);
        }
        // 发送订阅消息，通知网络监听有变化
//        EventBus.getDefault().post(event);
    }
}
