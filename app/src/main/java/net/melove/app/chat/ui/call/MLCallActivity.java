package net.melove.app.chat.ui.call;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;

import android.widget.Chronometer;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.app.chat.R;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.ui.MLBaseActivity;
import net.melove.app.chat.util.MLDateUtil;

/**
 * Created by lzan13 on 2016/8/8.
 * 通话界面的父类，做一些音视频通话的通用操作
 */
public class MLCallActivity extends MLBaseActivity {

    // 呼叫方名字
    protected String mChatId;
    // 是否是拨打进来的电话
    protected boolean isInComingCall;

    // 通话计时控件
    protected Chronometer mChronometer;

    // 通话结束状态，用来保存通话结束后的消息提示
    protected int mCallStatus;
    // 通话类型，用于区分语音和视频通话 0 代表视频，1 代表语音
    protected int mCallType;

    // 音频管理器
    protected AudioManager mAudioManager;
    protected SoundPool mSoundPool;
    protected int streamID;
    protected int loadId;

    // 振动器
    protected Vibrator mVibrator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置通话界面属性，保持屏幕常亮，关闭输入法，以及解锁
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    /**
     * 初始化界面方法，做一些界面的初始化操作
     */
    protected void initView() {
        mActivity = this;

        // 获取通话对方的username
        mChatId = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        isInComingCall = getIntent().getBooleanExtra(MLConstants.ML_EXTRA_IS_INCOMING_CALL, false);
        // 默认通话状态为自己取消
        mCallStatus = MLConstants.ML_CALL_CANCEL;

        // 收到呼叫或者呼叫对方时初始化通话状态监听
        MLHyphenate.getInstance().registerCallStateListener();

        // 初始化振动器
        mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        // 初始化音频管理器
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        // 根据系统版本不同选择不同的方式初始化音频播放工具
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createSoundPoolWithBuilder();
        } else {
            createSoundPoolWithConstructor();
        }
        if (MLCallStatus.getInstance().getCallState() == MLCallStatus.CALL_STATUS_NORMAL) {
            // 根据通话呼叫与被呼叫加载不同的提示音效
            if (isInComingCall) {
                loadId = mSoundPool.load(mActivity, R.raw.sound_call_incoming, 1);
            } else {
                loadId = mSoundPool.load(mActivity, R.raw.sound_calling, 1);
            }
            //  设置资源加载监听
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    playCallSound();
                }
            });
        }
    }

    /**
     * 通话结束，保存一条记录通话的消息
     */
    protected void saveCallMessage() {
        EMMessage message = null;
        EMTextMessageBody body = null;
        String content = null;
        if (isInComingCall) {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setFrom(mChatId);
        } else {
            message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setReceipt(mChatId);
        }

        switch (mCallStatus) {
            case MLConstants.ML_CALL_ACCEPTED:
                // 通话正常结束，要加上通话时间
                content = mChronometer.getText().toString();
                break;
            case MLConstants.ML_CALL_CANCEL:
                // 自己取消
                content = mActivity.getString(R.string.ml_call_cancel);
                break;
            case MLConstants.ML_CALL_CANCEL_INCOMING_CALL:
                // 对方取消
                content = mActivity.getString(R.string.ml_call_cancel_is_incoming);
                break;
            case MLConstants.ML_CALL_BUSY:
                // 对方正忙
                content = mActivity.getString(R.string.ml_call_busy);
                break;
            case MLConstants.ML_CALL_OFFLINE:
                // 对方不在线
                content = mActivity.getString(R.string.ml_call_not_online);
                break;
            case MLConstants.ML_CALL_REJECT_INCOMING_CALL:
                // 自己已拒绝
                content = mActivity.getString(R.string.ml_call_reject_is_incoming);
                break;
            case MLConstants.ML_CALL_REJECT:
                // 对方拒绝
                content = mActivity.getString(R.string.ml_call_reject);
                break;
            case MLConstants.ML_CALL_NORESPONSE:
                // 对方无响应
                content = mActivity.getString(R.string.ml_call_no_response);
                break;
            case MLConstants.ML_CALL_TRANSPORT:
                // 建立连接失败
                content = mActivity.getString(R.string.ml_call_connection_fail);
                break;
            case MLConstants.ML_CALL_VERSION_DIFFERENT:
                // 双方通话协议版本不同
                content = mActivity.getString(R.string.ml_call_not_online);
                break;
            default:
                // 默认为取消
                content = mActivity.getString(R.string.ml_call_cancel);
                break;
        }
        body = new EMTextMessageBody(content);
        message.addBody(body);
        message.setStatus(EMMessage.Status.SUCCESS);
        message.setMsgId(MLDateUtil.getCurrentMillisecond() + "");
        if (mCallType == 0) {
            message.setAttribute(MLConstants.ML_ATTR_CALL_VIDEO, true);
        } else {
            message.setAttribute(MLConstants.ML_ATTR_CALL_VOICE, true);
        }
        message.setUnread(false);
        // 调用sdk的保存消息方法
        EMClient.getInstance().chatManager().saveMessage(message);
    }

    /**
     * 调用系统振动
     */
    protected void vibrate() {
        if (mVibrator == null) {
            mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(mActivity.getResources().getInteger(R.integer.ml_time_vibrate));
    }

    /**
     * 播放呼叫通话提示音
     */
    protected void playCallSound() {
        if (!mAudioManager.isSpeakerphoneOn()) {
            mAudioManager.setSpeakerphoneOn(true);
        }
        // 设置音频管理器音频模式为铃音模式
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        // 播放提示音，返回一个播放的音频id，等下停止播放需要用到
        if (mSoundPool != null) {
            streamID = mSoundPool.play(loadId, // 播放资源id；就是加载到SoundPool里的音频资源顺序，这里就是第一个，也是唯一的一个
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
    protected void stopCallSound() {
        if (mSoundPool != null) {
            // 停止播放音效
            mSoundPool.stop(streamID);
            // 释放资源
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    /**
     * 当系统的 SDK 版本高于21时，使用另一种方式创建 SoundPool
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP) protected void createSoundPoolWithBuilder() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                // 设置音频要用在什么地方，这里选择电话通知铃音
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        // 使用 build 的方式实例化 SoundPool
        mSoundPool =
                new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
    }

    /**
     * 使用构造函数实例化 SoundPool
     */
    @SuppressWarnings("deprecation") protected void createSoundPoolWithConstructor() {
        // 老版本使用构造函数方式实例化 SoundPool，MODE 设置为铃音 MODE_RINGTONE
        mSoundPool = new SoundPool(1, AudioManager.MODE_RINGTONE, 0);
    }

    /**
     * 销毁界面时做一些自己的操作
     */
    @Override protected void onFinish() {
        // 关闭音效并释放资源
        stopCallSound();
        super.onFinish();
    }

    /**
     * 重载返回键
     */
    @Override public void onBackPressed() {
        // super.onBackPressed();

    }
}
