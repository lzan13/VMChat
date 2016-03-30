package net.melove.demo.chat.conversation.messageitem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLBitmapUtil;
import net.melove.demo.chat.common.util.MLCacheUtils;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.util.MLDimen;
import net.melove.demo.chat.common.util.MLFile;
import net.melove.demo.chat.common.util.MLMessageUtils;
import net.melove.demo.chat.common.widget.MLImageView;
import net.melove.demo.chat.conversation.MLMessageAdapter;

import java.io.File;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class MLImageMessageItem extends MLMessageItem {

    private int thumbnailsMax = MLDimen.dp2px(R.dimen.ml_dimen_192);
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
        mTimeView.setText(MLDate.long2Time(mMessage.getMsgTime()));

        EMImageMessageBody imgBody = (EMImageMessageBody) mMessage.getBody();
        // 设置显示图片控件的默认大小
        //        int[] size = MLBitmapUtil.getImageSize(imgBody.getWidth(), imgBody.getHeight());
        //        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(size[0] + 4, size[1] + 4));
        // 判断下是否是接收方的消息
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_RECEIVED) {
            // 判断消息是否处于下载状态，如果是下载状态设置一个默认的图片
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING
                    || imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                mImageView.setImageResource(R.mipmap.image_default);
            }
        }
        /**
         * 获取当前图片的缩略图地址，以及原图地址
         * TODO 因为SDK在发送图片的时候把 LocalPath设置为了压缩后的临时图片的地址，这里暂时使用缩略图保存的的原始图片地址作为原始图片地址
         */
        String thumbnailsPath = "";
        String fullSizePath = "";
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_RECEIVED) {
            // 接收方获取缩略图的路径
            thumbnailsPath = imgBody.thumbnailLocalPath();
            fullSizePath = imgBody.getLocalUrl();
        } else {
            thumbnailsPath = MLMessageUtils.getThumbImagePath(fullSizePath);
            fullSizePath = imgBody.thumbnailLocalPath();
        }

        // 设置缩略图的显示
        showThumbnailsImage(thumbnailsPath, fullSizePath, imgBody.getWidth(), imgBody.getHeight());

        // 给当前item 设置点击与长按事件监听
        mAdapter.setOnItemClick(this, mPosition);

        setCallback();
        refreshView();
    }

    /**
     * 设置缩略图的显示，并将缩略图添加到缓存
     *
     * @param thumbnailsPath 缩略图的路径
     * @param fullSizePath   原始图片的路径
     */
    private void showThumbnailsImage(final String thumbnailsPath, final String fullSizePath, final int width, final int height) {
        Bitmap bitmap = MLCacheUtils.getInstance().optBitmap(thumbnailsPath);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            new AsyncTask<Object, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Object... params) {
                    File thumbnailsFile = new File(thumbnailsPath);
                    File fullSizeFile = new File(fullSizePath);
                    Bitmap tempBitmap = null;
                    // 首先判断原图文件存在，同时缩略图不存在，或者存在缩略图大小比较小，图片不是很清晰时，通过原图重新生成缩略图
                    if (fullSizeFile.exists() && fullSizeFile.length() > 20480
                            && (!thumbnailsFile.exists() || thumbnailsFile.length() < 10240)) {
                        // 根据原图获取指定大小的缩略图
                        tempBitmap = MLBitmapUtil.compressBitmap(fullSizePath, thumbnailsMax, thumbnailsMax);
                        // 文件生成成功之后，把新的Bitmap保存到本地磁盘中
                        MLFile.saveBitmapToSDCard(tempBitmap, thumbnailsPath);
                    } else if (width > thumbnailsMax || height > thumbnailsMax) {
                        // 根据缩略图路径直接获取Bitmap
                        tempBitmap = MLBitmapUtil.compressBitmap(thumbnailsPath, thumbnailsMax, thumbnailsMax);
                    } else {
                        // 当图片本身就很小时，直接加在图片
                        tempBitmap = BitmapFactory.decodeFile(thumbnailsPath);
                    }
                    return tempBitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        // 设置图片控件为当前bitmap
                        mImageView.setImageBitmap(bitmap);
                        // 将Bitmap对象添加到缓存中去
                        MLCacheUtils.getInstance().putBitmap(thumbnailsPath, bitmap);
                    } else {
                        // 判断当前消息的状态，如果是失败的消息，则去重新下载缩略图
                        if (mMessage.status() == EMMessage.Status.FAIL) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // 下载缩略图
                                    EMClient.getInstance().chatManager().downloadThumbnail(mMessage);
                                }
                            }).start();
                        }
                    }
                }
            }.execute();
        }
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
