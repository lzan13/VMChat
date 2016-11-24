package net.melove.app.chat.module.listener;


import com.hyphenate.chat.EMCallStateChangeListener;

import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.ui.call.MLCallEvent;
import net.melove.app.chat.ui.call.MLCallStatus;
import net.melove.app.chat.util.MLLog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2016/10/18.
 * 通话状态监听类，用来监听通话过程中状态的变化
 */

public class MLCallStateListener implements EMCallStateChangeListener {

    @Override
    public void onCallStateChanged(CallState callState, CallError callError) {
        /**
         * 使用 EventBus 发送自定义的可订阅事件{@link MLCallEvent}
         *  {@link VideoCallActivity#onEventBus(CallEvent)}
         *  {@link VoiceCallActivity#onEventBus(CallEvent)}
         */
        MLCallEvent event = new MLCallEvent();
        event.setCallState(callState);
        event.setCallError(callError);
        EventBus.getDefault().post(event);

        switch (callState) {
            case CONNECTING: // 正在呼叫对方
                MLLog.i("正在呼叫对方" + callError);
                MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_CONNECTING);
                break;
            case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
                MLLog.i("正在等待对方接受呼叫申请" + callError);
                MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_CONNECTING);
                break;
            case ACCEPTED: // 通话已接通
                MLLog.i("通话已接通");
                MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_ACCEPTED);
                break;
            case DISCONNECTED: // 通话已中断
                MLLog.i("通话已结束" + callError);
                // 通话结束，重置通话状态
                MLCallStatus.getInstance().reset();
                if (callError == CallError.ERROR_UNAVAILABLE) {
                    MLLog.i("对方不在线" + callError);
                } else if (callError == CallError.ERROR_BUSY) {
                    MLLog.i("对方正忙" + callError);
                } else if (callError == CallError.REJECTED) {
                    MLLog.i("对方已拒绝" + callError);
                } else if (callError == CallError.ERROR_NORESPONSE) {
                    MLLog.i("对方未响应，可能手机不在身边" + callError);
                } else if (callError == CallError.ERROR_TRANSPORT) {
                    MLLog.i("连接建立失败" + callError);
                } else if (callError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED) {
                    MLLog.i("双方通讯协议不同" + callError);
                } else if (callError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                    MLLog.i("双方通讯协议不同" + callError);
                } else {
                    MLLog.i("通话已结束，时长：%s，error %s", "10:35", callError);
                }
                // 结束通话时取消通话状态监听
                MLHyphenate.getInstance().removeCallStateListener();
                break;
            case NETWORK_UNSTABLE:
                if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                    MLLog.i("没有通话数据" + callError);
                } else {
                    MLLog.i("网络不稳定" + callError);
                }
                break;
            case NETWORK_NORMAL:
                MLLog.i("网络正常");
                break;
            case VIDEO_PAUSE:
                MLLog.i("视频传输已暂停");
                break;
            case VIDEO_RESUME:
                MLLog.i("视频传输已恢复");
                break;
            case VOICE_PAUSE:
                MLLog.i("语音传输已暂停");
                break;
            case VOICE_RESUME:
                MLLog.i("语音传输已恢复");
                break;
            default:
                break;
        }
    }
}
