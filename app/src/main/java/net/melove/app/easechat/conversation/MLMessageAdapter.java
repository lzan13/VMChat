package net.melove.app.easechat.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import net.melove.app.easechat.application.MLConstants;
import net.melove.app.easechat.application.eventbus.MLRefreshEvent;
import net.melove.app.easechat.conversation.messageitem.MLFileMessageItem;
import net.melove.app.easechat.conversation.messageitem.MLImageMessageItem;
import net.melove.app.easechat.conversation.messageitem.MLMessageItem;
import net.melove.app.easechat.conversation.messageitem.MLRecallMessageItem;
import net.melove.app.easechat.conversation.messageitem.MLTextMessageItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;


/**
 * Class ${FILE_NAME}
 * <p>
 * Created by lzan13 on 2016/1/6 18:51.
 */
public class MLMessageAdapter extends RecyclerView.Adapter<MLMessageAdapter.MessageViewHolder> {

    // 刷新类型
    public static final int NOTIFY_ALL = 0x00;
    public static final int NOTIFY_CHANGED = 0x01;
    public static final int NOTIFY_MOVED = 0x02;
    public static final int NOTIFY_INSERTED = 0x03;
    public static final int NOTIFY_RANGE_CHANGED = 0x04;
    public static final int NOTIFY_RANGE_INSERTED = 0x05;
    public static final int NOTIFY_RANGE_REMOVED = 0x06;
    public static final int NOTIFY_REMOVED = 0x07;


    private Context mContext;
    // 当前 Item position
    private int mCurrPosition;

    // 聊天对象的 useranme/groupid
    private String mChatId;
    // 当前会话对象
    private EMConversation mConversation;
    private List<EMMessage> mMessages;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;

    /**
     * 构造方法
     *
     * @param context 上下文对象，在解析布局的时候需要用到
     * @param chatId  当前会话者的id，为username/groupId
     */
    public MLMessageAdapter(Context context, String chatId) {
        mContext = context;
        mChatId = chatId;

        /**
         * 初始化会话对象，这里有三个参数么，
         * mChatid 第一个表示会话的当前聊天的 useranme 或者 groupid
         * null 第二个是会话类型可以为空
         * true 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatId, null, true);
        mMessages = mConversation.getAllMessages();
        // 将list集合倒序排列
        // Collections.reverse(mMessages);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * 重写 Adapter 的获取当前 Item 类型的方法（必须重写，同上）
     *
     * @param position 当前 Item 位置
     * @return 当前 Item 的类型
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = mMessages.get(position);
        int itemType = -1;
        // 判断是否为撤回类型的消息
        if (message.getBooleanAttribute(MLConstants.ML_ATTR_RECALL, false)) {
            itemType = MLConstants.MSG_TYPE_SYS_RECALL;
        } else {
            switch (message.getType()) {
            case TXT:
                itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_TEXT_SEND : MLConstants.MSG_TYPE_TEXT_RECEIVED;
                break;
            case IMAGE:
                itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_IMAGE_SEND : MLConstants.MSG_TYPE_IMAGE_RECEIVED;
                break;
            case FILE:
                itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_FILE_SEND : MLConstants.MSG_TYPE_FILE_RECEIVED;
                break;
            default:
                // 默认返回txt类型
                itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_TEXT_SEND : MLConstants.MSG_TYPE_TEXT_RECEIVED;
                break;
            }
        }
        return itemType;
    }

    /**
     * 重写RecyclerView.Adapter 创建ViewHolder方法，这里根据消息类型不同创建不同的 itemView 类
     *
     * @param parent   itemView的父控件
     * @param viewType itemView要显示的消息类型
     * @return 返回 自定义的 MessageViewHolder
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageViewHolder holder = null;
        switch (viewType) {
        /**
         *  SDK默认类型的消息
         */
        // 文字类消息
        case MLConstants.MSG_TYPE_TEXT_SEND:
        case MLConstants.MSG_TYPE_TEXT_RECEIVED:
            holder = new MessageViewHolder(new MLTextMessageItem(mContext, this, viewType));
            break;
        // 图片类消息
        case MLConstants.MSG_TYPE_IMAGE_SEND:
        case MLConstants.MSG_TYPE_IMAGE_RECEIVED:
            holder = new MessageViewHolder(new MLImageMessageItem(mContext, this, viewType));
            break;
        // 正常的文件类消息
        case MLConstants.MSG_TYPE_FILE_SEND:
        case MLConstants.MSG_TYPE_FILE_RECEIVED:
            holder = new MessageViewHolder(new MLFileMessageItem(mContext, this, viewType));
            break;
        /**
         * 自定义类型的消息
         */
        // 回撤类消息
        case MLConstants.MSG_TYPE_SYS_RECALL:
            holder = new MessageViewHolder(new MLRecallMessageItem(mContext, this, viewType));
            break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // 获取当前要显示的 message 对象
        EMMessage message = mMessages.get(position);
        /**
         *  调用自定义{@link MLMessageItem#onSetupView(EMMessage)}来填充数据
         */
        ((MLMessageItem) holder.itemView).onSetupView(message);

    }

    /**
     * -------------------------------------------------------------------------------------
     * 为RecyclerView 定义点击和长按的事件回调
     */
    /**
     * 设置回调监听
     *
     * @param listener 自定义回调接口
     */
    public void setOnItemClickListener(MLOnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        /**
         * Item 点击及长按事件的处理
         * 这里Item的点击及长按监听都在自定义的
         * {@link net.melove.app.easechat.conversation.messageitem.MLMessageItem}里实现，
         * 然后通过回调将
         * {@link net.melove.app.easechat.conversation.messageitem.MLMessageItem}的操作以自定义 Action
         * 的方式传递过过来，因为聊天列表的 Item 有多种多样的，每一个 Item 弹出菜单不同，
         *
         * @param message 需要操作的 Item 的 EMMessage 对象
         * @param action  Item 触发的动作，比如 点击、复制、转发、删除、撤回等
         */
        public void onItemAction(EMMessage message, int action);
    }


    /**
     * Item 项的点击和长按 Action 事件回调
     *
     * @param message 操作的 Item 的 EMMessage 对象
     * @param action  需要处理的动作，比如 复制、转发、删除、撤回等
     */
    public void onItemAction(EMMessage message, int action) {
        mOnItemClickListener.onItemAction(message, action);
    }

    /**
     * 更新数据源
     */
    public void refreshMessageData() {
        mMessages.clear();
        mMessages.addAll(mConversation.getAllMessages());
    }

    /**
     * 非静态内部类会隐式持有外部类的引用，就像大家经常将自定义的adapter在Activity类里，
     * 然后在adapter类里面是可以随意调用外部activity的方法的。当你将内部类定义为static时，
     * 你就调用不了外部类的实例方法了，因为这时候静态内部类是不持有外部类的引用的。
     * 声明ViewHolder静态内部类，可以将ViewHolder和外部类解引用。
     * 大家会说一般ViewHolder都很简单，不定义为static也没事吧。
     * 确实如此，但是如果你将它定义为static的，说明你懂这些含义。
     * 万一有一天你在这个ViewHolder加入一些复杂逻辑，做了一些耗时工作，
     * 那么如果ViewHolder是非静态内部类的话，就很容易出现内存泄露。如果是静态的话，
     * 你就不能直接引用外部类，迫使你关注如何避免相互引用。 所以将 ViewHolder 定义为静态的
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
