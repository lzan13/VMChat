package com.vmloft.develop.app.chat.chat.messageitem;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.chat.MessageAdapter;
import com.vmloft.develop.app.chat.chat.ChatActivity;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.widget.VMImageView;

/**
 * Created by lz on 2016/3/20.
 * 文字消息处理类
 */
public class TextMessageItem extends MessageItem {

    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context 上下文对象
     * @param adapter 适配器
     * @param viewType item类型
     */
    public TextMessageItem(Context context, MessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
    }

    /**
     * 实现数据的填充
     *
     * @param message 需要展示的 EMMessage 对象
     */
    @Override public void onSetupView(EMMessage message) {
        this.message = message;

        // 判断如果是单聊或者消息是发送方，不显示username
        if (this.message.getChatType() == EMMessage.ChatType.Chat
                || this.message.direct() == EMMessage.Direct.SEND) {
            usernameView.setVisibility(View.GONE);
        } else {
            // 设置消息消息发送者的名称
            usernameView.setText(message.getFrom());
            usernameView.setVisibility(View.VISIBLE);
        }

        // 设置消息时间
        msgTimeView.setText(VMDate.getRelativeTime(message.getMsgTime()));

        EMTextMessageBody body = (EMTextMessageBody) this.message.getBody();
        Spannable spannable = new SpannableString(body.getMessage().toString());
        contentView.setText(spannable, TextView.BufferType.SPANNABLE);

        // 刷新界面显示
        refreshView();
    }

    /**
     * 实现当前Item 的长按操作，因为各个Item类型不同，需要的实现操作不同，所以长按菜单的弹出在Item中实现，
     * 然后长按菜单项需要的操作，通过回调的方式传递到{@link ChatActivity#setItemClickListener()}中去实现
     * TODO 现在这种实现并不是最优，因为在每一个 Item 中都要去实现弹出一个 Dialog，但是又不想自定义dialog
     */
    @Override protected void onItemLongClick() {
        String[] menus = null;
        // 这里要根据消息的类型去判断要弹出的菜单，是否是发送方，并且是发送成功才能撤回
        if (viewType == AConstants.MSG_TYPE_TEXT_RECEIVED) {
            menus = new String[] {
                    activity.getResources().getString(R.string.menu_chat_copy),
                    activity.getResources().getString(R.string.menu_chat_forward),
                    activity.getResources().getString(R.string.menu_chat_delete)
            };
        } else {
            menus = new String[] {
                    activity.getResources().getString(R.string.menu_chat_copy),
                    activity.getResources().getString(R.string.menu_chat_forward),
                    activity.getResources().getString(R.string.menu_chat_delete),
                    activity.getResources().getString(R.string.menu_chat_recall)
            };
        }

        // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
        alertDialogBuilder = new AlertDialog.Builder(activity);
        // 弹出框标题
        // alertDialogBuilder.setTitle(R.string.dialog_title_conversation);
        alertDialogBuilder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        adapter.onItemAction(AConstants.ACTION_COPY, message);
                        break;
                    case 1:
                        adapter.onItemAction(AConstants.ACTION_FORWARD, message);
                        break;
                    case 2:
                        adapter.onItemAction(AConstants.ACTION_DELETE, message);
                        break;
                    case 3:
                        adapter.onItemAction(AConstants.ACTION_RECALL, message);
                        break;
                }
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 刷新当前 ItemView
     */
    protected void refreshView() {
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (message.status()) {
            case SUCCESS:
                ackStatusView.setVisibility(View.VISIBLE);
                msgProgressBar.setVisibility(View.GONE);
                resendView.setVisibility(View.GONE);
                break;
            // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，这里把Create和Fail归为同一个状态
            case FAIL:
            case CREATE:
                ackStatusView.setVisibility(View.GONE);
                msgProgressBar.setVisibility(View.GONE);
                resendView.setVisibility(View.VISIBLE);
                resendView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        adapter.onItemAction(AConstants.ACTION_RESEND, message);
                    }
                });
                break;
            case INPROGRESS:
                ackStatusView.setVisibility(View.GONE);
                msgProgressBar.setVisibility(View.VISIBLE);
                resendView.setVisibility(View.GONE);
                break;
        }
        // 设置消息ACK 状态
        setAckStatusView();
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override protected void onInflateView() {
        if (viewType == AConstants.MSG_TYPE_TEXT_SEND) {
            inflater.inflate(R.layout.item_msg_text_send, this);
        } else {
            inflater.inflate(R.layout.item_msg_text_received, this);
        }

        bubbleLayout = findViewById(R.id.layout_bubble);
        avatarView = (VMImageView) findViewById(R.id.img_avatar);
        contentView = (TextView) findViewById(R.id.text_content);
        usernameView = (TextView) findViewById(R.id.text_username);
        msgTimeView = (TextView) findViewById(R.id.text_time);
        resendView = (ImageView) findViewById(R.id.img_resend);
        msgProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ackStatusView = (ImageView) findViewById(R.id.img_msg_ack);
    }
}
