package net.melove.app.chat.ui.call;

/**
 * Created by lzan13 on 2016/8/16.
 * 语音和视频通话状态类，这是一个单例类，因为通话同一时间只会存在一个
 */
public class MLCallStatus {

    // 通话状态
    public static int CALL_STATUS_NORMAL = 0x00;
    public static int CALL_STATUS_CONNECTING = 0x01;
    public static int CALL_STATUS_CONNECTING_INCOMING = 0x02;
    public static int CALL_STATUS_ACCEPTED = 0x03;

    // 通话类型
    public static int CALL_TYPE_NORMAL = 0x00;
    public static int CALL_TYPE_VIDEO = 0x01;
    public static int CALL_TYPE_VOICE = 0x02;

    // 当前类的实例
    private static MLCallStatus instance;

    // 通话状态
    private int callState;
    // 通话类型
    private int callType;

    // 是否是呼入的通话
    private boolean isInComing;
    // 是否开启麦克风
    private boolean isMic;
    // 是否开启了摄像头
    private boolean isCamera;
    // 是否开启扬声器
    private boolean isSpeaker;
    // 是否开启了录制
    private boolean isRecord;

    /**
     * 私有构造方法
     * 实例化的时候初始化一些默认值
     */
    private MLCallStatus() {
        setCallType(CALL_TYPE_NORMAL);
        setCallState(CALL_STATUS_NORMAL);
        setMic(true);
        setCamera(true);
        setSpeaker(true);
        setRecord(false);
    }

    /**
     * 重置通话状态
     */
    public void reset() {
        setCallType(CALL_TYPE_NORMAL);
        setCallState(CALL_STATUS_NORMAL);
        setMic(true);
        setCamera(true);
        setSpeaker(true);
        setRecord(true);
    }

    /**
     * 获取单例类的实例
     *
     * @return
     */
    public static MLCallStatus getInstance() {
        if (instance == null) {
            instance = new MLCallStatus();
        }
        return instance;
    }

    public boolean isInComing() {
        return isInComing;
    }

    public void setInComing(boolean inComing) {
        isInComing = inComing;
    }

    public int getCallState() {
        return callState;
    }

    public void setCallState(int callState) {
        this.callState = callState;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    public boolean isMic() {
        return isMic;
    }

    public void setMic(boolean mic) {
        isMic = mic;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        isRecord = record;
    }

    public boolean isSpeaker() {
        return isSpeaker;
    }

    public void setSpeaker(boolean speaker) {
        isSpeaker = speaker;
    }
}
