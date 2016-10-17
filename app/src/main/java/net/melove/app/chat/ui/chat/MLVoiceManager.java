package net.melove.app.chat.ui.chat;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;


import net.melove.app.chat.ui.widget.MLWaveformView;

import java.io.IOException;

/**
 * Created by lzan13 on 2016/8/31. 音频播放管理类
 */
public class MLVoiceManager {

    private final int MEDIA_STATUS_NORMAL = 0;
    private final int MEDIA_STATUS_PAUSING = 1;
    private final int MEDIA_STATUS_PLAYING = 2;
    // 单例类的实例
    private static MLVoiceManager instance;

    private MLVoiceCallback mVoiceCallback;

    // 媒体播放器
    private MediaPlayer mMediaPlayer;

    // 音频可视化工具，主要是为了获取音频波形信息
    private Visualizer mVisualizer;

    private EMVoiceMessageBody voiceMessageBody;

    // 是否有音频正在播放中
    private int playStatus = MEDIA_STATUS_NORMAL;
    // 当前正在播放的消息ID
    private String currentMsgId;
    // 当前播放的波形展示view
    private MLWaveformView mWaveformView;

    /**
     * 私有构造方法
     */
    private MLVoiceManager() {

    }

    /**
     * 获取单例类的实例方法
     *
     * @return
     */
    public static MLVoiceManager getInstance() {
        if (instance == null) {
            instance = new MLVoiceManager();
        }
        return instance;
    }

