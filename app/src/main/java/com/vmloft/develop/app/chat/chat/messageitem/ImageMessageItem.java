package com.vmloft.develop.app.chat.chat.messageitem;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.chat.ChatActivity;
import com.vmloft.develop.app.chat.chat.MessageAdapter;
import com.vmloft.develop.app.chat.chat.MessageEvent;
import com.vmloft.develop.app.chat.chat.MessageUtils;

import com.vmloft.develop.library.tools.utils.bitmap.VMBitmap;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.widget.VMImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class ImageMessageItem extends MessageItem {

    // 定义图片缩略图限制
    private int thumbnailsMax = 0;
    private int thumbnailsMin = 192;
    private int mViewWidth;
    private int mViewHeight;

    /**
     * 构造方法，创建item的view，需要传递对应的参数
     *
     * @param context 上下文对象
     * @param adapter 适配器
     * @param viewType item类型
     */
    public ImageMessageItem(Context context, MessageAdapter adapter, int viewType) {
        super(context, adapter, viewType);
        thumbnailsMax = VMDimen.getDimenPixel( R.dimen.vm_dimen_192);
    }

    /**
     * 实现数据的填充
     *
     * @param message 需要展示的 EMMessage 对象
     */
    @Override public void onSetupView(EMMessage message) {
        this.message = message;
        EMClient.getInstance().chatManager().downloadAttachment(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override public void onSuccess() {
                VMLog.d("message download thumbnail onSuccess");
            }

            @Override public void onError(int i, String s) {
                VMLog.d("message download thumbnail onError");
            }

            @Override public void onProgress(int i, String s) {
                VMLog.d("message download thumbnail onProgress %d, %s", i, s);
            }
        });
        //EMClient.getInstance().chatManager().downloadThumbnail(message);
        // 判断如果是单聊或者消息是发送方，不显示username
        if (this.message.getChatType() == EMMessage.ChatType.Chat || this.message.direct() == EMMessage.Direct.SEND) {
            usernameView.setVisibility(View.GONE);
        } else {
            // 设置消息消息发送者的名称
            usernameView.setText(message.getFrom());
            usernameView.setVisibility(View.VISIBLE);
        }
        // 设置消息时间
        msgTimeView.setText(VMDate.getRelativeTime(this.message.getMsgTime()));

        // 获取图片消息体
        EMImageMessageBody imgBody = (EMImageMessageBody) this.message.getBody();
        // 取出图片原始宽高，这是在发送图片时发送方直接根据图片获得设置到body中的
        int width = imgBody.getWidth();
        int height = imgBody.getHeight();
        float scale = VMBitmap.getZoomScale(width, height, thumbnailsMax);
        // 根据图片原图大小，来计算缩略图要显示的大小，直接设置控件宽高
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        if (width <= thumbnailsMax && height <= thumbnailsMax) {
            if (width < thumbnailsMin) {
                lp.width = thumbnailsMin;
                lp.height = height * thumbnailsMin / width;
            } else {
                lp.width = width;
                lp.height = height;
            }
        } else {
            lp.width = (int) (width / scale);
            lp.height = (int) (height / scale);
        }
        mViewWidth = lp.width;
        mViewHeight = lp.height;
        // 设置显示图片控件的显示大小
        imageView.setLayoutParams(lp);

        // 判断下是否是接收方的消息
        if (viewType == AConstants.MSG_TYPE_IMAGE_RECEIVED) {
            // 判断消息是否处于下载状态，如果是下载状态设置一个默认的图片
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING
                    || imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            }
        }

        // 缩略图本地路径
        String thumbnailsPath = "";
        // 原图在本地路径
        String originalPath = "";
        if (viewType == AConstants.MSG_TYPE_IMAGE_RECEIVED) {
            // 接收方获取缩略图的路径
            originalPath = imgBody.getLocalUrl();
            thumbnailsPath = imgBody.thumbnailLocalPath();
        } else {
            // 发送方获取图片路径
            originalPath = imgBody.getLocalUrl();
            thumbnailsPath = MessageUtils.getThumbImagePath(originalPath);
        }
        // 为图片显示控件设置tag，在设置图片显示的时候，先判断下当前的tag是否是当前item的，是则显示图片
        //        imageView.setTag(thumbnailsPath);
        // 设置缩略图的显示
        showThumbnailsImage(thumbnailsPath, originalPath);

        // 刷新界面显示
        refreshView();
    }

    /**
     * 实现当前Item 的长按操作，因为各个Item类型不同，需要的实现操作不同，所以长按菜单的弹出在Item中实现，
     * 然后长按菜单项需要的操作，通过回调的方式传递到{@link ChatActivity#setItemClickListener()}中去实现
     * TODO 现在这种实现并不是最优，因为在每一个 Item 中都要去实现弹出一个 Dialog，但是又不想自定义dialog
     */
    @Override protected void onItemLongClick() {
        String[] menus = null;
        // 这里要根据消息的类型去判断要弹出的菜单，是否是发送方，并且是发送成功才能撤回
        if (viewType == AConstants.MSG_TYPE_IMAGE_RECEIVED) {
            menus = new String[] {
                    activity.getResources().getString(R.string.menu_chat_forward),
                    activity.getResources().getString(R.string.menu_chat_delete)
            };
        } else {
            menus = new String[] {
                    activity.getResources().getString(R.string.menu_chat_forward),
                    activity.getResources().getString(R.string.menu_chat_delete),
                    activity.getResources().getString(R.string.menu_chat_recall)
            };
        }

        // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
        alertDialogBuilder = new AlertDialog.Builder(activity);
        // 弹出框标题
        // alertDialogBuilder.setTitle(R.string.dialog_title_conversation);
        alertDialogBuilder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        adapter.onItemAction(AConstants.ACTION_FORWARD, message);
                        break;
                    case 1:
                        adapter.onItemAction(AConstants.ACTION_DELETE, message);
                        break;
                    case 2:
                        adapter.onItemAction(AConstants.ACTION_RECALL, message);
                        break;
                }
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 设置缩略图的显示，并将缩略图添加到缓存
     *
     * @param thumbnailsPath 缩略图的路径
     * @param originalPath 原始图片的路径
     */
    private void showThumbnailsImage(String thumbnailsPath, String originalPath) {
        File thumbnailsFile = new File(thumbnailsPath);
        File originalFile = new File(originalPath);
        // 根据图片存在情况加载缩略图显示
        if (originalFile.exists()) {
            // 原图存在，直接通过原图加载缩略图
            Glide.with(context).load(originalFile).crossFade().override(mViewWidth, mViewHeight).into(imageView);
        } else if (!originalFile.exists() && thumbnailsFile.exists()) {
            // 原图不存在，只存在缩略图
            Glide.with(context).load(thumbnailsFile).crossFade().override(mViewWidth, mViewHeight).into(imageView);
        } else if (!originalFile.exists() && !thumbnailsFile.exists()) {
            // 原图和缩略图都不存在
            Glide.with(context).load(thumbnailsFile).crossFade().override(mViewWidth, mViewHeight).into(imageView);
        }
    }

    /**
     * 刷新当前item
     */
    protected void refreshView() {
        // 判断是不是阅后即焚的消息
        if (message.getBooleanAttribute(AConstants.ATTR_BURN, false)) {
        } else {
        }
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (message.status()) {
            case SUCCESS:
                ackStatusView.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                resendView.setVisibility(View.GONE);
                break;
            // 当消息在发送过程中被Kill，消息的状态会变成Create，而且永远不会发送成功，所以这里把CREATE状态莪要设置为失败
            case FAIL:
            case CREATE:
                ackStatusView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                resendView.setVisibility(View.VISIBLE);
                resendView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        adapter.onItemAction(AConstants.ACTION_RESEND, message);
                    }
                });
                break;
            case INPROGRESS:
                ackStatusView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                resendView.setVisibility(View.GONE);
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
    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(MessageEvent event) {
        EMMessage message = event.getMessage();
        if (!message.getMsgId().equals(this.message.getMsgId())) {
            return;
        }
        if (message.getType() == EMMessage.Type.IMAGE && event.getStatus() == EMMessage.Status.INPROGRESS) {
            // 设置消息进度百分比
            percentView.setText(String.valueOf(event.getProgress()));
        }
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    @Override protected void onInflateView() {
        if (viewType == AConstants.MSG_TYPE_IMAGE_SEND) {
            inflater.inflate(R.layout.item_msg_image_send, this);
        } else {
            inflater.inflate(R.layout.item_msg_image_received, this);
        }

        // 通过 findViewById 实例化控件
        bubbleLayout = findViewById(R.id.layout_bubble);
        avatarView = (VMImageView) findViewById(R.id.img_avatar);
        imageView = (VMImageView) findViewById(R.id.img_image);
        usernameView = (TextView) findViewById(R.id.text_username);
        msgTimeView = (TextView) findViewById(R.id.text_time);
        resendView = (ImageView) findViewById(R.id.img_resend);
        progressLayout = findViewById(R.id.layout_progress);
        msgProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        percentView = (TextView) findViewById(R.id.text_progress_percent);
        ackStatusView = (ImageView) findViewById(R.id.img_msg_ack);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
