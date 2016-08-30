package net.melove.app.chat.communal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.melove.app.chat.R;
import net.melove.app.chat.communal.util.MLDimenUtil;


/**
 * Created by lzan13 on 2016/8/18.
 * 波形测试绘制类
 */
public class MLWaveformView extends View {

    // 上下文对象
    protected Context mContext;

    protected MLWaveformCallback mWaveformCallback;

    // 是否在触摸区域
    protected boolean isTouch = false;
    // 是否正在播放
    protected boolean isPlay = false;

    // 点坐标集合，四个一组
    protected float[] waveformPoints;
    // 采集到的声音信息
    protected byte[] waveformBytes;
    // 当前进度位置
    protected int position;

    // 控件布局边界
    protected RectF viewBounds;
    // 控件宽高
    protected float viewWidth;
    protected float viewHeight;

    // 波形部分画笔
    protected Paint waveformPaint;
    // 触摸部分画笔
    protected Paint touchPaint;
    // 文字画笔
    protected Paint textPaint;

    // 触摸部分颜色
    protected int touchColor;
    // 触摸部分图标
    protected int touchIconActive;
    protected int touchIconNormal;
    // 触摸部分大小
    protected int touchSize;
    // 触摸区域中心坐标
    protected float touchCenterX;
    protected float touchCenterY;

    // 时间字符内容
    protected String textTime;

    // 波形刻度颜色
    protected int waveformColor;
    // 波形间隔
    protected int waveformInterval;
    // 波形宽度
    protected int waveformWidth;


    /**
     * 构造方法
     *
     * @param context
     */
    public MLWaveformView(Context context) {
        this(context, null);
    }