    /**
     * 播放
     */
    public void onPlay(EMMessage message) {
        // 如果是没有听过的语音就设置已听，并发送ACK
        if (!message.isListened()) {
            EMClient.getInstance().chatManager().setMessageListened(message);
            try {
                // 发送已读ACK，告诉对方自己已经听了语音
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        voiceMessageBody = (EMVoiceMessageBody) message.getBody();

        if (playStatus == MEDIA_STATUS_NORMAL) {
            // 如果正常，直接开始播放
            playVoice();
        } else if (playStatus == MEDIA_STATUS_PAUSING) {
            if (currentMsgId != null && currentMsgId.equals(message.getMsgId())) {
                // 从暂停状态恢复
                resumePlayVoice();
            } else {
                // 将之前暂停的停止，开始播放新的
                stopPlayVoice();
                playVoice();
            }
        } else if (playStatus == MEDIA_STATUS_PLAYING) {
            if (currentMsgId != null && currentMsgId.equals(message.getMsgId())) {
                // 点击的为正在播放的Item时 暂停播放
                pausePlayVoice();
            } else {
                // 点击的不是当前播放的，停止播放之前的，播放新点击的
                stopPlayVoice();
                playVoice();
            }
        }
        // 设置当前播放消息ID
        currentMsgId = message.getMsgId();
    }

    /**
     * 停止播放，并释放资源
     */
    public void stopPlayVoice() {
        // 设置当前状态为正常
        playStatus = MEDIA_STATUS_NORMAL;
        currentMsgId = null;

        // 释放 Visualizer
        if (mVisualizer != null) {
            // 释放音频可视化采集器
            mVisualizer.release();
            mVisualizer = null;
        }
        // 释放 MediaPlayer
        if (mMediaPlayer != null) {
            // 停止播放
            mMediaPlayer.stop();
            // 释放 MediaPlayer
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // 调用停止回调，通知实现回调的类
        if (mVoiceCallback != null) {
            mVoiceCallback.onStop();
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlayVoice() {
        // 取消Visualizer 采集数据
        unableVisualizer();
        if (mMediaPlayer != null) {
            // 暂停播放
            mMediaPlayer.pause();
            // 设置当前状态为暂停中
            playStatus = MEDIA_STATUS_PAUSING;
        } else {
            // 设置当前状态为正常
            playStatus = MEDIA_STATUS_NORMAL;
        }
    }

    /**
     * 继续播放，从暂停状态恢复
     */
    public void resumePlayVoice() {
        // 激活Visualizer 采集数据
        enableVisualizer();

        if (mMediaPlayer != null) {
            // 当处于暂停状态时，直接调用 start 开始播放，否则重新开始加载音频文件播放
            mMediaPlayer.start();
            // 设置当前状态为播放中
            playStatus = MEDIA_STATUS_PLAYING;
        } else {
            // 设置当前状态为正常
            playStatus = MEDIA_STATUS_NORMAL;
        }
    }

    /**
     * 最终播放操作
     */
    public void playVoice() {
        try {
            /**
             * 通过 new() 的方式实例化 MediaPlayer ，也可以调用 create 方法，不过 create 必须传递Uri指向的文件
             * 这里为了方便可以直接根据文件地址加载，使用 setDataSource() 的方法
             *
             * 当使用 new() 或者调用 reset() 方法时 MediaPlayer 会进入 Idle 状态
             * 这两种方法的一个重要差别就是：如果在这个状态下调用了getDuration()等方法（相当于调用时机不正确），
             * 通过reset()方法进入idle状态的话会触发OnErrorListener.onError()，并且MediaPlayer会进入Error状态；
             * 如果是新创建的MediaPlayer对象，则并不会触发onError(),也不会进入Error状态；
             */
            mMediaPlayer = new MediaPlayer();
            // 设置数据源，即要播放的音频文件，MediaPlayer 进入 Initialized 状态，必须在 Idle 状态下调用
            mMediaPlayer.setDataSource(voiceMessageBody.getLocalUrl());
            // 准备 MediaPlayer 进入 Prepared 状态
            mMediaPlayer.prepare();
            // 设置是否循环播放 默认为 false，必须在 Prepared 状态下调用
            mMediaPlayer.setLooping(false);

            // 初始化设置 Visualizer
            onSetupVisualizer();

            // 开始播放状态将变为 Started，必须在 Prepared 状态下进行
            mMediaPlayer.start();

            // 设置当前状态为播放中
            playStatus = MEDIA_STATUS_PLAYING;

            // 媒体播放结束监听
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // 调用停止播放方法，主要是为了释放资源
                    stopPlayVoice();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 Visualizer
     */
    private void onSetupVisualizer() {
        // 实例化可视化观察器，参数为 MediaPlayer 将要播放的音频ID
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        // 设置捕获大小
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1] / 2);
        // 为 Visualizer 设置监听器
        /**
         * 这个监听又四个参数
         * Visualizer.setDataCaptureListener(OnDataCaptureListener listener, int rate, boolean waveform, boolean fft);
         * 		listener，表监听函数，匿名内部类实现该接口，该接口需要实现两个函数
         *      rate， 表示采样的周期，即隔多久采样一次，联系前文就是隔多久采样128个数据
         *      iswave，是波形信号
         *      isfft，是FFT信号，表示是获取波形信号还是频域信号
         *
         */
        mVisualizer.setDataCaptureListener(
                // 数据采集监听
                new Visualizer.OnDataCaptureListener() {
                    /**
                     * 采集快速傅里叶变换有关的数据
                     *
                     * @param visualizer   采集器
                     * @param fft          采集的傅里叶变换数据
                     * @param samplingRate 采样率
                     */
                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                        // 用 FFT 频域傅里叶变换数据更新
                        if (mVoiceCallback != null) {
                            mVoiceCallback.onUpdateData(fft, mMediaPlayer.getCurrentPosition());
                        }
                    }

                    /**
                     * 采集波形数据
                     *
                     * @param visualizer   采集器
                     * @param waveform     采集的波形数据
                     * @param samplingRate 采样率
                     */
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                        // 用 Waveform 波形数据更新
                        if (mVoiceCallback != null) {
                            mVoiceCallback.onUpdateData(waveform, mMediaPlayer.getCurrentPosition());
                        }
                    }
                },
                // 最大采样率
                Visualizer.getMaxCaptureRate() / 2,
                // 是否采集波形数据
                true,
                // 是否采集傅里叶变换数据
                false);
        enableVisualizer();
    }

    /**
     * 获取当前是否播放中
     *
     * @return
     */
    public boolean isPlaying(EMMessage message) {
        if (playStatus == MEDIA_STATUS_PLAYING && currentMsgId.equals(message.getMsgId())) {
            return true;
        }
        return false;
    }

    /**
     * 设置当前播放中的控件
     *
     * @param view 当前
     */
    public void setWaveformView(MLWaveformView view) {
        mWaveformView = view;
    }

    /**
     * 激活 Visualizer 开始采集数据
     */
    public void enableVisualizer() {
        if (mVisualizer != null) {
            // 激活 Visualizer，确保需要采集数据的时候才激活他
            mVisualizer.setEnabled(true);
        }
    }

    /**
     * 停止数据的采集
     */
    public void unableVisualizer() {
        if (mVisualizer != null) {
            // 停止 Visualizer
            mVisualizer.setEnabled(false);
        }
    }

    public void setVoiceCallback(MLVoiceCallback callback) {
        mVoiceCallback = callback;
    }

    /**
     * 语音播放管理类的回调函数，主要是给当前播放的 Item 回调变化数据
     */
    public interface MLVoiceCallback {
        /**
         * 更新播放中采集到的数据
         *
         * @param data     音频数据信息
         * @param position 当前播放进度
         */
        public void onUpdateData(byte[] data, int position);

        /**
         * 停止播放，一般是播放完成
         */
        public void onStop();
    }

}
