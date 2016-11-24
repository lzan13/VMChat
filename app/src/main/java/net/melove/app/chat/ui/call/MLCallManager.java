package net.melove.app.chat.ui.call;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import net.melove.app.chat.MLApplication;
import net.melove.app.chat.R;

/**
 * Created by lzan13 on 2016/11/21.
 * 全局的通化管理类，这是一个单例类，用来管理 app 通话操作
 */
public class MLCallManager {

    // 单例类实例
    private MLCallManager instance;

    // 音频管理器
    protected AudioManager mAudioManager;
    // 音频池
    protected SoundPool mSoundPool;

    protected int streamID;
    protected int loadId;

    /**
     * 私有化构造函数
     */
    private MLCallManager() {
        mAudioManager =
                (AudioManager) MLApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public MLCallManager getInstance() {
        if (instance == null) {
            instance = new MLCallManager();
        }
        return instance;
    }

    public void loadSound() {
        if (MLCallStatus.getInstance().isInComing()) {
            loadId = mSoundPool.load(MLApplication.getContext(), R.raw.sound_call_incoming, 1);
        } else {
            loadId = mSoundPool.load(MLApplication.getContext(), R.raw.sound_calling, 1);
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
}
