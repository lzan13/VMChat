package com.vmloft.develop.app.chat.ui.chat.messageitem;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.ui.chat.ChatActivity;
import com.vmloft.develop.app.chat.ui.chat.MessageAdapter;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.widget.VMImageView;

/**
 * Created by lz on 2016/3/20.
 * ViewHoler itemView 封装类
 * 不同的消息类型都可以继承此类进行实现消息的展示
 */
public abstract class MessageItem extends LinearLayout {

    // 上下文对象
    protected Context context;
    protected Activity activity;

    // 布局内容填充者，将xml布局文件解析为view
    protected LayoutInflater inflater;
    protected MessageAdapter adapter;

    // item 类型
    protected int viewType;

    // 当前 Item 需要处理的 EMMessage 对象
    protected EMMessage message;

    // 弹出框
    protected AlertDialog.Builder alertDialogBuilder;
    protected AlertDialog alertDialog;

    /**
     * 聊天界面不同的item 所有要显示的控件，每个item可能显示的个数不同，
     * 比如撤回消息只显示 mContentView和 msgTimeView
     */
    protected View bubbleLayout;
    // 显示聊天头像
    protected VMImageView avatarView;
    // 显示图片、文件消息显示文件图标
    protected VMImageView imageView;
    // 显示用户名
    protected TextView usernameView;
    // 显示时间
    protected TextView msgTimeView;
    // 重发按钮
    protected ImageView resendView;
    // Ack状态显示
    protected ImageView ackStatusView;

    // 显示聊天内容、文件消息就显示文件名
    protected TextView contentView;

    // 消息进度布局部分
    protected View progressLayout;
    // 进度圈
    protected ProgressBar msgProgressBar;
    // 进度百分比
    protected TextView percentView;

    // 文件消息显示文件大小
    protected TextView fileSizeView;

    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context 上下文对象
     * @param adapter 适配器
     * @param viewType item类型
     */
    public MessageItem(Context context, MessageAdapter adapter, int viewType) {
        super(context);
        this.context = context;
        activity = (Activity) context;
        inflater = LayoutInflater.from(context);
        this.adapter = adapter;
        this.viewType = viewType;

        onInflateView();
        onBubbleListener();
    }

    /**
     * 填充当前 Item，子类必须实现
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    protected abstract void onInflateView();

    /**
     * 处理数据显示
     *
     * @param message 需要展示的 EMMessage 对象
     */
    public abstract void onSetupView(EMMessage message);

    /**
     * 设置Item 气泡点击以及长按监听
     */
    protected void onBubbleListener() {
        if (avatarView != null) {
            // 设置消息头像点击监听
            avatarView.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View view) {
                    adapter.onItemAction(avatarView.getId(), message);
                }
            });
        }
        if (bubbleLayout != null) {
            // 设置Item 气泡的点击监听
            bubbleLayout.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    // 设置 Item 项点击的 Action
                    adapter.onItemAction(AConstants.ACTION_CLICK, message);
                }
            });
            // 设置Item 气泡的长按监听
            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    onItemLongClick();
                    return false;
                }
            });
        }
    }

    /**
     * 当前 Item 的点击监听
     */
    protected void onItemClick() {

    }

    /**
     * 当前Item 长按监听
     * 实现当前Item 的长按操作，因为各个Item类型不同，需要的实现操作不同，所以长按菜单的弹出在Item中实现，
     * 然后长按菜单项需要的操作，通过回调的方式传递到{@link ChatActivity#setItemClickListener()}中去实现
     * TODO 现在这种实现并不是最优，因为在每一个 Item 中都要去实现弹出一个 Dialog，但是又不想自定义dialog
     */
    protected abstract void onItemLongClick();

    /**
     * 设置ACK的状态显示，包括消息送达，消息已读
     * 这个在弱网环境下ack状态会失败，
     */
    protected void setAckStatusView() {
        // 需要先判断下是不是单聊的消息，如果不是单聊会话就不发送和展示ACK状态
        if (message.getChatType() != EMMessage.ChatType.Chat) {
            ackStatusView.setVisibility(View.GONE);
            return;
        }
        // 判断是否需要消息已读ACK，以及消息发送状态
        if (!EMClient.getInstance().getOptions().getRequireAck() || !EMClient.getInstance()
                .getOptions()
                .getRequireDeliveryAck()) {
            return;
        }
        // 判断是否是接收方的消息，是则发送ACK给消息发送者
        if (viewType == AConstants.MSG_TYPE_TEXT_RECEIVED) {
            message.setMessageStatusCallback(new EMCallBack() {
                @Override public void onSuccess() {
                    VMLog.d("send message ack onSuccess");
                }

                @Override public void onError(int i, String s) {
                    VMLog.d("send message ack onError");
                }

                @Override public void onProgress(int i, String s) {
                    VMLog.d("send message ack onProgress %d, %s", i, s);
                }
            });
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        // 设置ACK 的状态显示
        if (viewType == AConstants.MSG_TYPE_TEXT_SEND) {
            if (message.isAcked()) {
                // 表示对方已读消息，用两个对号表示
                ackStatusView.setImageResource(R.drawable.ic_done_all_white_18dp);
            } else if (message.isDelivered()) {
                // 表示消息已经送达，对方收到了，用一个对号表示
                ackStatusView.setImageResource(R.drawable.ic_done_white_18dp);
            } else {
                ackStatusView.setVisibility(View.GONE);
            }
        } else {
            ackStatusView.setVisibility(View.GONE);
        }
    }

    @Override protected void onAttachedToWindow() {
        //        VMLog.i("onAttachedToWindow %s", message.getMsgId());
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow() {
        //        VMLog.i("onDetachedFromWindow %s", message.getMsgId());
        // 检查是否有弹出框，如果有则销毁，防止界面销毁时出现异常
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDetachedFromWindow();
    }
}
