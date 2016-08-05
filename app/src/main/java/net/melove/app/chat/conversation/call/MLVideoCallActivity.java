package net.melove.app.chat.conversation.call;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLEasemobHelper;
import net.melove.app.chat.application.eventbus.MLCallEvent;
import net.melove.app.chat.communal.base.MLBaseActivity;
import net.melove.app.chat.communal.util.MLLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MLVideoCallActivity extends MLBaseActivity {


    // 呼叫方名字
    private String username;
    // 是否是拨打进来的电话
    private boolean isInComingCall;
    // 视频通话帮助类
    private EMCallManager.EMVideoCallHelper mVideoCallHelper;

    // 界面控件
    private TextView mCallStatusView;
    // 通话界面最小化按钮
    private ImageButton mExitFullScreenBtn;
    // 麦克风开关
    private CheckBox mOnOffMicBtn;
    // 摄像头开关
    private CheckBox mOnOffCamera;
    // 扬声器开关
    private CheckBox mOnOffSpeakerBtn;
    // 录制开关
    private CheckBox mOnOffRecordBtn;
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

    private void initView() {
        mActivity = this;


        // 收到呼叫或者呼叫对方时初始化通话状态监听
        MLEasemobHelper.getInstance().setCallStateChangeListener();

        mVideoCallHelper = EMClient.getInstance().callManager().getVideoCallHelper();
        // 设置自动码率  TODO 新的针对音视频优化的 SDK 不需要调用，默认直接开启
        mVideoCallHelper.setAdaptiveVideoFlag(true);


        // 初始化界面控件
        mCallStatusView = (TextView) findViewById(R.id.ml_text_call_status);
        mExitFullScreenBtn = (ImageButton) findViewById(R.id.ml_btn_exit_full_screen);
        mOnOffMicBtn = (CheckBox) findViewById(R.id.ml_btn_mic_switch);
        mOnOffCamera = (CheckBox) findViewById(R.id.ml_checkbox_camera);
        mOnOffSpeakerBtn = (CheckBox) findViewById(R.id.ml_speaker_switch);
        mOnOffRecordBtn = (CheckBox) findViewById(R.id.ml_record_switch);
        mRejectCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_reject_call);
        mEndCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_end_call);
        mAnswerCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_answer_call);

        // 设置按钮的点击监听
        mExitFullScreenBtn.setOnClickListener(viewListener);
        mOnOffMicBtn.setOnClickListener(viewListener);
        mOnOffCamera.setOnClickListener(viewListener);
        mOnOffSpeakerBtn.setOnClickListener(viewListener);
        mOnOffRecordBtn.setOnClickListener(viewListener);
        mRejectCallFab.setOnClickListener(viewListener);
        mEndCallFab.setOnClickListener(viewListener);
        mAnswerCallFab.setOnClickListener(viewListener);

    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.ml_btn_mic_switch:
                // 麦克风开关
                break;
            case R.id.ml_checkbox_camera:
                // 摄像头开关
                break;
            case R.id.ml_speaker_switch:
                // 扬声器开关
                break;
            case R.id.ml_record_switch:
                // 录制开关
                break;
            case R.id.ml_btn_fab_reject_call:
                // 拒绝接听通话
                // 结束通话时取消通话状态监听
                MLEasemobHelper.getInstance().removeCallStateChangeListener();
                break;
            case R.id.ml_btn_fab_end_call:
                // 结束通话
                // 结束通话时取消通话状态监听
                MLEasemobHelper.getInstance().removeCallStateChangeListener();
                break;
            case R.id.ml_btn_fab_answer_call:
                // 接听通话
                break;
            }
        }
    };


    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLCallEvent event) {
        EMCallStateChangeListener.CallError callError = event.getCallError();
        EMCallStateChangeListener.CallState callState = event.getCallState();

        switch (callState) {
        case CONNECTING: // 正在呼叫对方
            MLLog.i("正在呼叫对方" + callError);
            break;
        case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
            MLLog.i("正在等待对方接受呼叫申请" + callError);
            break;
        case ACCEPTED: // 通话已接通
            MLLog.i("通话已接通");
            break;
        case DISCONNNECTED: // 通话已中断
            MLLog.i("通话已中断" + callError);

            // 结束通话时取消通话状态监听
            MLEasemobHelper.getInstance().removeCallStateChangeListener();

            if (callError == EMCallStateChangeListener.CallError.ERROR_INAVAILABLE) {
                MLLog.i("对方不在线" + callError);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_BUSY) {
                MLLog.i("对方正在通话中" + callError);
            } else if (callError == EMCallStateChangeListener.CallError.REJECTED) {
                MLLog.i("对方已拒绝" + callError);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_NORESPONSE) {
                MLLog.i("对方未接听" + callError);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_TRANSPORT) {
                MLLog.i("连接建立失败，等下再呼叫吧" + callError);
            } else if (callError == EMCallStateChangeListener.CallError.ERROR_LOCAL_VERSION_SMALLER
                    || callError == EMCallStateChangeListener.CallError.ERROR_PEER_VERSION_SMALLER) {
                MLLog.i("双方通讯协议不同" + callError);
            } else {
                MLLog.i("通话已结束，时长：%s，error %s", "10:35", callError);
            }
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
