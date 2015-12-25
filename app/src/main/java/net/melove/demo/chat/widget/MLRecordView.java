package net.melove.demo.chat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class MLRecordView
 * Created by lzan13 on 2015/12/24 16:01.
 * 自定义点击录制语音按钮
 */
public class MLRecordView extends View {

    /**
     * 构造方法，通过代码引用 view 调用
     *
     * @param context
     */
    public MLRecordView(Context context) {
        super(context);
    }

    /**
     * 构造方法，通过 xml 布局文件引用 view 的时候调用
     *
     * @param context
     * @param attrs
     */
    public MLRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    

}
