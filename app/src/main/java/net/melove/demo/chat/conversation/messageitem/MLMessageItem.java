package net.melove.demo.chat.conversation.messageitem;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.widget.MLImageView;
import net.melove.demo.chat.common.widget.MLToast;
import net.melove.demo.chat.conversation.MLMessageAdapter;

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

    // 消息Callback结果
    protected final int CALLBACK_STATUS_SUCCESS = 0;
    protected final int CALLBACK_STATUS_ERROR = 1;
    protected final int CALLBACK_STATUS_PROGRESS = 2;


    // item 类型
    protected int mViewType;

    // 当前 Item 需要处理的 EMMessage 对象
    protected EMMessage mMessage;
    protected int mPosition;

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
    protected ProgressBar mProgressBar;
    protected TextView mPercentView;
    // Ack状态显示
    protected ImageView mAckStatusView;

    public MLMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context);
        mContext = context;
        mActivity = (Activity) context;
        mInflater = LayoutInflater.from(context);
        mAdapter = adapter;
        mViewType = viewType;
    }

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
        if (mViewType == MLConstants.MSG_TYPE_TXT_RECEIVED) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(mMessage.getFrom(), mMessage.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        // 设置ACK 的状态显示
        if (mViewType == MLConstants.MSG_TYPE_TXT_SEND) {
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

    /**
     * 设置消息的callback回调
     */
    protected void setMessageCallback(@Nullable final EMCallBack callback) {
        // 设置消息状态回调
        mMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                MLLog.i("消息发送成功 %s", mMessage.getMsgId());
                callback.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                MLLog.i("消息发送失败 code: %d, error: %s", i, s);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMessage.getError() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                            MLToast.errorToast(R.string.ml_toast_msg_have_illegal).show();
                        } else if (mMessage.getError() == EMError.GROUP_PERMISSION_DENIED) {
                            MLToast.errorToast(R.string.ml_toast_msg_not_join_group).show();
                        } else {
                            MLToast.errorToast(R.string.ml_toast_msg_send_faild).show();
                        }
                    }
                });
                callback.onError(i, s);
            }

            @Override
            public void onProgress(int i, String s) {
                MLLog.i("消息发送中 %d - %s", i, s);
                callback.onProgress(i, s);
            }
        });
    }

    /**
     * 处理数据显示
     *
     * @param message 需要展示的 EMMessage 对象
     */
    public abstract void onSetupView(EMMessage message, int position);

    /**
     * 抽象方法，填充当前 Item，子类必须实现
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    protected abstract void onInflateView();

}