    public MLWaveformView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MLWaveformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MLWaveformView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs);
    }

    /**
     * 初始化操作
     */
    private void init(AttributeSet attrs) {

        waveformBytes = null;

        // 波形部分默认参数
        waveformColor = 0xddff5722;
        waveformInterval = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_1);
        waveformWidth = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_2);

        // 触摸部分默认参数
        touchColor = 0xddff5722;
        touchIconActive = R.mipmap.ic_pause_white_24dp;
        touchIconNormal = R.mipmap.ic_play_arrow_white_24dp;
        touchSize = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_36);

        textTime = "Time";

        // 获取控件的属性值
        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.MLWaveformView);
            // 获取自定义属性值，如果没有设置就是默认值
            touchColor = array.getColor(R.styleable.MLWaveformView_ml_waveform_touch_color, touchColor);
            touchIconActive = array.getResourceId(R.styleable.MLWaveformView_ml_waveform_touch_icon_active, touchIconActive);
            touchIconNormal = array.getResourceId(R.styleable.MLWaveformView_ml_waveform_touch_icon_normal, touchIconNormal);
            touchSize = array.getDimensionPixelOffset(R.styleable.MLWaveformView_ml_waveform_touch_size, touchSize);

            waveformColor = array.getColor(R.styleable.MLWaveformView_ml_waveform_waveform_color, waveformColor);
            waveformInterval = array.getDimensionPixelOffset(R.styleable.MLWaveformView_ml_waveform_waveform_interval, waveformInterval);
            waveformWidth = array.getDimensionPixelOffset(R.styleable.MLWaveformView_ml_waveform_waveform_width, waveformWidth);

            // 回收资源
            array.recycle();
        }

        // 初始化画笔
        waveformPaint = new Paint();
        // 设置画笔抗锯齿
        waveformPaint.setAntiAlias(true);

        // 从前边的画笔创建新的画笔
        touchPaint = new Paint(waveformPaint);
        textPaint = new Paint(waveformPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制波形
        drawFFT(canvas);

        // 绘制触摸区域部分
        drawTouchIcon(canvas);

        // 绘制文字
        drawText(canvas);
    }

    /**
     * 绘制文字
     *
     * @param canvas 当前控件画布
     */
    protected void drawText(Canvas canvas) {
        textPaint.setTextSize(24);
        textPaint.setColor(0xddffffff);
        textPaint.setStrokeWidth(4);
        float textWidth = MLDimenUtil.getTextWidth(textPaint, textTime);
        float textHeight = MLDimenUtil.getTextHeight(textPaint);
        canvas.drawText(textTime, viewWidth - textWidth, textHeight, textPaint);

    }


    /**
     * 回执触摸图标
     *
     * @param canvas 当前控件的画布
     */
    protected void drawTouchIcon(Canvas canvas) {
        touchPaint.setColor(touchColor);
        // 绘制触摸区域圆形背景
        canvas.drawCircle(touchCenterX, touchCenterY, touchSize / 2, touchPaint);

        Bitmap bitmap = null;
        if (isPlay) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), touchIconActive);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), touchIconNormal);
        }
        // 绘制触摸图标
        canvas.drawBitmap(bitmap, touchCenterX - bitmap.getWidth() / 2, touchCenterY - bitmap.getHeight() / 2, touchPaint);
    }

    /**
     * 绘制频谱波形图
     *
     * @param canvas
     */
    private void drawFFT(Canvas canvas) {
        // 设置画笔颜色
        waveformPaint.setColor(waveformColor);
        // 设置画笔末尾样式
        waveformPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置画笔宽度
        waveformPaint.setStrokeWidth(waveformWidth / 2);

        // 波形数据如果为 null 直接 return
        if (waveformBytes == null) {
            return;
        }

        if (waveformPoints == null || waveformPoints.length < waveformBytes.length * 4) {
            waveformPoints = new float[waveformBytes.length * 4];
        }
        float baseX = touchSize;
        float baseY = viewHeight / 2;
        // 绘制时域型波形图
        float interval = (viewWidth - touchSize) / (waveformBytes.length - 1);
        for (int i = 0; i < waveformBytes.length - 1; i++) {
            // 计算第i个点的x坐标
            waveformPoints[i * 4] = baseX + i * interval;
            // 根据bytes[i]的值（波形点的值）计算第i个点的y坐标
            waveformPoints[i * 4 + 1] = baseY + ((byte) (waveformBytes[i] + 128)) * (viewHeight / 2) / 128;
            // 计算第i+1个点的x坐标
            waveformPoints[i * 4 + 2] = baseX + interval * (i + 1);
            // 根据bytes[i+1]的值（波形点的值）计算第i+1个点的y坐标
            waveformPoints[i * 4 + 3] = baseY + ((byte) (waveformBytes[i + 1] + 128)) * (viewHeight / 2) / 128;
        }
        canvas.drawLines(waveformPoints, waveformPaint);
    }

    /**
     * 绘制树状波形
     * TODO 由于采集数据频率以及数据大小，导致展示树状波形不是很理想，暂时不用
     *
     * @param canvas 当前控件的画布
     */
    private void drawWaveform(Canvas canvas) {
        // 设置画笔颜色
        waveformPaint.setColor(waveformColor);
        // 设置画笔宽度
        waveformPaint.setStrokeWidth(waveformWidth);
        // 设置画笔末尾样式
        waveformPaint.setStrokeCap(Paint.Cap.ROUND);

        int count = (int) ((viewWidth - touchSize) / (waveformInterval + waveformWidth));
        canvas.drawLine(touchSize, viewHeight, viewWidth, viewHeight, waveformPaint);

        // 波形数据如果为 null 直接 return
        if (waveformBytes == null) {
            return;
        }

        if (waveformPoints == null || waveformPoints.length < waveformBytes.length * 4) {
            waveformPoints = new float[waveformBytes.length * 4];
        }

        float baseX = touchSize;
        float baseY = viewHeight;

        // 回执频域型波形图
        for (int i = 0; i < count; i++) {
            float waveform = ((byte) (waveformBytes[i] + 128)) * (viewHeight / 2) / 128;
            waveformPoints[4 * i] = baseX + i * (waveformWidth + waveformInterval);
            waveformPoints[4 * i + 1] = baseY;
            waveformPoints[4 * i + 2] = baseX + i * (waveformWidth + waveformInterval);
            if (waveform < 0) {
                waveformPoints[4 * i + 3] = baseY + waveform;
            } else {
                waveformPoints[4 * i + 3] = baseY - waveform;
            }
        }
        canvas.drawLines(waveformPoints, waveformPaint);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewBounds = new RectF(getLeft(), getTop(), getRight(), getBottom());

        viewWidth = viewBounds.right - viewBounds.left;
        viewHeight = viewBounds.bottom - viewBounds.top;

        touchCenterX = 0 + touchSize / 2;
        touchCenterY = viewHeight / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 触摸点横坐标
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // 判断按下的位置是不是在触摸区域
            if (x < touchCenterX - touchSize / 2 || x > touchCenterX + touchSize / 2
                    || y < touchCenterY - touchSize / 2 || y > touchCenterY + touchSize / 2) {
                return false;
            }
            isTouch = true;
            // 设置画笔透明度
            touchPaint.setAlpha(128);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            if (isTouch) {
                if (isPlay) {
                    stopPlay();
                } else {
                    startPlay();
                }
            }
            // 设置画笔透明度
            touchPaint.setAlpha(255);
            postInvalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            if (isTouch) {
                mWaveformCallback.onDrag((int) x);
            }
            postInvalidate();
            break;
        default:
            break;
        }
        // 这里不调用系统的onTouchEvent方法，防止抬起时画面无法重绘
        // return super.onTouchEvent(event);
        return true;
    }

    private void startPlay() {
        isPlay = true;
        mWaveformCallback.onStart();
    }

    private void stopPlay() {
        isPlay = false;
        mWaveformCallback.onStop();
    }

    /**
     * 更新 FFT 频域数据
     *
     * @param bytes    采集到的数据信息
     * @param position 音源当前播放位置
     */
    public void updateFFTData(byte[] bytes, int position) {
        waveformBytes = bytes;
        invalidate();
    }

    /**
     * 更新波形数据
     *
     * @param bytes    采集到的数据信息
     * @param position 音源当前播放位置
     */
    public void updateWaveformData(byte[] bytes, int position) {
        waveformBytes = bytes;
        invalidate();
    }

    /**
     * 设置回调接口
     *
     * @param callback
     */
    public void setWaveformCallback(MLWaveformCallback callback) {
        mWaveformCallback = callback;
    }

    /**
     * 控件回调接口
     */
    public interface MLWaveformCallback {
        /**
         * 开始
         */
        public void onStart();

        /**
         * 停止
         */
        public void onStop();

        /**
         * 拖动
         *
         * @param position
         */
        public void onDrag(int position);

        /**
         * 错误
         *
         * @param error
         */
        public void onError(int error);
    }
}
