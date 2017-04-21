package com.vmloft.develop.app.chat.chat.messageitem;

import android.content.Context;
import android.widget.TextView;


import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.chat.MessageAdapter;
import com.vmloft.develop.library.tools.utils.VMDateUtil;

/**
 * Created by lz on 2016/3/20.
 * 撤回类型的消息的 ItemView
 */
public class RecallMessageItem extends MessageItem {

    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context  上下文对象
     * @param adapter  适配器
     * @param viewType item类型
     */
    public RecallMessageItem(Context context, MessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
    }

    /**
     * 实现数据的填充
     *
     * @param message 需要展示的 EMMessage 对象
     */
    @Override
    public void onSetupView(EMMessage message) {
        this.message = message;
        // 设置消息时间
        msgTimeView.setText(VMDateUtil.getRelativeTime(message.getMsgTime()));
        // 设置显示内容
        String messageStr = null;
        if (this.message.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format(context.getString(R.string.hint_msg_recall_by_self));
        }else{
            messageStr = String.format(context.getString(R.string.hint_msg_recall_by_user), message.getUserName());
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
        inflater.inflate(R.layout.item_msg_sys_recall, this);
        bubbleLayout = findViewById(R.id.layout_bubble);
        msgTimeView = (TextView) findViewById(R.id.text_time);
        contentView = (TextView) findViewById(R.id.text_content);
    }
}
