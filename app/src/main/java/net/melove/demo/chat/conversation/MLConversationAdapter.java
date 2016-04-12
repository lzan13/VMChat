package net.melove.demo.chat.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.communal.util.MLDate;
import net.melove.demo.chat.communal.util.MLLog;
import net.melove.demo.chat.communal.widget.MLImageView;

import java.util.List;

/**
 * Created by lz on 2015/12/13.
 * 会话列表的适配器，供{@link MLConversationsFragment}使用
 */
public class MLConversationAdapter extends RecyclerView.Adapter<MLConversationAdapter.ConversationViewHolder> {

    // 当前 Adapter 的上下文对象
    private Context mContext;
    private LayoutInflater mInflater;
    // 会话列表的数据源
    private List<EMConversation> mConversations;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;

    // 刷新会话列表
    private final int HANDLER_CONVERSATION_REFRESH = 0;
    private MLHandler mHandler;

    /**
     * 构造方法，需传递过来上下文对象和数据源
     *
     * @param context       上下文对象
     * @param conversations 需要展示的会话列表集合
     */
    public MLConversationAdapter(Context context, List<EMConversation> conversations) {
        mContext = context;
        mConversations = conversations;
        mInflater = LayoutInflater.from(mContext);
        mHandler = new MLHandler();
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_conversation, parent, false);
        ConversationViewHolder viewHolder = new ConversationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, final int position) {
        MLLog.d("MLConversationAdapter onBindViewHolder - %d", position);
        EMConversation conversation = mConversations.get(position);
        /**
         * 设置当前会话的最后时间 获取当前会话最后时间，并转为String类型
         * 之前是获取最后一条消息的时间 conversation.getLastMessage().getMsgTime();
         * 这里改为通过给 EMConversation 对象添加了一个时间扩展，这样可以避免在会话没有消息时，无法显示时间的问题
         * 调用{@link MLConversationExtUtils#getConversationLastTime(EMConversation)}获取扩展里的时间
         */
        String time = MLDate.long2Time(MLConversationExtUtils.getConversationLastTime(conversation));
        holder.timeView.setText(time);

        String content = "";
        // 判断当前会话在本地是否有聊天记录，并根据结果获取最后一条消息的内容
        if (conversation.getAllMessages().size() > 0) {
            switch (conversation.getLastMessage().getType()) {
                case TXT:
                    content = ((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage();
                    break;
                case FILE:
                    content = "[" + mContext.getString(R.string.ml_file) + "]";
                    break;
                case IMAGE:
                    content = "[" + mContext.getString(R.string.ml_photo) + "]";
                    break;
                case LOCATION:
                    content = "[" + mContext.getString(R.string.ml_location) + "]";
                    break;
                case VIDEO:
                    content = "[" + mContext.getString(R.string.ml_video) + "]";
                    break;
                case VOICE:
                    content = "[" + mContext.getString(R.string.ml_voice) + "]";
                    break;
                default:
                    break;
            }
        } else {
            // 当前会话没有聊天信息则设置显示内容为 空
            content = mContext.getString(R.string.ml_hint_empty);
        }
        holder.contentView.setText(content);
        // 设置当前会话联系人名称
        holder.usernameView.setText(conversation.getUserName());


        // 设置当前会话未读数
        int unreadCount = conversation.getUnreadMsgCount();
        if (unreadCount == 0) {
            holder.countView.setVisibility(View.GONE);
        } else if (unreadCount >= 100) {
            holder.countView.setVisibility(View.VISIBLE);
            holder.countView.setText("99+");
        } else {
            holder.countView.setVisibility(View.VISIBLE);
            holder.countView.setText(String.valueOf(unreadCount));
        }
        /**
         * 判断当前会话是否置顶
         * 调用工具类{@link MLConversationExtUtils#setConversationTop(EMConversation, boolean)}进行设置
         */
        if (MLConversationExtUtils.getConversationTop(conversation)) {
            holder.pushpinView.setVisibility(View.VISIBLE);
        } else {
            holder.pushpinView.setVisibility(View.GONE);
        }

        // 为每个Item设置点击与长按监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onItemLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    /**
     * 供界面调用的刷新 Adapter 的方法
     */
    public void refreshList() {
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_CONVERSATION_REFRESH;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        public void onItemClick(int position);

        public void onItemLongClick(int position);
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
     * 自定义Handler，用来处理消息的刷新等
     */
    protected class MLHandler extends Handler {
        private void refresh() {
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(Message msg) {
            //            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_CONVERSATION_REFRESH:
                    refresh();
                    break;
            }
        }
    }

    /**
     * 自定义会话列表项的 ViewHolder 用来显示会话列表项的内容
     */
    protected static class ConversationViewHolder extends RecyclerView.ViewHolder {
        public MLImageView avatarView;
        public TextView usernameView;
        public TextView contentView;
        public TextView timeView;
        public ImageView pushpinView;
        public TextView countView;

        /**
         * 构造方法，初始化列表项的控件
         *
         * @param itemView item项的父控件
         */
        public ConversationViewHolder(View itemView) {
            super(itemView);
            avatarView = (MLImageView) itemView.findViewById(R.id.ml_img_conversation_avatar);
            usernameView = (TextView) itemView.findViewById(R.id.ml_text_conversation_username);
            contentView = (TextView) itemView.findViewById(R.id.ml_text_conversation_content);
            timeView = (TextView) itemView.findViewById(R.id.ml_text_conversation_time);
            pushpinView = (ImageView) itemView.findViewById(R.id.ml_img_conversation_pushpin);
            countView = (TextView) itemView.findViewById(R.id.ml_text_conversation_count);
        }
    }


}
