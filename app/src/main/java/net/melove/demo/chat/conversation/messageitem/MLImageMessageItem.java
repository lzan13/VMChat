package net.melove.demo.chat.conversation.messageitem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        String thumbPath = body.thumbnailLocalPath();
        Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
        mImageView.setImageBitmap(bitmap);
        // 设置消息ACK 状态
        setAckStatusView();

        // 给当前item 设置点击与长按事件监听
        mAdapter.setOnItemClick(this, mPosition);
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
    }

}
