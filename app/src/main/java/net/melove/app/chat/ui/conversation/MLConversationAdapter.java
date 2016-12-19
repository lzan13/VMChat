package net.melove.app.chat.ui.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.app.chat.R;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.module.listener.MLItemCallBack;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.ui.widget.MLImageView;

import java.util.List;

/**
 * Created by lz on 2015/12/13.
 * 会话列表的适配器，供{@link MLConversationsFragment}使用
 */
public class MLConversationAdapter
        extends RecyclerView.Adapter<MLConversationAdapter.ConversationViewHolder> {

    // 当前 Adapter 的上下文对象
    private Context mContext;
    private LayoutInflater mInflater;
    // 会话列表的数据源
    private List<EMConversation> mConversations;

    // 自定义的回调接口
    private MLItemCallBack mCallback;

    // 刷新会话列表
    private final int HANDLER_CONVERSATION_REFRESH = 0;
    private MLHandler mHandler;

    /**
     * 构造方法，需传递过来上下文对象和数据源
     *
     * @param context 上下文对象
     * @param conversations 需要展示的会话列表集合
     */
    public MLConversationAdapter(Context context, List<EMConversation> conversations) {
        mContext = context;
        mConversations = conversations;
        mInflater = LayoutInflater.from(mContext);
        mHandler = new MLHandler();
    }

    @Override public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_conversation, parent, false);
        ConversationViewHolder viewHolder = new ConversationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ConversationViewHolder holder, final int position) {
        MLLog.d("MLConversationAdapter onBindViewHolder - %d", position);
        EMConversation conversation = mConversations.get(position);
        /**
         * 设置当前会话的最后时间 获取当前会话最后时间，并转为String类型
         * 之前是获取最后一条消息的时间 conversation.getLastMessage().getMsgTime();
         * 这里改为通过给 EMConversation 对象添加了一个时间扩展，这样可以避免在会话没有消息时，无法显示时间的问题
         * 调用{@link MLConversationExtUtils#getConversationLastTime(EMConversation)}获取扩展里的时间
         */
        long timestamp = MLConversationExtUtils.getConversationLastTime(conversation);

        // 设置时间
        holder.timeView.setText(MLDateUtil.getRelativeTime(timestamp));

        /**
         * 根据当前 conversation 判断会话列表项要显示的内容
         * 判断的项目有两项：
         *  当前会话在本地是否有聊天记录，
         *  当前会话是否有草稿，
         */
        String content = "";
        String msgPrefix = "";
        String draft = MLConversationExtUtils.getConversationDraft(conversation);
        if (!TextUtils.isEmpty(draft)) {
            // 表示草稿的前缀
            msgPrefix = "[" + mContext.getString(R.string.ml_hint_msg_draft) + "]";
            content = msgPrefix + draft;
        } else if (conversation.getAllMessages().size() > 0) {
            EMMessage lastMessage = conversation.getLastMessage();
            // 首先判断消息是否已经撤回，撤回就不能显示消息内容
            if (lastMessage.getBooleanAttribute(MLConstants.ML_ATTR_RECALL, false)) {
                content = mContext.getString(R.string.ml_hint_msg_recall_by_self);
            } else if (lastMessage.getBooleanAttribute(MLConstants.ML_ATTR_CALL_VIDEO, false)) {
                content = "[" + mContext.getString(R.string.ml_video_call) + "]";
                ;
            } else if (lastMessage.getBooleanAttribute(MLConstants.ML_ATTR_CALL_VOICE, false)) {
                content = "[" + mContext.getString(R.string.ml_voice_call) + "]";
                ;
            } else {
                switch (lastMessage.getType()) {
                    case TXT:
                        content = ((EMTextMessageBody) conversation.getLastMessage()
                                .getBody()).getMessage();
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
                // 判断这条消息状态，如果失败加上失败前缀提示
                if (conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
                    msgPrefix = "[" + mContext.getString(R.string.ml_hint_msg_failed) + "]";
                    content = msgPrefix + content;
                }
            }
        } else {
            // 当前会话没有聊天信息则设置显示内容为 空
            content = mContext.getString(R.string.ml_hint_empty);
        }
        // 根据不同的类型展示不同样式的消息
        if (!TextUtils.isEmpty(draft)) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.ml_red_87)), 0,
                    msgPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contentView.setText(spannable);
        } else if (conversation.getAllMsgCount() > 0
                && conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.ml_red_87)), 0,
                    msgPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contentView.setText(spannable);
        } else {
            holder.contentView.setText(content);
        }
        // 设置当前会话联系人名称
        if (conversation.getType() == EMConversation.EMConversationType.Chat) {
            // 这里有一些特殊的会话，因为是使用会话保存的申请与通知，处理下会话的标题
            if (conversation.getUserName().equals(MLConstants.ML_CONVERSATION_ID_APPLY)) {
                holder.titleView.setText(R.string.ml_apply);
            } else {
                holder.titleView.setText(conversation.getUserName());
            }
        } else if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
            // 如果是群聊设置群组名称
            EMGroup group =
                    EMClient.getInstance().groupManager().getGroup(conversation.getUserName());
            if (group != null) {
                holder.titleView.setText(group.getGroupName());
            } else {
                holder.titleView.setText(conversation.getUserName());
            }
        }

        // 设置当前会话未读数
        int unreadCount = conversation.getUnreadMsgCount();
        MLLog.i("conversation unread count %d", unreadCount);
        if (unreadCount == 0) {
            if (MLConversationExtUtils.getConversationUnread(conversation)) {
                holder.countView.setVisibility(View.VISIBLE);
                holder.countView.setText("1");
            } else {
                holder.countView.setVisibility(View.GONE);
            }
        } else if (unreadCount >= 100) {
            holder.countView.setVisibility(View.VISIBLE);
            holder.countView.setText("99+");
        } else {
            holder.countView.setVisibility(View.VISIBLE);
            holder.countView.setText(String.valueOf(unreadCount));
        }
        /**
         * 判断当前会话是否置顶
         * 调用工具类{@link MLConversationExtUtils#setConversationPushpin(EMConversation, boolean)}进行设置
         */
        if (MLConversationExtUtils.getConversationPushpin(conversation)) {
            holder.pushpinView.setVisibility(View.VISIBLE);
        } else {
            holder.pushpinView.setVisibility(View.GONE);
        }

        holder.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mCallback.onAction(holder.avatarView.getId(), position);
            }
        });
        // 为每个Item设置点击与长按监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCallback.onAction(MLConstants.ML_ACTION_CLICK, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                mCallback.onAction(MLConstants.ML_ACTION_LONG_CLICK, position);
                return true;
            }
        });
    }

    @Override public int getItemCount() {
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
     * 设置回调监听
     *
     * @param callback 自定义回调接口
     */
    public void setItemCallback(MLItemCallBack callback) {
        mCallback = callback;
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    protected class MLHandler extends Handler {
        private void refresh() {
            notifyDataSetChanged();
        }

        @Override public void handleMessage(Message msg) {
            //super.handleMessage(msg);
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
        public TextView titleView;
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
            avatarView = (MLImageView) itemView.findViewById(R.id.img_avatar);
            titleView = (TextView) itemView.findViewById(R.id.text_name);
            contentView = (TextView) itemView.findViewById(R.id.text_content);
            timeView = (TextView) itemView.findViewById(R.id.text_time);
            pushpinView = (ImageView) itemView.findViewById(R.id.img_pushpin);
            countView = (TextView) itemView.findViewById(R.id.text_unread_count);
        }
    }
}
