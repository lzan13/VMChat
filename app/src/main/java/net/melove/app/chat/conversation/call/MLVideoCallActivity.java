package net.melove.app.chat.conversation.call;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMCallStateChangeListener.CallError;
import com.hyphenate.chat.EMCallStateChangeListener.CallState;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLEasemobHelper;
import net.melove.app.chat.application.eventbus.MLCallEvent;
import net.melove.app.chat.communal.util.MLLog;
import net.melove.app.chat.communal.widget.MLToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MLVideoCallActivity extends MLCallActivity {

    // 视频通话帮助类
    private EMCallManager.EMVideoCallHelper mVideoCallHelper;

    // 显示视频通话画面的控件
    private EMLocalSurfaceView mLocalSurfaceView;
    private EMOppositeSurfaceView mOppositeSurfaceView;

    // 界面控件
    // 通话状态控件
    private TextView mCallStatusView;
    // 切换摄像头按钮
    private ImageButton mChangeCameraSwitch;
    // 通话界面最小化按钮
    private ImageButton mExitFullScreenBtn;
    // 麦克风开关
    private ImageButton mMicSwitch;
    // 摄像头开关
    private ImageButton mCameraSwitch;
    // 扬声器开关
    private ImageButton mSpeakerSwitch;
    // 录制开关
    private ImageButton mRecordSwitch;
    // 拒绝接听按钮
    private FloatingActionButton mRejectCallFab;
    // 结束通话按钮
    private FloatingActionButton mEndCallFab;
    // 接听通话按钮
    private FloatingActionButton mAnswerCallFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        initView();

    }

    /**
     * 重载父类方法,实现一些当前通话的操作，
     */
    @Override
    protected void initView() {
        super.initView();

        // 初始化界面控件
        mLocalSurfaceView = (EMLocalSurfaceView) findViewById(R.id.ml_surface_view_local);
        mOppositeSurfaceView = (EMOppositeSurfaceView) findViewById(R.id.ml_surface_view_opposite);

        // 通话状态控件
        mCallStatusView = (TextView) findViewById(R.id.ml_text_call_status);
        // 界面操作按钮
        mExitFullScreenBtn = (ImageButton) findViewById(R.id.ml_btn_exit_full_screen);
        mChangeCameraSwitch = (ImageButton) findViewById(R.id.ml_btn_change_camera_switch);
        mMicSwitch = (ImageButton) findViewById(R.id.ml_btn_mic_switch);
        mCameraSwitch = (ImageButton) findViewById(R.id.ml_btn_camera_switch);
        mSpeakerSwitch = (ImageButton) findViewById(R.id.ml_btn_speaker_switch);
        mRecordSwitch = (ImageButton) findViewById(R.id.ml_btn_record_switch);
        mRejectCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_reject_call);
        mEndCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_end_call);
        mAnswerCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_answer_call);

        // 设置按钮状态
        mChangeCameraSwitch.setActivated(true);
        mMicSwitch.setActivated(true);
        mCameraSwitch.setActivated(true);
        mSpeakerSwitch.setActivated(true);
        mRecordSwitch.setActivated(false);

        // 设置按钮的点击监听
        mExitFullScreenBtn.setOnClickListener(viewListener);
        mChangeCameraSwitch.setOnClickListener(viewListener);
        mMicSwitch.setOnClickListener(viewListener);
        mCameraSwitch.setOnClickListener(viewListener);
        mSpeakerSwitch.setOnClickListener(viewListener);
        mRecordSwitch.setOnClickListener(viewListener);
        mRejectCallFab.setOnClickListener(viewListener);
        mEndCallFab.setOnClickListener(viewListener);
        mAnswerCallFab.setOnClickListener(viewListener);


        // 初始化视频通话帮助类
        mVideoCallHelper = EMClient.getInstance().callManager().getVideoCallHelper();
        // 设置自动码率  TODO 新的针对音视频优化的 SDK 不需要调用，默认直接开启
        mVideoCallHelper.setAdaptiveVideoFlag(true);
        // 设置视频通话分辨率 默认是(320, 240)
        mVideoCallHelper.setResolution(640, 480);
        // 设置视频通话比特率 默认是(150)
        mVideoCallHelper.setVideoBitrate(300);
        EMClient.getInstance().callManager().setSurfaceView(mLocalSurfaceView, mOppositeSurfaceView);

        // 设置界面控件的显示
        if (isInComingCall) {
            mCallStatusView.setText(R.string.ml_call_connected_is_incoming);
            mRejectCallFab.setVisibility(View.VISIBLE);
            mEndCallFab.setVisibility(View.GONE);
            mAnswerCallFab.setVisibility(View.VISIBLE);
        } else {
            mCallStatusView.setText(R.string.ml_call_connecting);
            mRejectCallFab.setVisibility(View.GONE);
            mEndCallFab.setVisibility(View.VISIBLE);
            mAnswerCallFab.setVisibility(View.GONE);
            // 自己是主叫方，调用呼叫方法
            makeCall();
        }
    }

    /**
     * 开始呼叫对方
     */
    private void makeCall() {
        try {
            EMClient.getInstance().callManager().makeVideoCall(username);
        } catch (EMServiceNotReadyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 界面控件点击监听器
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.ml_btn_exit_full_screen:
                // 最小化通话界面
                break;
            case R.id.ml_btn_change_camera_switch:
                // 切换摄像头
                changeCamera();
                break;
            case R.id.ml_btn_mic_switch:
                // 麦克风开关
                onMicrophone();
                break;
            case R.id.ml_btn_camera_switch:
                // 摄像头开关
                onCamera();
                break;
            case R.id.ml_btn_speaker_switch:
                // 扬声器开关
                onSpeaker();
                break;
            case R.id.ml_btn_record_switch:
                // 录制开关
                recordCall();
                break;
            case R.id.ml_btn_fab_reject_call:
                // 拒绝接听通话
                rejectCall();
                break;
            case R.id.ml_btn_fab_end_call:
                // 结束通话
                endCall();
                break;
            case R.id.ml_btn_fab_answer_call:
                // 接听通话
                answerCall();
                break;
            }
        }
    };

    /**
     * 切换摄像头
     */
    private void changeCamera() {
        vibrate();
        // 根据切换摄像头开关是否被激活确定当前是前置还是后置摄像头
        if (mChangeCameraSwitch.isActivated()) {
            EMClient.getInstance().callManager().switchCamera();
            mChangeCameraSwitch.setActivated(false);
        } else {
            EMClient.getInstance().callManager().switchCamera();
            mChangeCameraSwitch.setActivated(false);
        }
    }

    /**
     * 麦克风开关，主要调用环信语音数据传输方法
     * TODO 3.1.4 SDK 暂时无效
     */
    private void onMicrophone() {
        vibrate();
        // 根据麦克风开关是否被激活来进行判断麦克风状态，然后进行下一步操作
        if (mMicSwitch.isActivated()) {
            // 暂停语音数据的传输
            EMClient.getInstance().callManager().pauseVoiceTransfer();
            mMicSwitch.setActivated(false);
        } else {
            // 回复语音数据的传输
            EMClient.getInstance().callManager().resumeVoiceTransfer();
            mMicSwitch.setActivated(true);
        }
    }

    /**
     * 摄像头开关
     */
    private void onCamera() {
        vibrate();
        // 根据摄像头开关按钮状态判断摄像头状态，然后进行下一步操作
        if (mCameraSwitch.isActivated()) {
            EMClient.getInstance().callManager().pauseVideoTransfer();
            mCameraSwitch.setActivated(false);
        } else {
            EMClient.getInstance().callManager().resumeVideoTransfer();
            mCameraSwitch.setActivated(true);
        }
    }

    /**
     * 扬声器开关
     */
    private void onSpeaker() {
        vibrate();
        // 根据按钮状态决定打开还是关闭扬声器
        if (mSpeakerSwitch.isActivated()) {
            closeSpeaker();
        } else {
            openSpeaker();
        }
    }

    /**
     * 录制通话内容 TODO 后期实现
     */
    private void recordCall() {
        vibrate();
        MLToast.makeToast(R.string.ml_toast_unrealized).show();
        // 根据开关状态决定是否开启录制
        if (mRecordSwitch.isActivated()) {
            mRecordSwitch.setActivated(false);
        } else {
            mRecordSwitch.setActivated(false);
        }
    }

    /**
     * 拒绝通话
     */
    private void rejectCall() {
        // 结束通话时取消通话状态监听
        MLEasemobHelper.getInstance().removeCallStateChangeListener();
        // 拒绝通话后关闭通知铃音
        stopCallSound();
        try {
            // 调用 SDK 的拒绝通话方法
            EMClient.getInstance().callManager().rejectCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            MLToast.errorToast("拒绝通话失败：error-" + e.getErrorCode() + "-" + e.getMessage()).show();
        }
        onFinish();
    }

    /**
     * 结束通话
     */
    private void endCall() {
        // 结束通话时取消通话状态监听
        MLEasemobHelper.getInstance().removeCallStateChangeListener();
        // 结束通话后关闭通知铃音
        stopCallSound();
        try {
            // 调用 SDK 的结束通话方法
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            MLToast.errorToast("结束通话失败：error-" + e.getErrorCode() + "-" + e.getMessage()).show();
        }
        onFinish();
    }

    /**
     * 接听通话
     */
    private void answerCall() {
        // 做一些接听时的操作，比如隐藏按钮，打开扬声器等
        mRejectCallFab.setVisibility(View.GONE);
        mAnswerCallFab.setVisibility(View.GONE);
        mEndCallFab.setVisibility(View.VISIBLE);
        // 接听通话后关闭通知铃音
        stopCallSound();
        // 默认接通时打开免提
        openSpeaker();
        // 调用接通通话方法
        try {
            EMClient.getInstance().callManager().answerCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            MLToast.errorToast("接听通话失败：error-" + e.getErrorCode() + "-" + e.getMessage()).show();
        }
    }

    /**
     * 打开扬声器
     * 主要是通过扬声器的开关以及设置音频播放模式来实现
     * 1、MODE_NORMAL：是正常模式，一般用于外放音频
     * 2、MODE_IN_CALL：
     * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
     * 4、MODE_RINGTONE：铃声模式
     */

    private void openSpeaker() {
        mSpeakerSwitch.setActivated(true);
        // 检查是否已经开启扬声器
        if (!mAudioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            mAudioManager.setSpeakerphoneOn(true);
        }
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     * 关闭扬声器，即开启听筒播放模式
     * 同上边{@link #openSpeaker()}
     */
    private void closeSpeaker() {
        mSpeakerSwitch.setActivated(false);
        // 检查是否已经开启扬声器
        if (mAudioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            mAudioManager.setSpeakerphoneOn(false);
        }
        // 设置声音模式为通讯模式，即使用听筒播放
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLCallEvent event) {
        CallError callError = event.getCallError();
        CallState callState = event.getCallState();

        switch (callState) {
        case CONNECTING: // 正在呼叫对方
            MLLog.i("正在呼叫对方" + callError);
            mCallStatusView.setText(R.string.ml_call_connecting);
            break;
        case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
            MLLog.i("正在等待对方接受呼叫申请" + callError);
            mCallStatusView.setText(R.string.ml_call_connected);
            break;
        case ACCEPTED: // 通话已接通
            MLLog.i("通话已接通");
            // 电话接通，停止播放提示音
            mCallStatusView.setText(R.string.ml_call_accepted);
            stopCallSound();
            break;
        case DISCONNNECTED: // 通话已中断
            MLLog.i("通话已结束" + callError);
            mCallStatusView.setText(R.string.ml_call_disconnected);

            if (callError == EMCallStateChangeListener.CallError.ERROR_INAVAILABLE) {
                MLLog.i("对方不在线" + callError);
                mCallStatusView.setText(R.string.ml_call_not_online);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_BUSY) {
                MLLog.i("对方正在通话中" + callError);
                mCallStatusView.setText(R.string.ml_call_busy);
            } else if (callError == EMCallStateChangeListener.CallError.REJECTED) {
                MLLog.i("对方已拒绝" + callError);
                mCallStatusView.setText(R.string.ml_call_reject);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_NORESPONSE) {
                MLLog.i("对方未接听" + callError);
                mCallStatusView.setText(R.string.ml_call_not_answer);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_TRANSPORT) {
                MLLog.i("连接建立失败" + callError);
                mCallStatusView.setText(R.string.ml_call_connection_fail);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_LOCAL_VERSION_SMALLER) {
                MLLog.i("双方通讯协议不同" + callError);
                mCallStatusView.setText(R.string.ml_call_local_version_smaller);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_PEER_VERSION_SMALLER) {
                MLLog.i("双方通讯协议不同" + callError);
                mCallStatusView.setText(R.string.ml_call_opposite_version_smaller);
            } else {
                MLLog.i("通话已结束，时长：%s，error %s", "10:35", callError);
                mCallStatusView.setText(R.string.ml_call_disconnected);
            }

            // 结束通话时取消通话状态监听
            MLEasemobHelper.getInstance().removeCallStateChangeListener();
            // 结束通话关闭界面
            onFinish();
            break;
        case NETWORK_UNSTABLE:
            if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                MLLog.i("没有通话数据" + callError);
                mCallStatusView.setText(R.string.ml_call_no_data);
            } else {
                MLLog.i("网络不稳定" + callError);
                mCallStatusView.setText(R.string.ml_call_network_unsatble);
            }
            break;
        case NETWORK_NORMAL:
            MLLog.i("网络正常");
            mCallStatusView.setText(R.string.ml_call_network_normal);
            break;
        case VIDEO_PAUSE:
            MLLog.i("视频传输已暂停");
            mCallStatusView.setText(R.string.ml_call_video_pause);
            break;
        case VIDEO_RESUME:
            MLLog.i("视频传输已恢复");
            mCallStatusView.setText(R.string.ml_call_video_resume);
            break;
        case VOICE_PAUSE:
            MLLog.i("语音传输已暂停");
            mCallStatusView.setText(R.string.ml_call_voice_pause);
            break;
        case VOICE_RESUME:
            MLLog.i("语音传输已恢复");
            mCallStatusView.setText(R.string.ml_call_voice_resume);
            break;
        default:
            break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
