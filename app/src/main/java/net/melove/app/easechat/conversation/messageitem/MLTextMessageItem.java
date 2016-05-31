package net.melove.app.easechat.conversation.messageitem;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.app.easechat.R;
import net.melove.app.easechat.application.MLConstants;
import net.melove.app.easechat.communal.util.MLDate;
import net.melove.app.easechat.communal.widget.MLImageView;
import net.melove.app.easechat.conversation.MLChatActivity;
import net.melove.app.easechat.conversation.MLMessageAdapter;

/**
 * Created by lz on 2016/3/20.
 * 文字消息处理类
 */
public class MLTextMessageItem extends MLMessageItem {

    public MLTextMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);

        onInflateView();

    }

    /**
     * 实现数据的填充
     *
     * @param message  需要展示的 EMMessage 对象
     */
    @Override
    public void onSetupView(EMMessage message) {
        mMessage = message;

        // 设置消息消息发送者的名称
        mUsernameView.setText(message.getFrom());
        // 设置消息时间
        mTimeView.setText(MLDate.long2Time(message.getMsgTime()));

        EMTextMessageBody body = (EMTextMessageBody) mMessage.getBody();
        String messageStr = body.getMessage().toString();
        mContentView.setText(messageStr);

        // 刷新界面显示
        refreshView();
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override
    protected void onInflateView() {
        if (mViewType == MLConstants.MSG_TYPE_TEXT_SEND) {
            mInflater.inflate(R.layout.item_msg_text_send, this);
        } else {
            mInflater.inflate(R.layout.item_msg_text_received, this);
        }

        mAvatarView = (MLImageView) findViewById(R.id.ml_img_msg_avatar);
        mContentView = (TextView) findViewById(R.id.ml_text_msg_content);
        mUsernameView = (TextView) findViewById(R.id.ml_text_msg_username);
        mTimeView = (TextView) findViewById(R.id.ml_text_msg_time);
        mResendView = (ImageView) findViewById(R.id.ml_img_msg_resend);
        mProgressBar = (ProgressBar) findViewById(R.id.ml_progressbar_msg);
        mAckStatusView = (ImageView) findViewById(R.id.ml_img_msg_ack);
    }

    /**
     * 实现当前Item 的长按操作，因为各个Item类型不同，需要的实现操作不同，所以长按菜单的弹出在Item中实现，
     * 然后长按菜单项需要的操作，通过回调的方式传递到{@link MLChatActivity#setItemClickListener()}中去实现
     * TODO 现在这种实现并不是最优，因为在每一个 Item 中都要去实现弹出一个 Dialog，但是又不想自定义dialog
     */
    @Override
    protected void onItemLongClick() {
        String[] menus = null;
        // 这里要根据消息的类型去判断要弹出的菜单，是否是发送方，并且是发送成功才能撤回
        if (mViewType == MLConstants.MSG_TYPE_TEXT_RECEIVED) {
            menus = new String[]{
                    mActivity.getResources().getString(R.string.ml_menu_chat_copy),
                    mActivity.getResources().getString(R.string.ml_menu_chat_forward),
                    mActivity.getResources().getString(R.string.ml_menu_chat_delete)
            };
        } else {
            menus = new String[]{
                    mActivity.getResources().getString(R.string.ml_menu_chat_copy),
                    mActivity.getResources().getString(R.string.ml_menu_chat_forward),
                    mActivity.getResources().getString(R.string.ml_menu_chat_delete),
                    mActivity.getResources().getString(R.string.ml_menu_chat_recall)
            };
        }

        // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
        AlertDialog.Builder menuDialog = new AlertDialog.Builder(mActivity);
        menuDialog.setTitle(R.string.ml_dialog_title_conversation);
        menuDialog.setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_COPY);
                    break;
                case 1:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_FORWARD);
                    break;
                case 2:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_DELETE);
                    break;
                case 3:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_RECALL);
                    break;
                }
            }
        });
        menuDialog.show();
    }

    /**
     * 刷新当前 ItemView
     */
    protected void refreshView() {
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (mMessage.status()) {
        case SUCCESS:
            mAckStatusView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mResendView.setVisibility(View.GONE);
            break;
        // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，这里把Create和Fail归为同一个状态
        case FAIL:
        case CREATE:
            mAckStatusView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mResendView.setVisibility(View.VISIBLE);
            mResendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_RESEND);
                }
            });
            break;
        case INPROGRESS:
            mAckStatusView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mResendView.setVisibility(View.GONE);
            break;
        }
        // 设置消息ACK 状态
        setAckStatusView();
    }

}
