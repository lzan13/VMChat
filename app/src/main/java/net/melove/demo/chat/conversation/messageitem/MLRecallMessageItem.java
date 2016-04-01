package net.melove.demo.chat.conversation.messageitem;

import android.content.Context;
import android.widget.TextView;


import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.conversation.MLMessageAdapter;

/**
 * Created by lz on 2016/3/20.
 * 撤回类型的消息的 ItemView
 */
public class MLRecallMessageItem extends MLMessageItem {

    public MLRecallMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
        onInflateView();
    }

    /**
     * 实现数据的填充
     *
     * @param message 需要展示的 EMMessage 对象
     */
    @Override
    public void onSetupView(EMMessage message, int position) {
        mMessage = message;
        mPosition = position;
        // 设置消息时间
        mTimeView.setText(MLDate.long2Time(message.getMsgTime()));

        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String messageStr = body.getMessage();
        mContentView.setText(messageStr);
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override
    protected void onInflateView() {
        mInflater.inflate(R.layout.item_msg_sys_recall, this);
        mTimeView = (TextView) findViewById(R.id.ml_text_msg_time);
        mContentView = (TextView) findViewById(R.id.ml_text_msg_content);
    }

    @Override
    protected void onItemLongClick() {

    }
}
