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
    private int streamID;
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
    private ImageButton mMicSwitch;
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
        setContentView(R.layout.activity_voice_call);

        initView();

    }

    /**
     * 初始化界面方法，做一些界面的初始化操作
     */
    private void initView() {
        mActivity = this;

        // 收到呼叫或者呼叫对方时初始化通话状态监听
        MLEasemobHelper.getInstance().setCallStateChangeListener();

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
            loadId = mSoundPool.load(mActivity, R.raw.sound_call_incoming, 1);
        } else {
            loadId = mSoundPool.load(mActivity, R.raw.sound_calling, 1);
        }
        //  设置资源加载监听
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                playCallSound();
            }
        });

        // 获取通话对方的username
        username = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        isInComingCall = getIntent().getBooleanExtra(MLConstants.ML_EXTRA_IS_INCOMING_CALL, false);

        // 初始化界面控件
        mCallStatusView = (TextView) findViewById(R.id.ml_text_call_status);
        mAvatarView = (MLImageView) findViewById(R.id.ml_img_call_avatar);
        mUsernameView = (TextView) findViewById(R.id.ml_text_call_username);

        // 最小化按钮
        mExitFullScreenBtn = (ImageButton) findViewById(R.id.ml_btn_exit_full_screen);
        mMicSwitch = (ImageButton) findViewById(R.id.ml_btn_mic_switch);
        mSpeakerSwitch = (ImageButton) findViewById(R.id.ml_speaker_switch);
        mRecordSwitch = (ImageButton) findViewById(R.id.ml_record_switch);
        mRejectCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_reject_call);
        mEndCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_end_call);
        mAnswerCallFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_answer_call);
        // 设置按钮状态
        mMicSwitch.setActivated(true);
        mSpeakerSwitch.setActivated(true);
        mRecordSwitch.setActivated(false);

        // 设置按钮的点击监听
        mExitFullScreenBtn.setOnClickListener(viewListener);
        mMicSwitch.setOnClickListener(viewListener);
        mSpeakerSwitch.setOnClickListener(viewListener);
        mRecordSwitch.setOnClickListener(viewListener);
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
            case R.id.ml_btn_mic_switch:
                // 麦克风开关
                onMicrophone();
                break;
            case R.id.ml_speaker_switch:
                // 扬声器开关
                onSpeaker();
                break;
            case R.id.ml_record_switch:
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
     * TODO 3.1.4 SDK 暂时无效
     */
    private void onMicrophone() {
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
     * 扬声器开关
     */
    private void onSpeaker() {
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
     * 播放呼叫通话提示音
     */
    private void playCallSound() {
        if (!mAudioManager.isSpeakerphoneOn()) {
            mAudioManager.setSpeakerphoneOn(true);
        }
        // 设置音频管理器音频模式为铃音模式
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        // 播放提示音，返回一个播放的音频id，等下停止播放需要用到
        if (mSoundPool != null) {
            streamID = mSoundPool.play(
                    loadId, // 播放资源id；就是加载到SoundPool里的音频资源顺序，这里就是第一个，也是唯一的一个
                    0.5f,   // 左声道音量
                    0.5f,   // 右声道音量
                    1,      // 优先级，这里至于一个提示音，不需要关注
                    -1,     // 是否循环；0 不循环，-1 循环
                    1);     // 播放比率；从0.5-2，一般设置为1，表示正常播放
        }
    }

    /**
     * 关闭音效的播放，并释放资源
     */
    private void stopCallSound() {
        if (mSoundPool != null) {
            // 停止播放音效
            mSoundPool.stop(streamID);
            // 释放资源
            mSoundPool.release();
            mSoundPool = null;
        }
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

    /**
     * 当系统的 SDK 版本高于21时，使用另一种方式创建 SoundPool
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createSoundPoolWithBuilder() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                // 设置音频要用在什么地方，这里选择电话通知铃音
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
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

    /**
     * 销毁界面时做一些自己的操作
     */
    @Override
    protected void onFinish() {
        // 关闭音效并释放资源
        stopCallSound();
        super.onFinish();
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
