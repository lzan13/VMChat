package net.melove.app.chat.communal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import net.melove.app.chat.R;
import net.melove.app.chat.communal.util.MLBitmapUtil;
import net.melove.app.chat.communal.util.MLDateUtil;
import net.melove.app.chat.communal.util.MLDimenUtil;

import java.util.LinkedList;

/**
 * Created by lzan13 on 2016/8/18.
 * 自定义录音控件
 */
public class MLRecordView extends View {

    // 上下文对象
    protected Context mContext;
    // 录音控件回调接口
    protected MLRecordCallback mRecordCallback;

    // 波形信息集合
    protected LinkedList<Integer> waveformList;

    // 自定义View画笔
    protected Paint backgroundPaint;
    protected Paint indicatorPaint;
    protected Paint waveformPaint;
    protected Paint textPaint;
    protected Paint touchPaint;

    // 控件边界，就是大小
    protected RectF viewBounds;
    // 控件宽高比
    protected float viewRatio;
    // 控件宽高
    protected float viewWidth;
    protected float viewHeight;

    // 录制开始时间
    protected long startTime = 0L;
    // 录制持续时间
    protected int recordTime = 0;

    // 触摸区域滑动提示图标
    protected String touchIconStr = "iVBORw0KGgoAAAANSUhEUgAAAD8AAAA/CAYAAABXXxDfAAAABHNCSVQICAgIfAhkiAAAA+9JREFU\n" +
            "aIHtmU9oXFUUxr9v3kxfahjJIjFKhZLGtoJSFJl/ySTZCKFRN7rV7eDmEaXRldBXceFkGOIuECrJ\n" +
            "XhFBUMQuIs7LeynGGou0Vp2CgpuG6vzJTEaTd9wkMBnHZLK4b6Lc3/Yy93znnffON/ceQKPRaDQa\n" +
            "jUaj0Wg0/13YbQHN2LYdrlarjxuGUc5ms78CEJXxjk3ymUwm0tfXd1FE5gF84/v+G9Fo9I5t276q\n" +
            "mIaqjY+CZVlmb2/vcyJyleSgiJwmearRaHw3OTl5f3l5WckbEFKx6VHIZDIP9PT0vELyPZL9AEDS\n" +
            "BPACySulUumMqthhVRt3gm3bPbVa7WUReRPAo81rJE0ReSgSiURUxe9a5S3LMqvV6ou7iQ+1ahGR\n" +
            "dRGZrdfrRVUaulL5TCYTMU1ziuQ7AE7jn0X4AcDlSqVybWFh4S9VOgLv9rZth2u12pTv++/vfeN7\n" +
            "iIgAKIqIlc/nP1OtJdBun8lkIoZhTO129YHWdZJFANP5fP7TIPQEVnnLskzTNKcALLRWHABE5A7J\n" +
            "13O5XCCJAwE1vHZ21sLXQScOBNDwDrKzXa6LyNvlcvkL1VpaUfrNW5Zl+r7/EoC30MbOANwEcLnR\n" +
            "aFybn5//U6WWdiirfAd29iOAK6VS6XOVdnYQShreYXZG8m4oFHotm81+oiJ+pyip/Obm5rMArrZr\n" +
            "biTv+b5/KZfLdTVxQF23fx5AtN2CiPwE4IaiuEdCScOLxWJrhmFEATwB4ETzGsmHATySTqdvOY6z\n" +
            "oSJ+pyhJfnV1dTOZTN4keZLkk9j/AAySwwAGUqnUbdd176nQ0AnKrM513crY2Nj3InISwAUAzUfT\n" +
            "CIBhkgOJROJbz/N+V6XjIJT6vOM45XQ6vQ7glIicJ9ncYE8AOGcYxoOjo6NrjuNUVGpph/KDjeM4\n" +
            "lUQi8aVhGEMAzmK/w4QBPCMi/SMjIysrKyubqvU0E8ipzvO8+sTExLKInAFwrjUuyacA9KdSKcd1\n" +
            "3VoQmtAqQiWFQqE+Pj6+vLOzM7zb8Pb9xyD5NMm+ZDK57rpuOQhNgZ7nC4VCPRaLffVvNgjgAsnB\n" +
            "VCp1y3Vd5TYY+NV1BzZ4FkB/PB6/7Xme0gfQlXv7w2yQ5GOhUKg/Ho/f8DzvD1U6uja0OMwGSZ4P\n" +
            "hULReDy+5nleVYWGbg4tZHZ29rft7e1pkh8B2Nq3KGKSvGia5jAUnT67PrGZm5u7Hw6HLZIfA9i7\n" +
            "0PBJ3iV5qVgsXoeigeWxmNW12OCQiPxC8t2tra0PFhcXld3wHJspLQBMT08PRiKRVwH8vLGx8eHS\n" +
            "0tLWoT/6PzEzM9NrWZbZbR0ajUaj0Wg0Go1Go9Fojid/A5Z7fiLwAWSVAAAAAElFTkSuQmCC\n";

