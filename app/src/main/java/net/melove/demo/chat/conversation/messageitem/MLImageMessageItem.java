package net.melove.demo.chat.conversation.messageitem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.widget.MLImageView;
import net.melove.demo.chat.conversation.MLMessageAdapter;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class MLImageMessageItem extends MLMessageItem {

    // 当前 Item 需要处理的 EMMessage 对象
    private EMMessage mMessage;

    public MLImageMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
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
        // 设置消息消息发送者的名称
        mUsernameView.setText(message.getFrom());
        // 设置消息时间
        mTimeView.setText(MLDate.long2Time(message.getMsgTime()));

        EMImageMessageBody body = (EMImageMessageBody) message.getBody();

        String thumbPath = body.thumbnailLocalPath();
        Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
        mImageView.setImageBitmap(bitmap);

        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (message.status()) {
            case SUCCESS:
                mMessageState.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                break;
            case FAIL:
                mProgressBar.setVisibility(View.GONE);
                mMessageState.setVisibility(View.VISIBLE);
                mMessageState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                break;
            case INPROGRESS:
                mProgressBar.setVisibility(View.VISIBLE);
                mMessageState.setVisibility(View.GONE);
                break;
            case CREATE:
                mProgressBar.setVisibility(View.VISIBLE);
                mMessageState.setVisibility(View.GONE);
                break;
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
        mMessageState = (ImageView) findViewById(R.id.ml_img_msg_state);
        mProgressBar = (ProgressBar) findViewById(R.id.ml_progressbar_msg);
    }

}
