package com.vmloft.develop.app.chat.chat.messageitem;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.app.chat.chat.ChatActivity;
import com.vmloft.develop.app.chat.chat.MessageAdapter;
import com.vmloft.develop.library.tools.utils.VMDateUtil;
import com.vmloft.develop.library.tools.widget.VMImageView;

/**
 * Created by lz on 2016/3/20.
 * 通话结束扩展消息处理类
 */
public class CallMessageItem extends MessageItem {

    private ImageView mCallIcon;

    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context 上下文对象
     * @param adapter 适配器
     * @param viewType item类型
     */
    public CallMessageItem(Context context, MessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
    }

    /**
     * 实现数据的填充
     *
     * @param message 需要展示的 EMMessage 对象
     */
    @Override public void onSetupView(EMMessage message) {
        mMessage = message;

        // 判断如果是单聊或者消息是发送方，不显示username
        if (mMessage.getChatType() == EMMessage.ChatType.Chat
                || mMessage.direct() == EMMessage.Direct.SEND) {
            usernameView.setVisibility(View.GONE);
        } else {
            // 设置消息消息发送者的名称
            usernameView.setText(message.getFrom());
            usernameView.setVisibility(View.VISIBLE);
        }

        // 设置消息时间
        msgTimeView.setText(VMDateUtil.getRelativeTime(message.getMsgTime()));

        EMTextMessageBody body = (EMTextMessageBody) mMessage.getBody();
        String messageStr = body.getMessage().toString();
        contentView.setText(messageStr);

        if (mMessage.getBooleanAttribute(Constants.ATTR_CALL_VIDEO, false)) {
            mCallIcon.setImageResource(R.drawable.ic_videocam_white_24dp);
        } else {
            mCallIcon.setImageResource(R.drawable.ic_call_white_24dp);
        }
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
        // 这里因为是通话结束保存的消息，只有一个删除操作
        menus = new String[] {
                activity.getResources().getString(R.string.menu_chat_delete)
        };

        // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
        alertDialogBuilder = new AlertDialog.Builder(activity);
        // 弹出框标题
        // alertDialogBuilder.setTitle(R.string.dialog_title_conversation);
        alertDialogBuilder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mAdapter.onItemAction(Constants.ACTION_DELETE, mMessage);
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
        // 因为是自己保存的通话类型消息，不存在失败和重发操作，也不展示消息是否已读
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override protected void onInflateView() {
        if (mViewType == Constants.MSG_TYPE_CALL_SEND) {
            mInflater.inflate(R.layout.item_msg_call_send, this);
        } else {
            mInflater.inflate(R.layout.item_msg_call_received, this);
        }

        bubbleLayout = findViewById(R.id.layout_bubble);
        avatarView = (VMImageView) findViewById(R.id.img_avatar);
        contentView = (TextView) findViewById(R.id.text_content);
        usernameView = (TextView) findViewById(R.id.text_username);
        msgTimeView = (TextView) findViewById(R.id.text_time);
        mCallIcon = (ImageView) findViewById(R.id.img_call_icon);
    }
}
