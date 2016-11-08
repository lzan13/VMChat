package net.melove.app.chat.ui.chat.messageitem;

import android.content.Context;
import android.widget.TextView;


import com.hyphenate.chat.EMMessage;

import net.melove.app.chat.R;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.ui.chat.MLMessageAdapter;

/**
 * Created by lz on 2016/3/20.
 * 撤回类型的消息的 ItemView
 */
public class MLRecallMessageItem extends MLMessageItem {



    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context  上下文对象
     * @param adapter  适配器
     * @param viewType item类型
     */
    public MLRecallMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
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
        msgTimeView.setText(MLDateUtil.getRelativeTime(message.getMsgTime()));
        // 设置显示内容
        String messageStr = null;
        if (mMessage.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format(mContext.getString(R.string.ml_hint_msg_recall_by_self));
        }else{
            messageStr = String.format(mContext.getString(R.string.ml_hint_msg_recall_by_user), message.getUserName());
        }
        contentView.setText(messageStr);
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
        bubbleLayout = findViewById(R.id.ml_layout_bubble);
        msgTimeView = (TextView) findViewById(R.id.ml_text_time);
        contentView = (TextView) findViewById(R.id.ml_text_content);
    }
}
