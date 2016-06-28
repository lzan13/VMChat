package net.melove.app.easechat.conversation.messageitem;

import android.content.Context;
import android.widget.TextView;


import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.app.easechat.R;
import net.melove.app.easechat.communal.util.MLDate;
import net.melove.app.easechat.conversation.MLMessageAdapter;

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
    public void onSetupView(EMMessage message) {
        mMessage = message;
        // 设置消息时间
        mTimeView.setText(MLDate.long2Time(message.getMsgTime()));
        // 设置显示内容
        String messageStr = null;
        if (mMessage.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format(mContext.getString(R.string.ml_hint_msg_recall_by_self));
        }else{
            messageStr = String.format(mContext.getString(R.string.ml_hint_msg_recall_by_user), message.getUserName());
        }
        mContentView.setText(messageStr);
    }

    @Override
    protected void onItemLongClick() {

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
}
