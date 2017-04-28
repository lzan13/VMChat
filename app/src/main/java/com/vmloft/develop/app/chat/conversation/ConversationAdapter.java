package com.vmloft.develop.app.chat.conversation;

import android.content.Context;
import android.graphics.Typeface;
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

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;
import com.vmloft.develop.library.tools.utils.VMDateUtil;
import com.vmloft.develop.library.tools.utils.VMLog;

import com.vmloft.develop.library.tools.widget.VMImageView;
import java.util.List;

/**
 * Created by lz on 2015/12/13.
 * 会话列表的适配器，供{@link ConversationsFragment}使用
 */
public class ConversationAdapter
        extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    // 当前 Adapter 的上下文对象
    private Context context;
    private LayoutInflater inflater;
    // 会话列表的数据源
    private List<EMConversation> conversations;

    // 自定义的回调接口
    private ItemCallBack callback;

    // 刷新会话列表
    private final int HANDLER_CONVERSATION_REFRESH = 0;
    private MLHandler handler;

    /**
     * 构造方法，需传递过来上下文对象和数据源
     *
     * @param context 上下文对象
     * @param conversations 需要展示的会话列表集合
     */
    public ConversationAdapter(Context context, List<EMConversation> conversations) {
        this.context = context;
        this.conversations = conversations;
        inflater = LayoutInflater.from(this.context);
        handler = new MLHandler();
    }

    @Override public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_conversation, parent, false);
        ConversationViewHolder viewHolder = new ConversationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ConversationViewHolder holder, final int position) {
        VMLog.d("ConversationAdapter onBindViewHolder - %d", position);
        EMConversation conversation = conversations.get(position);
        /**
         * 设置当前会话的最后时间 获取当前会话最后时间，并转为String类型
         * 之前是获取最后一条消息的时间 conversation.getLastMessage().getMsgTime();
         * 这里改为通过给 EMConversation 对象添加了一个时间扩展，这样可以避免在会话没有消息时，无法显示时间的问题
         * 调用{@link ConversationExtUtils#getConversationLastTime(EMConversation)}获取扩展里的时间
         */
        long timestamp = ConversationExtUtils.getConversationLastTime(conversation);

        // 设置时间
        holder.timeView.setText(VMDateUtil.getRelativeTime(timestamp));

        /**
         * 根据当前 conversation 判断会话列表项要显示的内容
         * 判断的项目有两项：
         *  当前会话在本地是否有聊天记录，
         *  当前会话是否有草稿，
         */
        String content = "";
        String msgPrefix = "";
        String draft = ConversationExtUtils.getConversationDraft(conversation);
        if (!TextUtils.isEmpty(draft)) {
            // 表示草稿的前缀
            msgPrefix = "[" + context.getString(R.string.hint_msg_draft) + "]";
            content = msgPrefix + draft;
        } else if (conversation.getAllMessages().size() > 0) {
            EMMessage lastMessage = conversation.getLastMessage();
            // 首先判断消息是否已经撤回，撤回就不能显示消息内容
            if (lastMessage.getBooleanAttribute(Constants.ATTR_RECALL, false)) {
                content = context.getString(R.string.hint_msg_recall_by_self);
            } else if (lastMessage.getBooleanAttribute(Constants.ATTR_CALL_VIDEO, false)) {
                content = "[" + context.getString(R.string.video_call) + "]";
                ;
            } else if (lastMessage.getBooleanAttribute(Constants.ATTR_CALL_VOICE, false)) {
                content = "[" + context.getString(R.string.voice_call) + "]";
                ;
            } else {
                switch (lastMessage.getType()) {
                    case TXT:
                        content = ((EMTextMessageBody) conversation.getLastMessage()
                                .getBody()).getMessage();
                        break;
                    case FILE:
                        content = "[" + context.getString(R.string.file) + "]";
                        break;
                    case IMAGE:
                        content = "[" + context.getString(R.string.photo) + "]";
                        break;
                    case LOCATION:
                        content = "[" + context.getString(R.string.location) + "]";
                        break;
                    case VIDEO:
                        content = "[" + context.getString(R.string.video) + "]";
                        break;
                    case VOICE:
                        content = "[" + context.getString(R.string.voice) + "]";
                        break;
                    default:
                        break;
                }
                // 判断这条消息状态，如果失败加上失败前缀提示
                if (conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
                    msgPrefix = "[" + context.getString(R.string.hint_msg_failed) + "]";
                    content = msgPrefix + content;
                }
            }
        } else {
            // 当前会话没有聊天信息则设置显示内容为 空
            content = context.getString(R.string.hint_empty);
        }
        // 根据不同的类型展示不同样式的消息
        if (!TextUtils.isEmpty(draft)) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.vm_red_87)), 0,
                    msgPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contentView.setText(spannable);
        } else if (conversation.getAllMsgCount() > 0
                && conversation.getLastMessage().status() == EMMessage.Status.FAIL) {
            Spannable spannable = new SpannableString(content);
            spannable.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(context, R.color.vm_red_87)), 0,
                    msgPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contentView.setText(spannable);
        } else {
            holder.contentView.setText(content);
        }
        // 设置当前会话联系人名称
        if (conversation.getType() == EMConversation.EMConversationType.Chat) {
            // 这里有一些特殊的会话，因为是使用会话保存的申请与通知，处理下会话的标题
            if (conversation.conversationId().equals(Constants.CONVERSATION_ID_APPLY)) {
                holder.titleView.setText(R.string.apply);
            } else {
                holder.titleView.setText(conversation.conversationId());
            }
        } else if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
            // 如果是群聊设置群组名称
            EMGroup group =
                    EMClient.getInstance().groupManager().getGroup(conversation.conversationId());
            if (group != null) {
                holder.titleView.setText(group.getGroupName());
            } else {
                holder.titleView.setText(conversation.conversationId());
            }
        } else if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
            // 如果是聊天室设置聊天室名称
            EMChatRoom chatRoom = EMClient.getInstance()
                    .chatroomManager()
                    .getChatRoom(conversation.conversationId());
            if (chatRoom != null) {
                holder.titleView.setText(chatRoom.getName());
            } else {
                holder.titleView.setText(conversation.conversationId());
            }
        }

        // 设置当前会话未读数
        int unreadCount = conversation.getUnreadMsgCount();
        VMLog.i("conversation unread count %d", unreadCount);
        if (unreadCount == 0) {
            if (ConversationExtUtils.getConversationUnread(conversation)) {
                holder.titleView.setTypeface(Typeface.DEFAULT_BOLD);
                holder.contentView.setTypeface(Typeface.DEFAULT_BOLD);
                holder.contentView.setTextColor(
                        ContextCompat.getColor(context, R.color.vm_black_87));
            } else {
                holder.titleView.setTypeface(Typeface.DEFAULT);
                holder.contentView.setTypeface(Typeface.DEFAULT);
                holder.contentView.setTextColor(
                        ContextCompat.getColor(context, R.color.vm_black_54));
            }
        } else {
            holder.titleView.setTypeface(Typeface.DEFAULT_BOLD);
            holder.contentView.setTypeface(Typeface.DEFAULT_BOLD);
            holder.contentView.setTextColor(ContextCompat.getColor(context, R.color.vm_black_87));
        }
        /**
         * 判断当前会话是否置顶
         * 调用工具类{@link ConversationExtUtils#setConversationPushpin(EMConversation, boolean)}进行设置
         */
        if (ConversationExtUtils.getConversationPushpin(conversation)) {
            holder.pushpinView.setVisibility(View.VISIBLE);
        } else {
            holder.pushpinView.setVisibility(View.GONE);
        }

        holder.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                callback.onAction(holder.avatarView.getId(), position);
            }
        });
        // 为每个Item设置点击与长按监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                callback.onAction(Constants.ACTION_CLICK, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                callback.onAction(Constants.ACTION_LONG_CLICK, position);
                return true;
            }
        });
    }

    @Override public int getItemCount() {
        return conversations.size();
    }

    /**
     * 供界面调用的刷新 Adapter 的方法
     */
    public void refreshList() {
        Message msg = handler.obtainMessage();
        msg.what = HANDLER_CONVERSATION_REFRESH;
        handler.sendMessage(msg);
    }

    /**
     * 设置回调监听
     *
     * @param callback 自定义回调接口
     */
    public void setItemCallback(ItemCallBack callback) {
        this.callback = callback;
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
        public VMImageView avatarView;
        public TextView titleView;
        public TextView contentView;
        public TextView timeView;
        public ImageView pushpinView;

        /**
         * 构造方法，初始化列表项的控件
         *
         * @param itemView item项的父控件
         */
        public ConversationViewHolder(View itemView) {
            super(itemView);
            avatarView = (VMImageView) itemView.findViewById(R.id.img_avatar);
            titleView = (TextView) itemView.findViewById(R.id.text_name);
            contentView = (TextView) itemView.findViewById(R.id.text_content);
            timeView = (TextView) itemView.findViewById(R.id.text_time);
            pushpinView = (ImageView) itemView.findViewById(R.id.img_pushpin);
        }
    }
}
