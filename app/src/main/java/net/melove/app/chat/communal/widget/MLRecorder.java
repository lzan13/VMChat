package net.melove.app.chat.communal.widget;

import android.media.MediaRecorder;


import net.melove.app.chat.communal.util.MLDateUtil;
import net.melove.app.chat.communal.util.MLFileUtil;

import java.io.IOException;

/**
 * Created by lz on 2016/8/20.
 * 定义的录音功能单例类，主要处理录音的相关操作
 */
public class MLRecorder {
    public static final int ERROR_NONE = 0;     // 没有错误
    public static final int ERROR_SYSTEM = 1;   // 系统错误
    public static final int ERROR_FAILED = 2;   // 录制失败
    public static final int ERROR_RECORDING = 3;// 正在录制
    public static final int ERROR_CANCEL = 4;   // 录音取消
    public static final int ERROR_SHORT = 5;    // 录音时间过短

    // 单例类的实例
    private static MLRecorder instance;

    // 媒体录影机，可以录制音频和视频
    private MediaRecorder mMediaRecorder;
    // 计算分贝基准值
    protected int decibelBase = 200;

    // 录制文件保存路径
    protected String recordFilePath;

    // 录音最大持续时间 10 分钟
    protected int maxDuration = 10 * 60 * 1000;
    // 音频采样率 单位 Hz
    protected int samplingRate = 8000;
    // 音频编码比特率
    protected int encodingBitRate = 64;

    // 录音机是否工作中
    protected boolean isRecording = false;

    /**
     * 单例类的私有构造方法
     */
    private MLRecorder() {
    }

    /**
     * 获取单例类的实例方法
     *
     * @return
     */
    public static MLRecorder getInstance() {
        if (instance == null) {
            instance = new MLRecorder();
        }
        return instance;
    }

    /**
     * 返回录制的语音文件路径
     *
     * @return
     */
    public String getRecordFilePath() {
        return recordFilePath;
    }

    /**
     * 初始化录制音频
     */
    public void initVoiceRecorder() {
        // 实例化媒体录影机
        mMediaRecorder = new MediaRecorder();
        // 设置音频源为麦克风
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        /**
         * 设置音频文件编码格式，这里设置默认
         * https://developer.android.com/reference/android/media/MediaRecorder.AudioEncoder.html
         */
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        /**
         * 设置音频文件输出格式
         * https://developer.android.com/reference/android/media/MediaRecorder.OutputFormat.html
         */
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置录音最大持续时间
        mMediaRecorder.setMaxDuration(maxDuration);
        // 设置音频采样率
        mMediaRecorder.setAudioSamplingRate(samplingRate);
        // 设置音频编码比特率
        mMediaRecorder.setAudioEncodingBitRate(encodingBitRate);
    }

    /**
     * 开始录制声音文件
     */
    public int startRecordVoice(String path) {
        // 判断录制系统是否空闲
        if (isRecording) {
            return ERROR_RECORDING;
        }
        if (path == null || path.equals("")) {
            // 这里默认保存在 /sdcard/android/data/packagename/files/下
            recordFilePath = MLFileUtil.getFilesFromSDCard() + MLDateUtil.getCurrentMillisecond() + ".amr";
        } else {
            recordFilePath = path;
        }

        // 设置为录制音频中
        isRecording = true;
        // 判断媒体录影机是否释放，没有则释放
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        // 释放之后重新初始化
        initVoiceRecorder();

        // 设置输出文件路径
        mMediaRecorder.setOutputFile(recordFilePath);
        try {
            // 准备录制
            mMediaRecorder.prepare();
            // 开始录制
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_SYSTEM;
        }
        return ERROR_NONE;
    }

    /**
     * 停止录音
     */
    public int stopRecordVoice() {
        // 停止录音，将录音状态设置为false
        isRecording = false;
        // 释放媒体录影机
        if (mMediaRecorder != null) {
            // 防止录音机 start 后马上调用 stop 出现异常
            //            mMediaRecorder.setOnErrorListener(null);
            try {
                // 停止录制
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
                return ERROR_SYSTEM;
            }
            // 重置媒体录影机
            mMediaRecorder.reset();
            // 释放媒体录影机
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        // 根据录制结果判断录音是否成功
        if (!MLFileUtil.isFileExists(recordFilePath)) {
            return ERROR_FAILED;
        }
        return ERROR_NONE;
    }

    /**
     * 取消录音
     *
     * @return
     */
    public int cancelRecordVoice() {
        // 停止录音，将录音状态设置为false
        isRecording = false;
        // 释放媒体录影机
        if (mMediaRecorder != null) {
            try {
                // 停止录制
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
                return ERROR_SYSTEM;
            }
            // 重置媒体录影机
            mMediaRecorder.reset();
            // 释放媒体录影机
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        // 根据录制结果判断录音是否成功
        if (MLFileUtil.isFileExists(recordFilePath)) {
            MLFileUtil.deleteFile(recordFilePath);
        }
        return ERROR_CANCEL;
    }

    /**
     * 获取声音分贝信息
     *
     * @return
     */
    public int getVoiceWaveform() {
        int waveform = 1;
        if (mMediaRecorder != null) {
            int ratio = 0;
            try {
                ratio = mMediaRecorder.getMaxAmplitude() / decibelBase;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            if (ratio > 0) {
                // 根据麦克风采集到的声音振幅计算声音分贝大小
                waveform = (int) (20 * Math.log10(ratio)) / 10;
            }
        }
        return waveform;
    }

    /**
     * 录音机是否正在录制中
     *
     * @return
     */
    public boolean isRecording() {
        return isRecording;
    }

}
