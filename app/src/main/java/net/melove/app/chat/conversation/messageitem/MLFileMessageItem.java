package net.melove.app.chat.conversation.messageitem;

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

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.eventbus.MLMessageEvent;
import net.melove.app.chat.communal.util.MLDateUtil;
import net.melove.app.chat.communal.widget.MLImageView;
import net.melove.app.chat.conversation.MLChatActivity;
import net.melove.app.chat.conversation.MLMessageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class MLFileMessageItem extends MLMessageItem {


    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context  上下文对象
     * @param adapter  适配器
     * @param viewType item类型
     */
    public MLFileMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
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

        // 判断如果是单聊或者消息是发送方，不显示username
        if (mMessage.getChatType() == EMMessage.ChatType.Chat || mMessage.direct() == EMMessage.Direct.SEND) {
            mUsernameView.setVisibility(View.GONE);
        } else {
            // 设置消息消息发送者的名称
            mUsernameView.setText(message.getFrom());
            mUsernameView.setVisibility(View.VISIBLE);
        }
        // 设置消息时间
        mTimeView.setText(MLDateUtil.getRelativeTime(mMessage.getMsgTime()));

        EMNormalFileMessageBody fileBody = (EMNormalFileMessageBody) mMessage.getBody();
        String filename = fileBody.getFileName();
        // 设置文件名
        mContentView.setText(filename);
        // 设置文件大小
        String fileExtend = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        mContentSizeView.setText(TextFormater.getDataSize(fileBody.getFileSize()) + "  " + fileExtend);

        // 刷新界面显示
        refreshView();
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
        if (mViewType == MLConstants.MSG_TYPE_FILE_RECEIVED) {
            menus = new String[]{
                    mActivity.getResources().getString(R.string.ml_menu_chat_forward),
                    mActivity.getResources().getString(R.string.ml_menu_chat_delete)
            };
        } else {
            menus = new String[]{
                    mActivity.getResources().getString(R.string.ml_menu_chat_forward),
                    mActivity.getResources().getString(R.string.ml_menu_chat_delete),
                    mActivity.getResources().getString(R.string.ml_menu_chat_recall)
            };
        }

        // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
        // 弹出框标题
        // alertDialogBuilder.setTitle(R.string.ml_dialog_title_conversation);
        alertDialogBuilder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_FORWARD);
                    break;
                case 1:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_DELETE);
                    break;
                case 2:
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_RECALL);
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
        if (mMessage.getBooleanAttribute(MLConstants.ML_ATTR_BURN, false)) {
        } else {
        }
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (mMessage.status()) {
        case SUCCESS:
            mAckStatusView.setVisibility(View.VISIBLE);
            mProgressLayout.setVisibility(View.GONE);
            mResendView.setVisibility(View.GONE);
            break;
        case FAIL:
        case CREATE:
            // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，所以这里把CREATE状态莪要设置为失败
            mAckStatusView.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.GONE);
            mResendView.setVisibility(View.VISIBLE);
            mResendView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.onItemAction(mMessage, MLConstants.ML_ACTION_MSG_RESEND);
                }
            });
            break;
        case INPROGRESS:
            mAckStatusView.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.VISIBLE);
            mResendView.setVisibility(View.GONE);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLMessageEvent event) {
        EMMessage message = event.getMessage();
        if (!message.getMsgId().equals(mMessage.getMsgId())) {
            return;
        }
        if (message.getType() == EMMessage.Type.IMAGE && event.getStatus() == EMMessage.Status.INPROGRESS) {
            // 设置消息进度百分比
            mPercentView.setText(String.valueOf(event.getProgress()));
        }
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override
    protected void onInflateView() {
        if (mViewType == MLConstants.MSG_TYPE_FILE_SEND) {
            mInflater.inflate(R.layout.item_msg_file_send, this);
        } else {
            mInflater.inflate(R.layout.item_msg_file_received, this);
        }

        mAvatarView = (MLImageView) findViewById(R.id.ml_img_msg_avatar);
        mImageView = (MLImageView) findViewById(R.id.ml_img_msg_image);
        mUsernameView = (TextView) findViewById(R.id.ml_text_msg_username);
        mTimeView = (TextView) findViewById(R.id.ml_text_msg_time);
        mContentView = (TextView) findViewById(R.id.ml_text_msg_content);
        mContentSizeView = (TextView) findViewById(R.id.ml_text_msg_size);
        mResendView = (ImageView) findViewById(R.id.ml_img_msg_resend);
        mProgressLayout = findViewById(R.id.ml_layout_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.ml_progressbar_msg);
        mPercentView = (TextView) findViewById(R.id.ml_text_msg_progress_percent);
        mAckStatusView = (ImageView) findViewById(R.id.ml_img_msg_ack);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
