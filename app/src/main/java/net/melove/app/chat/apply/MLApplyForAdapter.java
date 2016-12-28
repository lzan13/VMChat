package net.melove.app.chat.apply;

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

import net.melove.app.chat.R;
import net.melove.app.chat.app.MLConstants;
import net.melove.app.chat.app.MLItemCallBack;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.widget.MLImageView;

import java.util.Collections;
import java.util.List;

/**
 * Created by lzan13 on 2016/3/17.
 * 申请信息适配器类
 */
public class MLApplyForAdapter extends RecyclerView.Adapter<MLApplyForAdapter.ApplyViewHolder> {

    // 上下文对象
    private Context mContext;
    private MLItemCallBack mCallback;
    private LayoutInflater mInflater;

    // 当前会话对象
    private EMConversation mConversation;
    private List<EMMessage> mMessages;

    public MLApplyForAdapter(Context context) {
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
                .getConversation(MLConstants.ML_CONVERSATION_ID_APPLY, null, true);
        mMessages = mConversation.getAllMessages();
        // 将list集合倒序排列
        Collections.reverse(mMessages);
    }

    @Override public ApplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_apply, parent, false);
        return new ApplyViewHolder(view);
    }

    @Override public void onBindViewHolder(ApplyViewHolder holder, final int position) {
        MLLog.i("MLApplyForAdapter - onBindViewHolder - %d", position);
        final EMMessage message = mMessages.get(position);

        holder.imageViewAvatar.setImageResource(R.mipmap.ic_character_blackcat);

        String username = message.getStringAttribute(MLConstants.ML_ATTR_USERNAME, "");
        // 设置申请的人
        holder.textViewUsername.setText(username);
        // 设置申请理由
        String reason = message.getStringAttribute(MLConstants.ML_ATTR_REASON, "");
        holder.textViewReason.setText(reason);

        String status = message.getStringAttribute(MLConstants.ML_ATTR_STATUS, "");
        if (!TextUtils.isEmpty(status)) {
            holder.btnAgree.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.textViewStatus.setVisibility(View.GONE);
        } else {
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.textViewStatus.setText(status);
        }

        // 设置 itemView Button 的点击监听
        holder.btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mCallback.onAction(MLConstants.ML_ACTION_AGREED, message.getMsgId());
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mCallback.onAction(MLConstants.ML_ACTION_REJECT, message.getMsgId());
            }
        });
        // 给当前 itemView 设置点击和长按监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCallback.onAction(MLConstants.ML_ACTION_CLICK, message.getMsgId());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                mCallback.onAction(MLConstants.ML_ACTION_LONG_CLICK, message.getMsgId());
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
    public void setItemCallBack(MLItemCallBack callback) {
        mCallback = callback;
    }

    /**
     * 自定义ViewHolder
     */
    protected static class ApplyViewHolder extends RecyclerView.ViewHolder {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;
        Button btnReject;

        /**
         * 构造方法，初始化列表项的各个控件
         *
         * @param itemView item项的父控件
         */
        public ApplyViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = (MLImageView) itemView.findViewById(R.id.img_avatar);
            textViewUsername = (TextView) itemView.findViewById(R.id.text_username);
            textViewReason = (TextView) itemView.findViewById(R.id.text_reason);
            textViewStatus = (TextView) itemView.findViewById(R.id.text_status);
            btnAgree = (Button) itemView.findViewById(R.id.btn_agree);
            btnReject = (Button) itemView.findViewById(R.id.btn_reject);
        }
    }
}
