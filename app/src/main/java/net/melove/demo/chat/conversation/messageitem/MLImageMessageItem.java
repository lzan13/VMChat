package net.melove.demo.chat.conversation.messageitem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.util.MLMessageUtils;
import net.melove.demo.chat.common.widget.MLImageView;
import net.melove.demo.chat.conversation.MLMessageAdapter;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class MLImageMessageItem extends MLMessageItem {

    private MLHandler mHandler;

    public MLImageMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
        onInflateView();
        mHandler = new MLHandler();
    }

    /**
     * 实现数据的填充
     *
     * @param message  需要展示的 EMMessage 对象
     * @param position 当前item 在列表中的位置
     */
    @Override
    public void onSetupView(EMMessage message, int position) {
        mMessage = message;
        mPosition = position;
        // 设置消息消息发送者的名称
        mUsernameView.setText(message.getFrom());
        // 设置消息时间
        mTimeView.setText(MLDate.long2Time(message.getMsgTime()));

        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
        String thumbImagePath = "";
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_RECEIVED) {
            thumbImagePath = MLMessageUtils.getThumbImagePath(body.getThumbnailUrl());
        }

        // 给当前item 设置点击与长按事件监听
        mAdapter.setOnItemClick(this, mPosition);

        setCallback();
        refreshView();
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
            mProgressBar.setVisibility(View.GONE);
            mPercentView.setVisibility(View.GONE);
            mResendView.setVisibility(View.GONE);
            break;
        case FAIL:
        case CREATE:
            // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，所以这里把CREATE状态莪要设置为失败
            mAckStatusView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mPercentView.setVisibility(View.GONE);
            mResendView.setVisibility(View.VISIBLE);
            mResendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resendMessage(mMessage.getMsgId());
                }
            });
            break;
        case INPROGRESS:
            mAckStatusView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mPercentView.setVisibility(View.VISIBLE);
            mResendView.setVisibility(View.GONE);
            break;
        }
        // 设置消息ACK 状态
        setAckStatusView();
    }


    private void showImage(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        mImageView.setImageBitmap(bitmap);
    }

    /**
     * 设置当前消息的callback回调
     */
    protected void setCallback() {
        setMessageCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                mHandler.sendMessage(mHandler.obtainMessage(CALLBACK_STATUS_SUCCESS));
            }

            @Override
            public void onError(int i, String s) {
                mHandler.sendMessage(mHandler.obtainMessage(CALLBACK_STATUS_ERROR));
            }

            @Override
            public void onProgress(int i, String s) {
                Message msg = mHandler.obtainMessage(CALLBACK_STATUS_PROGRESS);
                msg.arg1 = i;
                mHandler.sendMessage(msg);
            }
        });
    }

    class MLHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case CALLBACK_STATUS_SUCCESS:
                refreshView();
                break;
            case CALLBACK_STATUS_ERROR:
                refreshView();
                break;
            case CALLBACK_STATUS_PROGRESS:
                mPercentView.setText(String.format("%%d", msg.arg1));
                break;
            }
        }
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override
    protected void onInflateView() {
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_SEND) {
            mInflater.inflate(R.layout.item_msg_image_send, this);
        } else {
            mInflater.inflate(R.layout.item_msg_image_received, this);
        }

        mAvatarView = (MLImageView) findViewById(R.id.ml_img_msg_avatar);
        mImageView = (MLImageView) findViewById(R.id.ml_img_msg_image);
        mUsernameView = (TextView) findViewById(R.id.ml_text_msg_username);
        mTimeView = (TextView) findViewById(R.id.ml_text_msg_time);
        mResendView = (ImageView) findViewById(R.id.ml_img_msg_resend);
        mProgressBar = (ProgressBar) findViewById(R.id.ml_progressbar_msg);
        mPercentView = (TextView) findViewById(R.id.ml_text_msg_progress_percent);
        mAckStatusView = (ImageView) findViewById(R.id.ml_img_msg_ack);
    }

}
