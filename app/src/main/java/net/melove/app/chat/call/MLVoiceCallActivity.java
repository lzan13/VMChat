package net.melove.app.chat.call;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMCallStateChangeListener.CallError;
import com.hyphenate.chat.EMCallStateChangeListener.CallState;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.util.MLBitmapUtil;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.ui.widget.MLImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create by lzan13 2016/8/8
 * 语音通话界面
 */
public class MLVoiceCallActivity extends MLCallActivity {

    // 使用 ButterKnife 注解的方式获取控件
    @BindView(R.id.img_background) ImageView mCallBackgroundView;
    @BindView(R.id.text_call_status) TextView mCallStatusView;
    @BindView(R.id.img_call_avatar) MLImageView mAvatarView;
    @BindView(R.id.text_call_username) TextView mUsernameView;
    @BindView(R.id.btn_exit_full_screen) ImageButton mExitFullScreenBtn;
    @BindView(R.id.btn_mic_switch) ImageButton mMicSwitch;
    @BindView(R.id.btn_speaker_switch) ImageButton mSpeakerSwitch;
    @BindView(R.id.btn_record_switch) ImageButton mRecordSwitch;
    @BindView(R.id.fab_reject_call) FloatingActionButton mRejectCallFab;
    @BindView(R.id.fab_end_call) FloatingActionButton mEndCallFab;
    @BindView(R.id.fab_answer_call) FloatingActionButton mAnswerCallFab;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        ButterKnife.bind(this);

