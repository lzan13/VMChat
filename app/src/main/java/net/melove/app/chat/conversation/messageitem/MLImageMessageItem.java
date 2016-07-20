package net.melove.app.chat.conversation.messageitem;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.eventbus.MLMessageEvent;
import net.melove.app.chat.communal.util.MLBitmapUtil;
import net.melove.app.chat.communal.module.MLBitmapCache;
import net.melove.app.chat.communal.util.MLDateUtil;
import net.melove.app.chat.communal.util.MLDimen;
import net.melove.app.chat.communal.util.MLFileUtil;
import net.melove.app.chat.conversation.MLMessageUtils;
import net.melove.app.chat.communal.widget.MLImageView;
import net.melove.app.chat.conversation.MLChatActivity;
import net.melove.app.chat.conversation.MLMessageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class MLImageMessageItem extends MLMessageItem {

    private int thumbnailsMax = MLDimen.dp2px(R.dimen.ml_dimen_192);


    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context  上下文对象
     * @param adapter  适配器
     * @param viewType item类型
     */
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

        EMImageMessageBody imgBody = (EMImageMessageBody) mMessage.getBody();
        // 设置显示图片控件的默认大小
        //        int[] size = MLBitmapUtil.getImageSize(imgBody.getWidth(), imgBody.getHeight());
        //        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(size[0] + 4, size[1] + 4));
        int width = imgBody.getWidth();
        int height = imgBody.getHeight();
        float scale = MLBitmapUtil.getZoomScale(width, height, thumbnailsMax);
        ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
        lp.width = (int) (width / scale);
        lp.height = (int) (height / scale);
        mImageView.setLayoutParams(lp);

        // 判断下是否是接收方的消息
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_RECEIVED) {
            // 判断消息是否处于下载状态，如果是下载状态设置一个默认的图片
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING
                    || imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                mImageView.setBackgroundResource(R.mipmap.image_default);
            }
        }

        /**
         * 获取当前图片的缩略图地址，以及原图地址
         */
        String thumbnailsPath = "";
        String fullSizePath = "";
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_RECEIVED) {
            // 接收方获取缩略图的路径
            fullSizePath = imgBody.getLocalUrl();
            thumbnailsPath = imgBody.thumbnailLocalPath();
        } else {
            fullSizePath = imgBody.getLocalUrl();
            thumbnailsPath = MLMessageUtils.getThumbImagePath(fullSizePath);
        }

        // 为图片显示控件设置tag，在设置图片显示的时候，先判断下当前的tag是否是当前item的，是则显示图片
        mImageView.setTag(thumbnailsPath);

        // 设置缩略图的显示
        showThumbnailsImage(thumbnailsPath, fullSizePath, imgBody.getWidth(), imgBody.getHeight());

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
        if (mViewType == MLConstants.MSG_TYPE_IMAGE_RECEIVED) {
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
        alertDialogBuilder.setTitle(R.string.ml_dialog_title_conversation);
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
        menuDialog = alertDialogBuilder.create();
        menuDialog.show();
    }

    /**
     * 设置缩略图的显示，并将缩略图添加到缓存
     *
     * @param thumbnailsPath 缩略图的路径
     * @param fullSizePath   原始图片的路径
     */
    private void showThumbnailsImage(final String thumbnailsPath, final String fullSizePath, final int width, final int height) {
        Bitmap bitmap = MLBitmapCache.getInstance().optBitmap(thumbnailsPath);
        if (bitmap != null) {
            if (mImageView.getTag().equals(thumbnailsPath)) {
                mImageView.setImageBitmap(bitmap);
            }
        } else {
            // 暂时没有bitmap就设置一个默认的图片
            mImageView.setBackgroundResource(R.mipmap.image_default);
            new AsyncTask<Object, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Object... params) {
                    File thumbnailsFile = new File(thumbnailsPath);
                    File fullSizeFile = new File(fullSizePath);
                    Bitmap tempBitmap = null;

                    // 首先判断原图以及缩略图是否都存在，如果不存在直接返回
                    if (!fullSizeFile.exists() && !thumbnailsFile.exists()) {
                        return tempBitmap;
                    } else if ((!fullSizeFile.exists() && (width > thumbnailsMax || height > thumbnailsMax))
                            || thumbnailsFile.exists() && thumbnailsFile.length() > 1024 * 10 * 2) {
                        // 然后判断缩略图是否存在，并且足够清晰，则根据缩略图路径直接获取Bitmap
                        tempBitmap = MLBitmapUtil.loadBitmapThumbnail(thumbnailsPath, thumbnailsMax);
                    } else if (fullSizeFile.exists() && fullSizeFile.length() > 1024 * 10 * 5
                            && (width > thumbnailsMax || height > thumbnailsMax)) {
                        // 然后判断原图是否存在，通过原图重新生成缩略图
                        tempBitmap = MLBitmapUtil.loadBitmapThumbnail(fullSizePath, thumbnailsMax);
                        // 文件生成成功之后，把新的Bitmap保存到本地磁盘中
                        MLFileUtil.saveBitmapToSDCard(tempBitmap, thumbnailsPath);
                    } else {
                        // 当图片本身就很小时，直接加在图片
                        tempBitmap = MLBitmapUtil.loadBitmapByFile(thumbnailsPath, width);
                    }
                    return tempBitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        // 设置图片控件为当前bitmap
                        if (mImageView.getTag().equals(thumbnailsPath)) {
                            mImageView.setImageBitmap(bitmap);
                        }
                        // 将Bitmap对象添加到缓存中去
                        MLBitmapCache.getInstance().putBitmap(thumbnailsPath, bitmap);
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
            mProgressLayout.setVisibility(View.GONE);
            mResendView.setVisibility(View.GONE);
            break;
        // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，所以这里把CREATE状态莪要设置为失败
        case FAIL:
        case CREATE:
            mAckStatusView.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.GONE);
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
        if (message.status() == EMMessage.Status.INPROGRESS) {
            // 设置消息进度百分比
            mPercentView.setText(String.valueOf(event.getProgress()));
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

        // 通过 findViewById 实例化控件
        mAvatarView = (MLImageView) findViewById(R.id.ml_img_msg_avatar);
        mImageView = (MLImageView) findViewById(R.id.ml_img_msg_image);
        mUsernameView = (TextView) findViewById(R.id.ml_text_msg_username);
        mTimeView = (TextView) findViewById(R.id.ml_text_msg_time);
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
