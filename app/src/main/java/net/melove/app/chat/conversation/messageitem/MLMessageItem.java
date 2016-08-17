package net.melove.app.chat.conversation.messageitem;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.communal.util.MLLog;
import net.melove.app.chat.communal.widget.MLImageView;
import net.melove.app.chat.conversation.MLChatActivity;
import net.melove.app.chat.conversation.MLMessageAdapter;

/**
 * Created by lz on 2016/3/20.
 * ViewHoler itemView 封装类
 * 不同的消息类型都可以继承此类进行实现消息的展示
 */
public abstract class MLMessageItem extends LinearLayout {

    // 上下文对象
    protected Context mContext;
    protected Activity mActivity;

    // 布局内容填充者，将xml布局文件解析为view
    protected LayoutInflater mInflater;
    protected MLMessageAdapter mAdapter;

    // item 类型
    protected int mViewType;

    // 当前 Item 需要处理的 EMMessage 对象
    protected EMMessage mMessage;

    // 弹出框
    protected AlertDialog.Builder alertDialogBuilder;
    protected AlertDialog alertDialog;

    /**
     * 聊天界面不同的item 所有要显示的控件，每个item可能显示的个数不同，
     * 比如撤回消息只显示 mContentView和 mTimeView
     */
    // 显示聊天头像
    protected MLImageView mAvatarView;
    // 显示图片、文件消息显示文件图标
    protected MLImageView mImageView;
    // 显示用户名
    protected TextView mUsernameView;
    // 显示聊天内容、文件消息就显示文件名
    protected TextView mContentView;
    // 文件消息显示文件大小
    protected TextView mContentSizeView;
    // 显示时间
    protected TextView mTimeView;
    // 重发按钮
    protected ImageView mResendView;
    // 消息进度
    protected View mProgressLayout;
    protected ProgressBar mProgressBar;
    protected TextView mPercentView;
    // Ack状态显示
    protected ImageView mAckStatusView;


    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context  上下文对象
     * @param adapter  适配器
     * @param viewType item类型
     */
    public MLMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context);
        mContext = context;
        mActivity = (Activity) context;
        mInflater = LayoutInflater.from(context);
        mAdapter = adapter;
        mViewType = viewType;

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置 Item 项点击的 Action
                mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_CLICK);
            }
        });
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick();
                return false;
            }
        });
    }

    /**
     * 抽象方法，填充当前 Item，子类必须实现
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
     * 当前Item 长按监听
     * 实现当前Item 的长按操作，因为各个Item类型不同，需要的实现操作不同，所以长按菜单的弹出在Item中实现，
     * 然后长按菜单项需要的操作，通过回调的方式传递到{@link MLChatActivity#setItemClickListener()}中去实现
     * TODO 现在这种实现并不是最优，因为在每一个 Item 中都要去实现弹出一个 Dialog，但是又不想自定义dialog
     */
    protected abstract void onItemLongClick();

    /**
     * 设置ACK的状态显示，包括消息送达，消息已读
     * 这个在弱网环境下ack状态会失败，
     */
    protected void setAckStatusView() {
        // 需要先判断下是不是单聊的消息，如果不是单聊会话就不发送和展示ACK状态
        if (mMessage.getChatType() != EMMessage.ChatType.Chat) {
            return;
        }
        // 判断是否需要消息已读ACK，以及消息发送状态
        if (!EMClient.getInstance().getOptions().getRequireAck() || !EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            return;
        }
        // 判断是否是接收方的消息，是则发送ACK给消息发送者
        if (mViewType == MLConstants.MSG_TYPE_TEXT_RECEIVED) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mMessage.getFrom(), mMessage.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        // 设置ACK 的状态显示
        if (mViewType == MLConstants.MSG_TYPE_TEXT_SEND) {
            if (mMessage.isAcked()) {
                // 表示对方已读消息，用两个对号表示
                mAckStatusView.setImageResource(R.mipmap.ic_done_all_white_18dp);
            } else if (mMessage.isDelivered()) {
                // 表示消息已经送达，对方收到了，用一个对号表示
                mAckStatusView.setImageResource(R.mipmap.ic_done_white_18dp);
            } else {
                mAckStatusView.setVisibility(View.GONE);
            }
        } else {
            mAckStatusView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        MLLog.i("onAttachedToWindow %s", mMessage.getMsgId());
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        MLLog.i("onDetachedFromWindow %s", mMessage.getMsgId());
        // 检查是否有弹出框，如果有则销毁，防止界面销毁时出现异常
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDetachedFromWindow();
    }
}
