package net.melove.app.chat.ui.chat.call;


import com.hyphenate.chat.EMCallStateChangeListener;

/**
 * Created by lzan13 on 2016/8/1.
 * 通话事件类
 */
public class MLCallEvent {

    // 通话错误信息
    private EMCallStateChangeListener.CallError callError;

    // 通话状态
    private EMCallStateChangeListener.CallState callState;


    public EMCallStateChangeListener.CallError getCallError() {
        return callError;
    }

    public void setCallError(EMCallStateChangeListener.CallError callError) {
        this.callError = callError;
    }

    public EMCallStateChangeListener.CallState getCallState() {
        return callState;
    }

    public void setCallState(EMCallStateChangeListener.CallState callState) {
        this.callState = callState;
    }


}
