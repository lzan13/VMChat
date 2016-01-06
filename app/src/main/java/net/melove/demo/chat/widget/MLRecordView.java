package net.melove.demo.chat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.melove.demo.chat.util.MLDimen;

/**
 * Class MLRecordView
 * Created by lzan13 on 2015/12/24 16:01.
 * 自定义点击录制语音按钮
 */
public class MLRecordView extends View {

    // 这里定义尺寸类属性的时候，默认值这里的值都是默认为 dp 使用时要转为像素，可以适应屏幕
    // 控件的宽高
    private int width = 360;
    private int height = 56;
    // 录音按钮的半径
    private int radius = 72;

    // 录音按钮的颜色
    private int recordPlateColor = 0x89be2d17;

    /**
     * 构造方法，通过代码引用 view 调用
     *
     * @param context
     */
    public MLRecordView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造方法，通过 xml 布局文件引用 view 的时候调用
     *
     * @param context
     * @param attrs
     */
    public MLRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        width = MLDimen.dip2px(width);
        height = MLDimen.dip2px(height);
        radius = MLDimen.dip2px(radius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawC(canvas);

    }


    /**
     * 这里绘制一个圆，用来表示一个可以按下的按钮
     *
     * @param canvas
     */
    private void drawC(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(recordPlateColor);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
