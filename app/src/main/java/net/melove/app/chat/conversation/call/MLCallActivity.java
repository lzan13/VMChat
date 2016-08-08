package net.melove.app.chat.conversation.call;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.MLEasemobHelper;
import net.melove.app.chat.communal.base.MLBaseActivity;

/**
 * Created by lzan13 on 2016/8/8.
 * 通话界面的父类，做一些音视频通话的通用操作
 */
public class MLCallActivity extends MLBaseActivity {

    // 呼叫方名字
    protected String username;
    // 是否是拨打进来的电话
    protected boolean isInComingCall;

    // 音频管理器
    protected AudioManager mAudioManager;
    protected SoundPool mSoundPool;
    protected int streamID;
    protected int loadId;

    // 振动器
    protected Vibrator mVibrator;


    /**
     * 初始化界面方法，做一些界面的初始化操作
     */
    protected void initView() {
        mActivity = this;

        // 获取通话对方的username
        username = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        isInComingCall = getIntent().getBooleanExtra(MLConstants.ML_EXTRA_IS_INCOMING_CALL, false);

        // 收到呼叫或者呼叫对方时初始化通话状态监听
        MLEasemobHelper.getInstance().setCallStateChangeListener();

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

    /**
     * 重载返回键
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed();

    }

}
