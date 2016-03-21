package net.melove.demo.chat.conversation.messageitem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.common.widget.MLImageView;
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
    // item 类型
    protected int mViewType;

    // 当前 Item 需要处理的 EMMessage 对象
    protected EMMessage mMessage;
    // 显示聊天头像的 ImageView 控件
    protected MLImageView mAvatarView;
    protected MLImageView mImageView;
    protected TextView mUsernameView;
    protected TextView mContentView;
    protected TextView mTimeView;
    protected ImageView mMessageState;
    protected ProgressBar mProgressBar;

    public MLMessageItem(Context context, MLMessageAdapter adapter, int viewType) {
        super(context);
        mContext = context;
        mActivity = (Activity) context;
        mInflater = LayoutInflater.from(context);
        mAdapter = adapter;
        mViewType = viewType;
    }

    /**
     * 处理数据显示
     *
     * @param message 需要展示的 EMMessage 对象
     */
    public abstract void onSetupView(EMMessage message);

    /**
     * 抽象方法，填充当前 Item，子类必须实现
     * 解析对应的xml 布局，填充当前 ItemView，并初始化控件
     */
    protected abstract void onInflateView();

}
