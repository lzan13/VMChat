package com.vmloft.develop.app.chat.chat.messageitem;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.util.TextFormater;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.app.chat.chat.ChatActivity;
import com.vmloft.develop.app.chat.chat.MessageEvent;
import com.vmloft.develop.app.chat.chat.MessageAdapter;

import com.vmloft.develop.library.tools.utils.VMDateUtil;
import com.vmloft.develop.library.tools.widget.VMImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class FileMessageItem extends MessageItem {

    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context 上下文对象
     * @param adapter 适配器
     * @param viewType item类型
     */
    public FileMessageItem(Context context, MessageAdapter adapter, int viewType) {
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
        msgTimeView.setText(VMDateUtil.getRelativeTime(this.message.getMsgTime()));

        EMNormalFileMessageBody fileBody = (EMNormalFileMessageBody) this.message.getBody();
        String filename = fileBody.getFileName();

        // 设置文件名
        contentView.setText(filename);
        // 设置文件大小
        String fileExtend = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        fileSizeView.setText(TextFormater.getDataSize(fileBody.getFileSize()) + "  " + fileExtend);

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
        if (viewType == Constants.MSG_TYPE_FILE_RECEIVED) {
            menus = new String[] {
                    activity.getResources().getString(R.string.menu_chat_forward),
                    activity.getResources().getString(R.string.menu_chat_delete)
            };
        } else {
            menus = new String[] {
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
                        adapter.onItemAction(Constants.ACTION_FORWARD, message);
                        break;
                    case 1:
                        adapter.onItemAction(Constants.ACTION_DELETE, message);
                        break;
                    case 2:
                        adapter.onItemAction(Constants.ACTION_RECALL, message);
                        break;
                }
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 刷新当前item
     */
    protected void refreshView() {
        // 判断是不是阅后即焚的消息
        if (message.getBooleanAttribute(Constants.ATTR_BURN, false)) {
        } else {
        }
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (message.status()) {
            case SUCCESS:
                ackStatusView.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                resendView.setVisibility(View.GONE);
                break;
            case FAIL:
            case CREATE:
                // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，所以这里把CREATE状态莪要设置为失败
                ackStatusView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                resendView.setVisibility(View.VISIBLE);
                resendView.setOnClickListener(new OnClickListener() {
                    @Override public void onClick(View v) {
                        adapter.onItemAction(Constants.ACTION_RESEND, message);
                    }
                });
                break;
            case INPROGRESS:
                ackStatusView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                resendView.setVisibility(View.GONE);
                break;
        }
        // 设置消息ACK 状态
        setAckStatusView();
    }

    /**
     * 使用注解的方式实现EventBus的观察者方法，用来监听特定事件
     *
     * @param event 要监听的事件类型
     */
    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(MessageEvent event) {
        EMMessage message = event.getMessage();
        if (!message.getMsgId().equals(this.message.getMsgId())) {
            return;
        }
        if (message.getType() == EMMessage.Type.IMAGE
                && event.getStatus() == EMMessage.Status.INPROGRESS) {
            // 设置消息进度百分比
            percentView.setText(String.valueOf(event.getProgress()));
        }
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override protected void onInflateView() {
        if (viewType == Constants.MSG_TYPE_FILE_SEND) {
            inflater.inflate(R.layout.item_msg_file_send, this);
        } else {
            inflater.inflate(R.layout.item_msg_file_received, this);
        }

        bubbleLayout = findViewById(R.id.layout_bubble);
        avatarView = (VMImageView) findViewById(R.id.img_avatar);
        imageView = (VMImageView) findViewById(R.id.img_image);
        usernameView = (TextView) findViewById(R.id.text_username);
        msgTimeView = (TextView) findViewById(R.id.text_time);
        contentView = (TextView) findViewById(R.id.text_content);
        fileSizeView = (TextView) findViewById(R.id.text_size);
        resendView = (ImageView) findViewById(R.id.img_resend);
        progressLayout = findViewById(R.id.layout_progress);
        msgProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        percentView = (TextView) findViewById(R.id.text_progress_percent);
        ackStatusView = (ImageView) findViewById(R.id.img_msg_ack);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
