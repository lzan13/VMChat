package net.melove.demo.chat.conversation.item;

import android.content.Context;
import android.widget.TextView;


import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.R;

/**
 * Created by lz on 2016/3/20.
 */
public class MLRecallMessageItem extends MLMessageItem {

    // 当前 Item 需要处理的 EMMessage 对象
    private EMMessage mMessage;

    public MLRecallMessageItem(Context context, int viewType) {
        super(context, viewType);

        timeView = (TextView) findViewById(R.id.ml_text_msg_time);
        contentView = (TextView) findViewById(R.id.ml_text_msg_content);
    }

    @Override
    public void onSetupView(EMMessage message) {

    }

    @Override
    protected void onInflateView() {

    }

    public void initView() {


    }
}
