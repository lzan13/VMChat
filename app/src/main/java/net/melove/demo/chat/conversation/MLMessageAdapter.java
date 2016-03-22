package net.melove.demo.chat.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.conversation.messageitem.MLImageMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLRecallMessageItem;
import net.melove.demo.chat.conversation.messageitem.MLTextMessageItem;

import java.util.List;


/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2016/1/6 18:51.
 */
public class MLMessageAdapter extends RecyclerView.Adapter<MLMessageAdapter.MessageViewHolder> {

    // 刷新类型
    private final int HANDLER_MSG_REFRESH = 0;
    private final int HANDLER_MSG_REFRESH_MORE = 1;

    private Context mContext;

    private LayoutInflater mInflater;

    private String mChatId;
    private EMConversation mConversation;
    private List<EMMessage> mMessages;
    private RecyclerView mRecyclerView;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;
    private MLHandler mHandler;

    /**
     * 构造方法
     *
     * @param context
     * @param chatId
     * @param recyclerView
     */
    public MLMessageAdapter(Context context, String chatId, RecyclerView recyclerView) {
        mContext = context;
        mChatId = chatId;
        mInflater = LayoutInflater.from(mContext);
        mRecyclerView = recyclerView;

        mHandler = new MLHandler();

        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatId, null, true);
        mMessages = mConversation.getAllMessages();

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
        switch (message.getType()) {
        case TXT:
            // 判断是否为撤回类型的消息
            if (message.getBooleanAttribute(MLConstants.ML_ATTR_RECALL, false)) {
                itemType = MLConstants.MSG_TYPE_SYS_RECALL;
            } else {
                itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_TXT_SEND : MLConstants.MSG_TYPE_TXT_RECEIVED;
            }
            break;
        case IMAGE:
            itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_IMAGE_SEND : MLConstants.MSG_TYPE_IMAGE_RECEIVED;
            break;
        default:
            // 默认返回txt类型
            itemType = message.direct() == EMMessage.Direct.SEND ? MLConstants.MSG_TYPE_TXT_SEND : MLConstants.MSG_TYPE_TXT_RECEIVED;
            break;
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
        case MLConstants.MSG_TYPE_TXT_SEND:
        case MLConstants.MSG_TYPE_TXT_RECEIVED:
            holder = new MessageViewHolder(new MLTextMessageItem(mContext, this, viewType));
            break;
        // 图片类消息
        case MLConstants.MSG_TYPE_IMAGE_SEND:
        case MLConstants.MSG_TYPE_IMAGE_RECEIVED:
            holder = new MessageViewHolder(new MLImageMessageItem(mContext, this, viewType));
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
        ((MLMessageItem) holder.itemView).onSetupView(message, position);

    }

    /**
     * 重发消息方法
     */
    public void resendMessage(String msgId) {
        EMMessage failedMessage = mConversation.getMessage(msgId, true);
        final EMMessage message = EMMessage.createTxtSendMessage(((EMTextMessageBody) failedMessage.getBody()).getMessage(), failedMessage.getTo());
        EMClient.getInstance().chatManager().sendMessage(message);
        mConversation.removeMessage(msgId);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                refreshList();
            }

            @Override
            public void onError(int i, String s) {
                refreshList(mConversation.getMessagePosition(message));
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        // 点击回调
        public void onItemClick(View view, int position);

        // 长按回调
        public void onItemLongClick(View view, int position);
    }

    /**
     * 设置回调监听
     *
     * @param listener 自定义回调接口
     */
    public void setOnItemClickListener(MLOnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 为每个Item设置点击与长按监听
     *
     * @param view     需要设置监听的view
     * @param position 需要设置监听的position
     */
    public void setOnItemClick(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onItemLongClick(v, position);
                return false;
            }
        });
    }

    /**
     * 供界面调用的刷新 Adapter 的方法
     */
    public void refreshList() {
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_MSG_REFRESH;
        mHandler.sendMessage(msg);
    }

    /**
     * 刷新列表，并滚动到指定位置
     *
     * @param position 要滚动到的位置
     */
    public void refreshList(int position) {
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_MSG_REFRESH_MORE;
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    private class MLHandler extends Handler {

        /**
         * 刷新聊天信息列表，并滚动到底部
         */
        private void refresh() {
            mMessages.clear();
            mMessages.addAll(mConversation.getAllMessages());
            notifyDataSetChanged();
            if (mMessages.size() > 1) {
                // 滚动到最后一条
                mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
            }
        }

        /**
         * 刷新界面并平滑滚动到新加载的记录位置
         *
         * @param position 新加载的内容的最后一个位置
         */
        private void refresh(final int position) {
            mMessages.clear();
            mMessages.addAll(mConversation.getAllMessages());
            notifyDataSetChanged();
            // 滚动到相应的位置
            mRecyclerView.scrollToPosition(position - 1);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLER_MSG_REFRESH:
                refresh();
                break;
            case HANDLER_MSG_REFRESH_MORE:
                refresh(msg.arg1);
                break;
            }
        }
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
