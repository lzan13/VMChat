package net.melove.app.chat.conversation.call;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;


import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMCallStateChangeListener.CallError;
import com.hyphenate.chat.EMCallStateChangeListener.CallState;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.MLEasemobHelper;
import net.melove.app.chat.application.eventbus.MLCallEvent;
import net.melove.app.chat.communal.base.MLBaseActivity;
import net.melove.app.chat.communal.util.MLLog;
import net.melove.app.chat.communal.widget.MLImageView;
import net.melove.app.chat.communal.widget.MLToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MLVoiceCallActivity extends MLBaseActivity {

    // 呼叫方名字
    private String username;
    // 是否是拨打进来的电话
    private boolean isInComingCall;

    // 音频管理器
    private AudioManager mAudioManager;
    private SoundPool mSoundPool;
    private int soundId;
    private int loadId;

    // 界面控件
    private TextView mCallStatusView;
    // 显示对方头像的控件
    private MLImageView mAvatarView;
    // 显示对方名字
    private TextView mUsernameView;
    // 通话界面最小化按钮
    private ImageButton mExitFullScreenBtn;
    // 麦克风开关
    private CheckBox mMicCheckBox;
    // 扬声器开关
    private CheckBox mSpeakerCheckBox;
    // 录制开关
    private CheckBox mRecordCheckBox;
    // 拒绝接听按钮
    private FloatingActionButton mRejectCallFab;
    // 结束通话按钮
    private FloatingActionButton mEndCallFab;
    // 接听通话按钮
    private FloatingActionButton mAnswerCallFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        initView();

    }

    /**
     * 初始化界面方法，做一些界面的初始化操作
     */
    private void initView() {
        mActivity = this;

        username = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        isInComingCall = getIntent().getBooleanExtra(MLConstants.ML_EXTRA_IS_INCOMING_CALL, false);

        // 初始化界面控件
        mCallStatusView = (TextView) findViewById(R.id.ml_text_call_status);
        mAvatarView = (MLImageView) findViewById(R.id.ml_img_call_avatar);
        mUsernameView = (TextView) findViewById(R.id.ml_text_call_username);

        // 最小化按钮
        mExitFullScreenBtn = (ImageButton) findViewById(R.id.ml_btn_exit_full_screen);
        mMicCheckBox = (CheckBox) findViewById(R.id.ml_checkbox_mic);
        mSpeakerCheckBox = (CheckBox) findViewById(R.id.ml_checkbox_speaker);
        mRecordCheckBox = (CheckBox) findViewById(R.id.ml_checkbox_record);
        mRejectCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_reject_call);
        mEndCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_end_call);
        mAnswerCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_answer_call);

        // 设置按钮的点击监听
        mExitFullScreenBtn.setOnClickListener(viewListener);
        mMicCheckBox.setOnClickListener(viewListener);
        mSpeakerCheckBox.setOnClickListener(viewListener);
        mRecordCheckBox.setOnClickListener(viewListener);
        mRejectCallFab.setOnClickListener(viewListener);
        mEndCallFab.setOnClickListener(viewListener);
        mAnswerCallFab.setOnClickListener(viewListener);

        // 设置对方的名字以及界面其他控件的显示
        mUsernameView.setText(username);
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
            makeCall();
        }


        // 收到呼叫或者呼叫对方时初始化通话状态监听
        MLEasemobHelper.getInstance().initCallStateChangeListener();

        // 初始化音频管理器
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        // 根据系统版本不同选择不同的方式初始化音频播放工具
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createSoundPoolWithBuilder();
        } else {
            createSoundPoolWithConstructor();
        }
        // 根据通话呼叫与被呼叫加载不同的提示音效
        if (isInComingCall) {
            loadId = mSoundPool.load(mActivity, R.raw.ml_call_incoming, 1);
        } else {
            loadId = mSoundPool.load(mActivity, R.raw.ml_calling, 1);
        }
        playCallSound();
    }

    /**
     * 开始呼叫对方
     */
    private void makeCall() {
        try {
            EMClient.getInstance().callManager().makeVoiceCall(username);
        } catch (EMServiceNotReadyException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.ml_btn_exit_full_screen:
                // 最小化通话界面
                break;
            case R.id.ml_checkbox_mic:
                // 麦克风开关
                onMicrophone();
                break;
            case R.id.ml_checkbox_speaker:
                // 扬声器开关
                onSpeaker();
                break;
            case R.id.ml_checkbox_record:
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
     * 麦克风开关，主要调用环信语音数据传输方法
     */
    private void onMicrophone() {
        if (mMicCheckBox.isChecked()) {
            // 暂停语音数据的传输
            EMClient.getInstance().callManager().pauseVoiceTransfer();
        } else {
            // 回复语音数据的传输
            EMClient.getInstance().callManager().resumeVoiceTransfer();
        }
    }

    /**
     * 扬声器开关
     */
    private void onSpeaker() {
        // 根据复选框状态决定打开还是关闭扬声器
        if (mSpeakerCheckBox.isChecked()) {
            closeSpeaker();
        } else {
            openSpeaker();
        }
    }

    /**
     * 录制通话内容 TODO 后期实现
     */
    private void recordCall() {

    }

    /**
     * 拒绝通话
     */
    private void rejectCall() {
        try {
            // 调用 SDK 的拒绝通话方法
            EMClient.getInstance().callManager().rejectCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            MLToast.errorToast("拒绝通话失败：error-" + e.getErrorCode() + "-" + e.getMessage()).show();
        }
        // 结束通话时取消通话状态监听
        MLEasemobHelper.getInstance().removeCallStateChangeListener();
        onFinish();
    }

    /**
     * 结束通话
     */
    private void endCall() {
        try {
            // 调用 SDK 的结束通话方法
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            MLToast.errorToast("结束通话失败：error-" + e.getErrorCode() + "-" + e.getMessage()).show();
        }
        // 结束通话时取消通话状态监听
        MLEasemobHelper.getInstance().removeCallStateChangeListener();
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
        // 检查是否已经开启扬声器
        if (mAudioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            mAudioManager.setSpeakerphoneOn(false);
        }
        // 设置声音模式为通讯模式，即使用听筒播放
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 播放呼叫通话提示音
     */
    private void playCallSound() {
        mAudioManager.setSpeakerphoneOn(true);
        // 设置音频管理器音频模式为铃音模式
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        // 播放提示音，返回一个播放的音频id，等下停止播放需要用到
        soundId = mSoundPool.play(
                loadId, // 播放资源id；就是加载到SoundPool里的音频资源顺序，这里就是第一个，也是唯一的一个
                0.5f,   // 左声道音量
                0.5f,   // 右声道音量
                1,      // 优先级，这里至于一个提示音，不需要关注
                -1,     // 是否循环；0 不循环，-1 循环
                1);     // 播放比率；从0.5-2，一般设置为1，表示正常播放
    }

    /**
     * 实现订阅方法，订阅全局监听发来的通话状态事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLCallEvent event) {
        CallError callError = event.getCallError();
        CallState callState = event.getCallState();

        switch (callState) {
        case CONNECTING: // 正在呼叫对方
            MLLog.i("正在呼叫对方" + callError);
            break;
        case CONNECTED: // 正在等待对方接受呼叫申请（对方申请与你进行通话）
            MLLog.i("正在等待对方接受呼叫申请" + callError);
            break;
        case ACCEPTED: // 通话已接通
            MLLog.i("通话已接通");
            // 电话接通，停止播放提示音
            mSoundPool.stop(soundId);
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

    /**
     * 当系统的 SDK 版本高于21时，使用另一种方式创建 SoundPool
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createSoundPoolWithBuilder() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                // 设置音频要用在什么地方，这里选择电话通知铃音
                .setUsage(AudioAttributes.USAGE_UNKNOWN)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        // 使用 build 的方式实例化 SoundPool
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
    }

    /**
     * 使用构造函数实例化 SoundPool
     */
    @SuppressWarnings("deprecation")
    protected void createSoundPoolWithConstructor() {
        // 老版本使用构造函数方式实例化 SoundPool，MODE 设置为铃音 MODE_RINGTONE
        mSoundPool = new SoundPool(1, AudioManager.MODE_RINGTONE, 0);
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
