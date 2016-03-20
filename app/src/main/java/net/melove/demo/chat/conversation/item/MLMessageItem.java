package net.melove.demo.chat.conversation.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.common.widget.MLImageView;

/**
 * Created by lz on 2016/3/20.
 */
public abstract class MLMessageItem extends LinearLayout {

    // 上下文对象
    protected Context context;
    // 布局内容填充者，将xml布局文件解析为view
    protected LayoutInflater inflater;
    // item 类型
    protected int viewType;

    protected MLImageView avatarView;
    protected MLImageView imageView;
    protected TextView usernameView;
    protected TextView contentView;
    protected TextView timeView;
    protected ImageView msgState;
    protected ProgressBar progressBar;

    public MLMessageItem(Context context, int viewType) {
        super(context);
        this.context = context;
        this.viewType = viewType;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 处理数据显示
     *
     * @param message 需要展示的 EMMessage 对象
     */
    public abstract void onSetupView(EMMessage message);

    /**
     * 抽象方法，填充当前 Item，子类必须实现
     */
    protected abstract void onInflateView();
}