    // 触摸区域颜色
    protected int touchColor;
    // 触摸区域图标资源
    protected int touchIcon;
    // 触摸区域大小
    protected int touchSize;
    // 触摸提示文字
    protected String touchText = "";
    // 触摸区域中心点位置
    protected float centerX;
    protected float centerY;

    // 波形刻度颜色
    protected int waveformColor;
    // 波形刻度间隔
    protected int waveformInterval;
    // 波形刻度宽度
    protected int waveformWidth;

    // 指示器颜色
    protected int indicatorColor;
    // 指示器大小
    protected int indicatorSize;

    // 文字颜色
    protected int textColor;
    // 文字大小
    protected int textSize;


    public MLRecordView(Context context) {
        this(context, null);
    }

    public MLRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MLRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MLRecordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(context, attrs);
    }

    /**
     * 控件初始化方法
     */
    protected void init(Context context, AttributeSet attrs) {
        //  关闭控件级别的硬件加速
        //        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 波形数据集合
        waveformList = new LinkedList<Integer>();

        // 默认高度 64dp
        viewHeight = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_56);

        // 初始化自定义控件的宽高比
        viewRatio = 360.0f / 72.0f;

        // 触摸区域相关参数
        touchColor = 0xdd2384fe;
        touchIcon = R.mipmap.ic_call_white_24dp;
        touchSize = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_48);

        // 波形刻度相关参数
        waveformColor = 0xddff5722;
        waveformInterval = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_1);
        waveformWidth = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_2);

        // 默认指示器相关参数
        indicatorColor = 0xddd22a14;
        indicatorSize = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_16);

        // 文字相关参数
        textColor = 0x89212121;
        textSize = MLDimenUtil.getDimenPixel(R.dimen.ml_size_14);

        // 获取控件的属性值
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MLRecordView);
            // 获取自定义属性值，如果没有设置就是默认值
            touchColor = array.getColor(R.styleable.MLRecordView_ml_touch_color, touchColor);
            touchIcon = array.getResourceId(R.styleable.MLRecordView_ml_touch_icon, touchIcon);
            touchSize = array.getDimensionPixelOffset(R.styleable.MLRecordView_ml_touch_size, touchSize);
            touchText = array.getString(R.styleable.MLRecordView_ml_touch_text);

            waveformColor = array.getColor(R.styleable.MLRecordView_ml_waveform_color, waveformColor);
            waveformInterval = array.getDimensionPixelOffset(R.styleable.MLRecordView_ml_waveform_interval, waveformInterval);
            waveformWidth = array.getDimensionPixelOffset(R.styleable.MLRecordView_ml_waveform_width, waveformWidth);

            indicatorColor = array.getColor(R.styleable.MLRecordView_ml_indicator_color, indicatorColor);
            indicatorSize = array.getDimensionPixelOffset(R.styleable.MLRecordView_ml_indicator_size, indicatorSize);

            textColor = array.getColor(R.styleable.MLRecordView_ml_text_color, textColor);
            textSize = array.getDimensionPixelOffset(R.styleable.MLRecordView_ml_text_size, textSize);
            // 回收资源
            array.recycle();
        }
        if (touchText == null) {
            touchText = "Slide to cancel";
        }

        // 实例化画笔
        backgroundPaint = new Paint();
        // 设置抗锯齿
        backgroundPaint.setAntiAlias(true);
        // 效果同上
        backgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // 设置画笔模式
        backgroundPaint.setStyle(Paint.Style.FILL);
        // 其他画笔都通过第一个画笔创建
        indicatorPaint = new Paint(backgroundPaint);
        waveformPaint = new Paint(backgroundPaint);
        textPaint = new Paint(backgroundPaint);

        touchPaint = new Paint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        drawBackground(canvas);
        // 绘制指示器
        drawIndicator(canvas);
        // 绘制波形刻度部分
        drawWaveform(canvas);
        // 绘制文字
        drawTimeText(canvas);
        // 绘制触摸区域
        drawCircle(canvas);
    }

    /**
     * 回执控件背景，这里要注意不能绘制全部，只能回执有效区域
     */
    protected void drawBackground(Canvas canvas) {
        if (MLRecorder.getInstance().isRecording()) {
            backgroundPaint.setColor(0xddffffff);
            Rect rect = new Rect(0, 0, (int) viewWidth, (int) viewHeight);
            canvas.drawRect(rect, backgroundPaint);
        }
    }

    /**
     * 绘制指示器
     *
     * @param canvas
     */
    protected void drawIndicator(Canvas canvas) {
        if (MLRecorder.getInstance().isRecording()) {
            // 设置画笔颜色
            indicatorPaint.setColor(indicatorColor);
            // 绘制指示器的圆
            canvas.drawCircle(viewHeight / 2, viewHeight / 2, indicatorSize / 2, indicatorPaint);
        }
    }

    /**
     * 绘制波形
     */
    protected void drawWaveform(Canvas canvas) {
        if (MLRecorder.getInstance().isRecording()) {
            // 设置画笔模式
            waveformPaint.setStyle(Paint.Style.STROKE);
            // 设置画笔宽度
            waveformPaint.setStrokeWidth(waveformWidth);
            // 设置画笔末尾样式
            waveformPaint.setStrokeCap(Paint.Cap.ROUND);
            // 设置画笔颜色
            waveformPaint.setColor(waveformColor);
            int count = (int) (viewWidth / (waveformWidth + waveformInterval));
            for (int i = 0; i < count; i++) {
                float startX = i * (waveformInterval + waveformWidth);
                float waveformHeight = 2;
                if (i < waveformList.size()) {
                    waveformHeight = waveformList.get(i) * viewHeight / 12;
                }
                if (waveformHeight == 0) {
                    // 防止计算得到的波形高度为 0 导致显示空白
                    waveformHeight = 2;
                }
                // 绘制波形线
                canvas.drawLine(startX, viewHeight - waveformHeight, startX, viewHeight, waveformPaint);
            }
        }
    }

    /**
     * 画时间文字
     *
     * @param canvas
     */
    protected void drawTimeText(Canvas canvas) {
        if (MLRecorder.getInstance().isRecording()) {
            textPaint.setColor(textColor);
            textPaint.setStrokeWidth(1);
            textPaint.setTextSize(textSize);
            String timeText = "";
            int minute = recordTime / 1000 / 60;
            if (minute < 10) {
                timeText = "0" + minute;
            } else {
                timeText = "" + minute;
            }
            int seconds = recordTime / 1000 % 60;
            if (seconds < 10) {
                timeText = timeText + ":0" + seconds;
            } else {
                timeText = timeText + ":" + seconds;
            }
            int millisecond = recordTime % 1000 / 100;
            timeText = timeText + "." + millisecond;
            float textWidth = textPaint.measureText(timeText);
            canvas.drawText(timeText, viewHeight / 2 + textWidth / 2, viewHeight / 2 + textSize / 3, textPaint);
        }
    }

    /**
     * 绘制触摸时圆形区域
     */
    protected void drawCircle(Canvas canvas) {
        if (MLRecorder.getInstance().isRecording()) {
            // 绘制触摸提示文字
            textPaint.setTextSize(textSize);
            float textWidth = MLDimenUtil.getTextWidth(textPaint, touchText);
            float textHeight = MLDimenUtil.getTextHeight(textPaint);
            canvas.drawText(touchText, centerX - textWidth - touchSize / 3 * 2, centerY + textHeight / 3, textPaint);

            Bitmap touchSlideIcon = MLBitmapUtil.string2Bitmap(touchIconStr);
            // 绘制滑动取消箭头
            canvas.drawBitmap(touchSlideIcon, centerX - textWidth - touchSize, centerY - touchSlideIcon.getHeight() / 2, textPaint);
        }
        // 设置触摸区域画笔颜色
        touchPaint.setColor(touchColor);
        touchPaint.setAntiAlias(true);

        canvas.drawCircle(centerX, centerY, touchSize / 2, touchPaint);

        // 绘制触摸区域的图标
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), touchIcon);
        canvas.drawBitmap(bitmap, centerX - bitmap.getWidth() / 2, centerY - bitmap.getHeight() / 2, touchPaint);

        //        Bitmap tempBitmap = Bitmap.createBitmap((int) viewHeight * 2, (int) viewHeight * 2, Bitmap.Config.ARGB_8888);
        //        Canvas tempCanvas = new Canvas(tempBitmap);
        //        Paint tempPaint = new Paint();
        //        tempPaint.setAntiAlias(true);
        //        tempPaint.setStyle(Paint.Style.FILL);
        //        tempPaint.setColor(touchColor);
        //        PorterDuffXfermode xfermode = null;
        //        if (MLRecorder.getInstance().isRecording()) {
        //            /**
        //             * 录制进行中，画笔模式设置为全部显示，PorterDuff.Mode.DARKEN
        //             */
        //            xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        //        } else {
        //            /**
        //             * 录制空闲状态，画笔模式设置为只显示中间图标部分，PorterDuff.Mode.SRC_IN
        //             */
        //            xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        //        }
        //        tempPaint.setXfermode(xfermode);
        //        tempCanvas.drawCircle(tempBitmap.getWidth() / 2, tempBitmap.getHeight() / 2, touchSize / 2, tempPaint);
        //        // 绘制触摸区域的图标
        //        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), touchIcon);
        //        tempCanvas.drawBitmap(bitmap, tempBitmap.getWidth() / 2 - bitmap.getWidth() / 2, tempBitmap.getHeight() / 2 - bitmap.getHeight() / 2, tempPaint);
        //        // 将新建的 Canvas 传递给 View
        //        canvas.drawBitmap(tempBitmap, centerX - tempBitmap.getWidth() / 2, centerY - tempBitmap.getHeight() / 2, touchPaint);
    }

    /**
     * 开始录制
     */
    public void startRecord(String path) {
        // 调用录音机开始录制音频
        int recordError = MLRecorder.getInstance().startRecordVoice(path);
        if (recordError == MLRecorder.ERROR_NONE) {
            // 开始录音
            // 初始化开始录制时间
            startTime = MLDateUtil.getCurrentMillisecond();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (MLRecorder.getInstance().isRecording()) {
                        // 睡眠 100 毫秒，
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        recordTime = getRecordTime();
                        int decibel = MLRecorder.getInstance().getVoiceWaveform();
                        waveformList.addFirst(decibel);
                        // MLLog.i("麦克风监听声音分贝：%d", decibel);
                        postInvalidate();
                    }
                }
            }).start();
        } else if (recordError == MLRecorder.ERROR_RECORDING) {
            // 录音进行中
        } else if (recordError == MLRecorder.ERROR_SYSTEM) {
            if (mRecordCallback != null) {
                // 媒体录音器准备失败，调用取消
                MLRecorder.getInstance().cancelRecordVoice();
                mRecordCallback.onFailed(recordError);
            }
        } else {
            if (mRecordCallback != null) {
                // 录音开始
                mRecordCallback.onStart();
            }
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        // 调用录音机停止录制
        int recordError = MLRecorder.getInstance().stopRecordVoice();
        // 停止录制，清除集合
        waveformList.clear();
        // 计算录制时间
        recordTime = getRecordTime();
        if (mRecordCallback != null) {
            if (recordTime < 1000) {
                // 录制时间太短
                mRecordCallback.onFailed(MLRecorder.ERROR_SHORT);
            } else if (recordError == MLRecorder.ERROR_NONE) {
                // 录音成功
                mRecordCallback.onSuccess(MLRecorder.getInstance().getRecordFilePath(), recordTime);
            } else if (recordError == MLRecorder.ERROR_FAILED) {
                // 录音失败
                mRecordCallback.onFailed(recordError);
            } else if (recordError == MLRecorder.ERROR_SYSTEM) {
                // 录音失败，系统错误
                mRecordCallback.onFailed(recordError);
            }
        }
        recordTime = 0;
        // 刷新UI
        postInvalidate();
    }

    /**
     * 取消录制
     */
    public void cancelRecord() {
        // 调用录音机停止录制
        int recordError = MLRecorder.getInstance().cancelRecordVoice();
        // 停止录制，清除集合
        waveformList.clear();
        if (mRecordCallback != null) {
            if (recordError == MLRecorder.ERROR_CANCEL) {
                // 取消录音
                mRecordCallback.onCancel();
            } else if (recordError == MLRecorder.ERROR_SYSTEM) {

            }
        }
        recordTime = 0;
        postInvalidate();
    }

    /**
     * 获取录音持续时间
     *
     * @return
     */
    private int getRecordTime() {return (int) (MLDateUtil.getCurrentMillisecond() - startTime);}

    /**
     * 重写 onTouchEvent 监听方法，用来响应控件触摸
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isShown()) {
            return false;
        }
        // 触摸点横坐标
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // 判断按下的位置是不是在触摸区域
            if (x < viewWidth - viewHeight) {
                return false;
            }
            // 按下后更改触摸区域半径
            touchSize = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_72);
            // 触摸开始录音
            startRecord(null);
            postInvalidate();
            break;
        case MotionEvent.ACTION_UP:
            centerX = viewWidth - viewHeight / 2;
            // 抬起后更改触摸区域半径
            touchSize = MLDimenUtil.getDimenPixel(R.dimen.ml_dimen_48);
            // 根据向左滑动的距离判断是正常停止录制，还是取消录制
            if (x > viewWidth / 2) {
                // 抬起停止录制
                stopRecord();
            } else {
                // 取消录制
                cancelRecord();
            }
            postInvalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            if (x < viewWidth - viewHeight / 2) {
                centerX = x;
            } else {
                centerX = viewWidth - viewHeight / 2;
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewBounds = new RectF(getLeft(), getTop(), getRight(), getBottom());

        viewWidth = viewBounds.right - viewBounds.left;
        viewHeight = viewBounds.bottom - viewBounds.top;
        // 触摸区域中心位置
        centerX = viewWidth - viewHeight / 2;
        centerY = viewHeight / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = Integer.MAX_VALUE;
        /**
         * 测量模式
         * 父布局希望子布局的大小,如果布局里面设置的是固定值,这里取布局里面的固定值和父布局大小值中的最小值.
         * 如果设置的是match_parent,则取父布局的大小
         */
        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 父控件提供的空间大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // 父控件提供的空间大小
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 去除 Padding 后的父容器传过来的宽和高，就是父控件允许子空间施展的空间大小
        int spaceWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int spaceHeight = heightSize - getPaddingTop() - getPaddingBottom();

        int width = 0;
        int height = 0;

        // 根据控件模式计算宽度
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            width = spaceWidth;
        } else {
            // 自己计算模式，需要手动根据控件内容计算出来
            width = defaultWidth;
        }

        // 根据控件模式计算高度
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
            height = spaceHeight;
        } else {
            // 如果高度不能确定，就根据宽度的大小按比例计算出来
            height = (int) (width * 1.0f / viewRatio);
        }
        // 最后调用父类方法,把View的大小告诉父布局。
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 设置录音回调
     *
     * @param callback
     */
    public void setRecordCallback(MLRecordCallback callback) {
        mRecordCallback = callback;
    }

    /**
     * 录音控件的回调接口，用于回调给调用者录音结果
     */
    public interface MLRecordCallback {

        /**
         * 录音取消
         */
        public void onCancel();

        /**
         * 录音失败
         *
         * @param error 失败的错误信息
         */
        public void onFailed(int error);

        /**
         * 录音开始
         */
        public void onStart();

        /**
         * 录音成功
         *
         * @param path 录音文件的路径
         * @param time 录音时长
         */
        public void onSuccess(String path, int time);

    }

}
