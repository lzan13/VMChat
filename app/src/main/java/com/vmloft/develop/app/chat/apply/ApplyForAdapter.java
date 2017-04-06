package com.vmloft.develop.app.chat.apply;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;

import com.vmloft.develop.library.tools.widget.VMImageView;
import java.util.Collections;
import java.util.List;

/**
 * Created by lzan13 on 2016/3/17.
 * 申请信息适配器类
 */
public class ApplyForAdapter extends RecyclerView.Adapter<ApplyForAdapter.ApplyViewHolder> {

    // 上下文对象
    private Context mContext;
    private ItemCallBack mCallback;
    private LayoutInflater mInflater;

    // 当前会话对象
    private EMConversation mConversation;
    private List<EMMessage> mMessages;

    public ApplyForAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        /**
         * 初始化会话对象，这里有三个参数么，
         * mChatid 第一个表示会话的当前聊天的 useranme 或者 groupid
         * null 第二个是会话类型可以为空
         * true 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance()
                .chatManager()
                .getConversation(Constants.CONVERSATION_ID_APPLY, null, true);
        mMessages = mConversation.getAllMessages();
        // 将list集合倒序排列
        Collections.reverse(mMessages);
    }

    @Override public ApplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_apply, parent, false);
        return new ApplyViewHolder(view);
    }

    @Override public void onBindViewHolder(ApplyViewHolder holder, final int position) {
        VMLog.i("ApplyForAdapter - onBindViewHolder - %d", position);
        final EMMessage message = mMessages.get(position);

        holder.avatarView.setImageResource(R.drawable.ic_character_blackcat);

        String username = message.getStringAttribute(Constants.ATTR_USERNAME, "");
        // 设置申请的人
        holder.usernameView.setText(username);
        // 设置申请理由
        String reason = message.getStringAttribute(Constants.ATTR_REASON, "");
        holder.reasonView.setText(reason);

        String status = message.getStringAttribute(Constants.ATTR_STATUS, "");
        if (!TextUtils.isEmpty(status)) {
            holder.btnAgree.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.statusView.setVisibility(View.GONE);
        } else {
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.statusView.setVisibility(View.VISIBLE);
            holder.statusView.setText(status);
        }

        // 设置 itemView Button 的点击监听
        holder.btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mCallback.onAction(Constants.ACTION_AGREED, message.getMsgId());
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mCallback.onAction(Constants.ACTION_REJECT, message.getMsgId());
            }
        });
        // 给当前 itemView 设置点击和长按监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCallback.onAction(Constants.ACTION_CLICK, message.getMsgId());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                mCallback.onAction(Constants.ACTION_LONG_CLICK, message.getMsgId());
                return true;
            }
        });
    }

    @Override public int getItemCount() {
        return mMessages.size();
    }

    /**
     * 设置 Item 回调
     *
     * @param callback 自定义实现的回调接口
     */
    public void setItemCallBack(ItemCallBack callback) {
        mCallback = callback;
    }

    /**
     * 自定义ViewHolder
     */
    protected static class ApplyViewHolder extends RecyclerView.ViewHolder {
        VMImageView avatarView;
        TextView usernameView;
        TextView reasonView;
        TextView statusView;
        Button btnAgree;
        Button btnReject;

        /**
         * 构造方法，初始化列表项的各个控件
         *
         * @param itemView item项的父控件
         */
        public ApplyViewHolder(View itemView) {
            super(itemView);
            avatarView = (VMImageView) itemView.findViewById(R.id.img_avatar);
            usernameView = (TextView) itemView.findViewById(R.id.text_username);
            reasonView = (TextView) itemView.findViewById(R.id.text_reason);
            statusView = (TextView) itemView.findViewById(R.id.text_status);
            btnAgree = (Button) itemView.findViewById(R.id.btn_agree);
            btnReject = (Button) itemView.findViewById(R.id.btn_reject);
        }
    }
}