        initView();
    }

    /**
     * 重载父类方法,实现一些当前通话的操作，
     */
    @Override protected void initView() {
        super.initView();

        // 设置通话类型为语音
        mCallType = 1;

        mChronometer = (Chronometer) findViewById(R.id.chronometer_call_time);

        mCallBackgroundView.setImageResource(R.mipmap.ic_character_penguin);

        // 设置按钮状态
        mMicSwitch.setActivated(MLCallStatus.getInstance().isMic());
        mSpeakerSwitch.setActivated(MLCallStatus.getInstance().isSpeaker());
        mRecordSwitch.setActivated(MLCallStatus.getInstance().isRecord());

        // 设置对方的名字以及界面其他控件的显示
        mUsernameView.setText(mChatId);
        // 判断下当前是否正在进行通话中
        if (MLCallStatus.getInstance().getCallState() == MLCallStatus.CALL_STATUS_NORMAL) {
            // 设置通话呼入呼出状态
            MLCallStatus.getInstance().setInComing(isInComingCall);
            if (isInComingCall) {
                // 收到通话请求，设置通话状态为被呼叫中
                MLCallStatus.getInstance()
                        .setCallState(MLCallStatus.CALL_STATUS_CONNECTING_INCOMING);
                // 设置通话状态为对方申请通话
                mCallStatusView.setText(R.string.ml_call_connected_is_incoming);
                mRejectCallFab.setVisibility(View.VISIBLE);
                mEndCallFab.setVisibility(View.GONE);
                mAnswerCallFab.setVisibility(View.VISIBLE);
            } else {
                // 收到通话请求，设置通话状态为呼叫中
                MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_CONNECTING);
                // 设置通话状态为正在呼叫中
                mCallStatusView.setText(R.string.ml_call_connecting);
                mRejectCallFab.setVisibility(View.GONE);
                mEndCallFab.setVisibility(View.VISIBLE);
                mAnswerCallFab.setVisibility(View.GONE);
                // 自己是主叫方，调用呼叫方法
                makeCall();
            }
        } else if (MLCallStatus.getInstance().getCallState()
                == MLCallStatus.CALL_STATUS_CONNECTING) {
            // 设置通话呼入呼出状态
            isInComingCall = MLCallStatus.getInstance().isInComing();
            // 设置通话状态为正在呼叫中
            mCallStatusView.setText(R.string.ml_call_connecting);
            mRejectCallFab.setVisibility(View.GONE);
            mEndCallFab.setVisibility(View.VISIBLE);
            mAnswerCallFab.setVisibility(View.GONE);
        } else if (MLCallStatus.getInstance().getCallState()
                == MLCallStatus.CALL_STATUS_CONNECTING_INCOMING) {
            // 设置通话呼入呼出状态
            isInComingCall = MLCallStatus.getInstance().isInComing();
            // 设置通话状态为对方申请通话
            mCallStatusView.setText(R.string.ml_call_connected_is_incoming);
            mRejectCallFab.setVisibility(View.VISIBLE);
            mEndCallFab.setVisibility(View.GONE);
            mAnswerCallFab.setVisibility(View.VISIBLE);
        } else {
            // 设置通话呼入呼出状态
            isInComingCall = MLCallStatus.getInstance().isInComing();
            // 再次打开要设置状态为正常通话状态
            mCallStatus = MLConstants.ML_CALL_ACCEPTED;
            mCallStatusView.setText(R.string.ml_call_accepted);
            mRejectCallFab.setVisibility(View.GONE);
            mEndCallFab.setVisibility(View.VISIBLE);
            mAnswerCallFab.setVisibility(View.GONE);
        }
    }

    /**
     * 开始呼叫对方
     */
    private void makeCall() {
        try {
            EMClient.getInstance().callManager().makeVoiceCall(mChatId);
        } catch (EMServiceNotReadyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 界面控件点击监听器
     */
    @OnClick({
            R.id.btn_exit_full_screen, R.id.btn_mic_switch, R.id.btn_speaker_switch,
            R.id.btn_record_switch, R.id.fab_reject_call, R.id.fab_end_call, R.id.fab_answer_call
    }) void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit_full_screen:
                // 最小化通话界面
                exitFullScreen();
                break;
            case R.id.btn_mic_switch:
                // 麦克风开关
                onMicrophone();
                break;
            case R.id.btn_speaker_switch:
                // 扬声器开关
                onSpeaker();
                break;
            case R.id.btn_record_switch:
                // 录制开关
                recordCall();
                break;
            case R.id.fab_reject_call:
                // 拒绝接听通话
                rejectCall();
                break;
            case R.id.fab_end_call:
                // 结束通话
                endCall();
                break;
            case R.id.fab_answer_call:
                // 接听通话
                answerCall();
                break;
        }
    }

    /**
     * 退出全屏通话界面
     */
    private void exitFullScreen() {
        // 振动反馈
        vibrate();
        // 让应用回到桌面
        //        mActivity.moveTaskToBack(true);
        mActivity.finish();
    }

    /**
     * 麦克风开关，主要调用环信语音数据传输方法
     */
    private void onMicrophone() {
        // 振动反馈
        vibrate();
        try {
            // 根据麦克风开关是否被激活来进行判断麦克风状态，然后进行下一步操作
            if (mMicSwitch.isActivated()) {
                // 暂停语音数据的传输
                EMClient.getInstance().callManager().pauseVoiceTransfer();
                // 设置按钮状态
                mMicSwitch.setActivated(false);
                MLCallStatus.getInstance().setMic(false);
            } else {
                // 恢复语音数据的传输
                EMClient.getInstance().callManager().resumeVoiceTransfer();
                // 设置按钮状态
                mMicSwitch.setActivated(true);
                MLCallStatus.getInstance().setMic(true);
            }
        } catch (HyphenateException e) {
            MLLog.e("exception code: %d, %s", e.getErrorCode(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 扬声器开关
     */
    private void onSpeaker() {
        // 振动反馈
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
        // 振动反馈
        vibrate();
        Snackbar.make(getRootView(), R.string.ml_toast_unrealized, Snackbar.LENGTH_SHORT).show();
        // 根据开关状态决定是否开启录制
        if (mRecordSwitch.isActivated()) {
            // 设置按钮状态
            mRecordSwitch.setActivated(false);
            MLCallStatus.getInstance().setRecord(false);
        } else {
            // 设置按钮状态
            mRecordSwitch.setActivated(true);
            MLCallStatus.getInstance().setRecord(true);
        }
    }

    /**
     * 拒绝通话
     */
    private void rejectCall() {
        // 振动反馈
        vibrate();
        // 结束通话时取消通话状态监听
        MLHyphenate.getInstance().removeCallStateListener();
        // 拒绝通话后关闭通知铃音
        stopCallSound();
        try {
            // 调用 SDK 的拒绝通话方法
            EMClient.getInstance().callManager().rejectCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            Snackbar.make(getRootView(), "error-" + e.getMessage() + "-" + e.getErrorCode(),
                    Snackbar.LENGTH_SHORT).show();
        }
        // 通话结束，重置通话状态
        MLCallStatus.getInstance().reset();
        // 拒绝通话设置通话状态为自己拒绝
        mCallStatus = MLConstants.ML_CALL_REJECT_INCOMING_CALL;
        // 保存一条通话消息
        saveCallMessage();
        // 结束界面
        onFinish();
    }

    /**
     * 结束通话
     */
    private void endCall() {
        // 振动反馈
        vibrate();
        // 结束通话时取消通话状态监听
        MLHyphenate.getInstance().removeCallStateListener();
        // 结束通话后关闭通知铃音
        stopCallSound();
        try {
            // 调用 SDK 的结束通话方法
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            Snackbar.make(getRootView(), "error-" + e.getMessage() + "-" + e.getErrorCode(),
                    Snackbar.LENGTH_SHORT).show();
        }
        // 通话结束，重置通话状态
        MLCallStatus.getInstance().reset();
        // 挂断电话调用保存消息方法
        saveCallMessage();
        // 结束界面
        onFinish();
    }

    /**
     * 接听通话
     */
    private void answerCall() {
        // 振动反馈
        vibrate();
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
            // 设置通话状态为正常结束
            mCallStatus = MLConstants.ML_CALL_ACCEPTED;
            // 设置通话状态为已接通
            MLCallStatus.getInstance().setCallState(MLCallStatus.CALL_STATUS_ACCEPTED);
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
            Snackbar.make(getRootView(), "接听通话失败：error-" + e.getErrorCode() + "-" + e.getMessage(),
                    Snackbar.LENGTH_SHORT).show();
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
        // 设置按钮状态
        mSpeakerSwitch.setActivated(true);
        MLCallStatus.getInstance().setSpeaker(true);
        // 检查是否已经开启扬声器
        if (!mAudioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            mAudioManager.setSpeakerphoneOn(true);
        }
        // 设置声音模式为正常模式
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     * 关闭扬声器，即开启听筒播放模式
     * 同上边{@link #openSpeaker()}
     */
    private void closeSpeaker() {
        // 设置按钮状态
        mSpeakerSwitch.setActivated(false);
        MLCallStatus.getInstance().setSpeaker(false);
        // 检查是否已经开启扬声器
        if (mAudioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            mAudioManager.setSpeakerphoneOn(false);
        }
        // 设置声音模式为通讯模式，即使用听筒播放
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 实现订阅方法，订阅全局监听发来的通话状态事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(MLCallEvent event) {
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
                // 通话已接通，设置通话状态为正常状态
                mCallStatus = MLConstants.ML_CALL_ACCEPTED;
                // 开始计时
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();
                break;
            case DISCONNECTED: // 通话已中断
                MLLog.i("通话已结束" + callError);
                // 停止计时
                mChronometer.stop();
                mCallStatusView.setText(R.string.ml_call_disconnected);
                if (callError == CallError.ERROR_UNAVAILABLE) {
                    MLLog.i("对方不在线" + callError);
                    // 设置通话状态为对方不在线
                    mCallStatus = MLConstants.ML_CALL_OFFLINE;
                    mCallStatusView.setText(R.string.ml_call_not_online);
                } else if (callError == CallError.ERROR_BUSY) {
                    MLLog.i("对方正忙" + callError);
                    // 设置通话状态为对方在忙
                    mCallStatus = MLConstants.ML_CALL_BUSY;
                    mCallStatusView.setText(R.string.ml_call_busy);
                } else if (callError == CallError.REJECTED) {
                    MLLog.i("对方已拒绝" + callError);
                    // 设置通话状态为对方已拒绝
                    mCallStatus = MLConstants.ML_CALL_REJECT;
                    mCallStatusView.setText(R.string.ml_call_reject);
                } else if (callError == CallError.ERROR_NORESPONSE) {
                    MLLog.i("对方未响应，可能手机不在身边" + callError);
                    // 设置通话状态为对方未响应
                    mCallStatus = MLConstants.ML_CALL_NORESPONSE;
                    mCallStatusView.setText(R.string.ml_call_no_response);
                } else if (callError == CallError.ERROR_TRANSPORT) {
                    MLLog.i("连接建立失败" + callError);
                    // 设置通话状态为建立连接失败
                    mCallStatus = MLConstants.ML_CALL_TRANSPORT;
                    mCallStatusView.setText(R.string.ml_call_connection_fail);
                } else if (callError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED) {
                    MLLog.i("双方通讯协议不同" + callError);
                    // 设置通话状态为双方协议不同
                    mCallStatus = MLConstants.ML_CALL_VERSION_DIFFERENT;
                    mCallStatusView.setText(R.string.ml_call_local_version_smaller);
                } else if (callError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                    MLLog.i("双方通讯协议不同" + callError);
                    // 设置通话状态为双方协议不同
                    mCallStatus = MLConstants.ML_CALL_VERSION_DIFFERENT;
                    mCallStatusView.setText(R.string.ml_call_opposite_version_smaller);
                } else {
                    MLLog.i("通话已结束，时长：%s，error %s", "10:35", callError);
                    // 根据当前状态判断是正常结束，还是对方取消通话
                    if (mCallStatus == MLConstants.ML_CALL_CANCEL) {
                        // 设置通话状态
                        mCallStatus = MLConstants.ML_CALL_CANCEL_INCOMING_CALL;
                    }
                    mCallStatusView.setText(R.string.ml_call_cancel_is_incoming);
                }
                // 通话结束保存消息
                saveCallMessage();
                // 结束通话时取消通话状态监听
                MLHyphenate.getInstance().removeCallStateListener();
                // 结束通话关闭界面
                onFinish();
                break;
            case NETWORK_UNSTABLE:
                if (callError == EMCallStateChangeListener.CallError.ERROR_NO_DATA) {
                    MLLog.i("没有通话数据" + callError);
                    mCallStatusView.setText(R.string.ml_call_no_data);
                } else {
                    MLLog.i("网络不稳定" + callError);
                    mCallStatusView.setText(R.string.ml_call_network_unstable);
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

    @Override protected void onFinish() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        super.onFinish();
    }

    @Override protected void onResume() {
        super.onResume();

        // 添加控件监听，监听背景图加载是否完成
        mCallBackgroundView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override public boolean onPreDraw() {
                        // 移除控件加载图片监听
                        mCallBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                        MLLog.i("blur bitmap - 0 - %d", MLDateUtil.getCurrentMillisecond());
                        mCallBackgroundView.setDrawingCacheEnabled(true);
                        Bitmap bitmap = mCallBackgroundView.getDrawingCache();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            mCallBackgroundView.setImageBitmap(MLBitmapUtil.stackBlurBitmap(bitmap,
                                    mActivity.getResources().getInteger(R.integer.ml_img_blur_16),
                                    mActivity.getResources().getInteger(R.integer.ml_img_blur_8),
                                    false));
                        } else {
                            mCallBackgroundView.setImageBitmap(
                                    MLBitmapUtil.rsBlurBitmp(mActivity, bitmap,
                                            mActivity.getResources()
                                                    .getInteger(R.integer.ml_img_blur_16), mActivity
                                                    .getResources()
                                                    .getInteger(R.integer.ml_img_blur_8)));
                        }
                        mCallBackgroundView.setDrawingCacheEnabled(false);
                        MLLog.i("blur bitmap - 1 - %d", MLDateUtil.getCurrentMillisecond());
                        return false;
                    }
                });
    }

    @Override protected void onStart() {
        super.onStart();
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }
}
