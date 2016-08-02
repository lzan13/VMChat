package net.melove.app.chat.conversation.call;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
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
    private ImageButton mOnOffMicBtn;
    // 摄像头开关
    private ImageButton mOnOffCamera;
    // 扬声器开关
    private ImageButton mOnOffSpeakerBtn;
    // 录制开关
    private ImageButton mOnOffRecordBtn;
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
        MLEasemobHelper.getInstance().initCallStateChangeListener();

        mVideoCallHelper = EMClient.getInstance().callManager().getVideoCallHelper();
        //        // 设置自动码率  TODO 新的针对音视频优化的 SDK 不需要调用，默认直接开启
        mVideoCallHelper.setAdaptiveVideoFlag(true);


        // 初始化界面控件
        mCallStatusView = (TextView) findViewById(R.id.ml_text_call_status);
        mExitFullScreenBtn = (ImageButton) findViewById(R.id.ml_btn_exit_full_screen);
        mOnOffMicBtn = (ImageButton) findViewById(R.id.ml_btn_on_off_mic);
        mOnOffCamera = (ImageButton) findViewById(R.id.ml_btn_on_off_camera);
        mOnOffSpeakerBtn = (ImageButton) findViewById(R.id.ml_btn_on_off_speaker);
        mOnOffRecordBtn = (ImageButton) findViewById(R.id.ml_btn_on_off_record);
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
            case R.id.ml_btn_on_off_mic:
                // 麦克风开关
                break;
            case R.id.ml_btn_on_off_camera:
                // 摄像头开关
                break;
            case R.id.ml_btn_on_off_speaker:
                // 扬声器开关
                break;
            case R.id.ml_btn_on_off_record:
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
        final EMCallStateChangeListener.CallError callError = event.getCallError();
        switch (event.getCallState()) {
        case CONNECTING: // 正在连接对方
            MLLog.i("lzna13", "正在连接对方");
            break;
        case CONNECTED: // 双方已经建立连接
            MLLog.i("lzna13", "双方已经建立连接");
            break;
        case ACCEPTED: // 电话接通成功
            MLLog.i("lzna13", "电话接通成功");
            break;
        case DISCONNNECTED: // 电话断了
            MLLog.i("lzna13", "电话断了" + callError);
            finish();
            break;
        case NETWORK_UNSTABLE:
            runOnUiThread(new Runnable() {
                public void run() {
                    if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                        MLLog.i("lzna13", "没有通话数据" + callError);
                    } else {
                        MLLog.i("lzna13", "网络不稳定" + callError);
                    }
                }
            });
            break;
        case NETWORK_NORMAL:
            MLLog.i("lzna13", "网络正常");
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
