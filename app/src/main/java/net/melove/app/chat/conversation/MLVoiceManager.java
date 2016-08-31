package net.melove.app.chat.conversation;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

import com.hyphenate.chat.EMMessage;

import net.melove.app.chat.application.MLApplication;

/**
 * Created by lzan13 on 2016/8/31.
 */
public class MLVoiceManager {


    // 单例类的实例
    private static MLVoiceManager instance;

    // 媒体播放器
    private MediaPlayer mMediaPlayer;

    // 音频可视化工具，主要是为了获取音频波形信息
    private Visualizer mVisualizer;

    // 是否有音频正在播放中
    private boolean isPlay = false;
    // 当前正在播放的消息ID
    private String currentMsgId;

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
        if (isPlay) {
            onStop(message);
            if (currentMsgId != null && currentMsgId.equals(message.getMsgId())) {
                return;
            }
            // Create the MediaPlayer
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(message.get);
            // 设置是否循环播放 默认为 false
            mMediaPlayer.setLooping(true);

            // 媒体播放结束监听
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // 取消激活 Visualizer
                    mVisualizer.setEnabled(false);
                }
            });
        } else {

        }
    }

    /**
     * 停止
     */
    public void onStop(EMMessage message) {

    }

    /**
     * 暂停
     */
    public void onPause(EMMessage message) {

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
                        // 用 FFT 频域傅里叶变换数据更新 mVisualizerView 组件
                        mWaveformView.updateFFTData(fft, mMediaPlayer.getCurrentPosition());
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
                        // 用 Waveform 波形数据更新 mVisualizerView 组件
                        mWaveformView.updateWaveformData(waveform, mMediaPlayer.getCurrentPosition());
                    }
                },
                // 最大采样率
                Visualizer.getMaxCaptureRate() / 2,
                // 是否采集波形数据
                true,
                // 是否采集傅里叶变换数据
                false);
    }


    /**
     * 激活 Visualizer 开始采集数据
     */
    public void startVisualizer() {
        // 激活 Visualizer，确保需要采集数据的时候才激活他
        mVisualizer.setEnabled(true);
    }

    /**
     * 停止数据的采集
     */
    public void stopVisualizer() {
        // 停止 Visualizer
        mVisualizer.setEnabled(false);
    }

}
