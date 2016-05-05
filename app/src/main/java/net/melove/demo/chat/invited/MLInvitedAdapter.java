package net.melove.demo.chat.invited;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.communal.util.MLDate;
import net.melove.demo.chat.communal.util.MLLog;
import net.melove.demo.chat.communal.widget.MLImageView;
import net.melove.demo.chat.database.MLInvitedDao;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;

/**
 * Created by lzan13 on 2016/3/17.
 * 申请信息适配器类
 */
public class MLInvitedAdapter extends RecyclerView.Adapter<MLInvitedAdapter.InvitedViewHolder> {

    // 上下文对象
    private Context mContext;

    private LayoutInflater mInflater;

    private List<MLInvitedEntity> mInvitedEntities;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;

    public MLInvitedAdapter(Context context, List<MLInvitedEntity> invitedEntityList) {
        mContext = context;
        mInvitedEntities = invitedEntityList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public InvitedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_invited, parent, false);
        InvitedViewHolder holder = new InvitedViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(InvitedViewHolder holder, final int position) {
        MLLog.i("MLInvitedAdapter - onBindViewHolder - %d", position);
        MLInvitedEntity invitedEntity = mInvitedEntities.get(position);
        holder.imageViewAvatar.setImageResource(R.mipmap.ic_character_blackcat);

        String currUsername = EMClient.getInstance().getCurrentUser();
        // 设置申请的人
        if (currUsername.equals(invitedEntity.getUserName())) {
            holder.textViewUsername.setText(invitedEntity.getUserName());
        }
        holder.textViewUsername.setText(invitedEntity.getUserName());
        // 设置申请理由
        holder.textViewReason.setText(invitedEntity.getReason());

        // 判断当前的申请与通知的状态，显示不同的提醒文字
        if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.AGREED) {
            holder.textViewStatus.setText(R.string.ml_agreed);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.REFUSED) {
            holder.textViewStatus.setText(R.string.ml_refused);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAGREED) {
            holder.textViewStatus.setText(R.string.ml_be_agreed);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEREFUSED) {
            holder.textViewStatus.setText(R.string.ml_be_refused);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.APPLYFOR) {
            holder.textViewStatus.setText(R.string.ml_waiting_respond);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAPPLYFOR) {
            holder.textViewStatus.setText(R.string.ml_waiting_dispose);
            holder.textViewStatus.setVisibility(View.GONE);
            holder.btnAgree.setVisibility(View.VISIBLE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.GROUPAPPLYFOR) {
            holder.textViewStatus.setText(R.string.ml_waiting_dispose);
            holder.textViewStatus.setVisibility(View.GONE);
            holder.btnAgree.setVisibility(View.VISIBLE);
        }

        holder.btnAgree.setTag(position);
        holder.btnAgree.setOnClickListener(viewListener);
        /**
         * 给当前 ItemView 设置点击和长按监听
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置点击动作
                mOnItemClickListener.onItemAction(position, MLConstants.ML_ACTION_INVITED_CLICK);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 这里直接给长按设置删除操作
                mOnItemClickListener.onItemAction(position, MLConstants.ML_ACTION_INVITED_DELETE);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInvitedEntities.size();
    }

    /**
     * 申请与通知列表内Button点击事件
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            switch (v.getId()) {
            case R.id.ml_btn_invited_agree:
                mOnItemClickListener.onItemAction(position, MLConstants.ML_ACTION_INVITED_AGREE);
                break;
            }
        }
    };

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        /**
         * Item 点击及长按事件的处理
         * 这里Item的点击及长按监听都在当前的 MLInvitedAdapter 实现
         *
         * @param position 需要操作的Item的位置
         * @param action   长按菜单需要处理的动作，
         */
        public void onItemAction(int position, int action);
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
     * 自定义ViewHolder
     */
    protected static class InvitedViewHolder extends RecyclerView.ViewHolder {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;

        /**
         * 构造方法，初始化列表项的各个控件
         *
         * @param itemView item项的父控件
         */
        public InvitedViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = (MLImageView) itemView.findViewById(R.id.ml_img_invited_avatar);
            textViewUsername = (TextView) itemView.findViewById(R.id.ml_text_invited_username);
            textViewReason = (TextView) itemView.findViewById(R.id.ml_text_invited_reason);
            textViewStatus = (TextView) itemView.findViewById(R.id.ml_text_invited_status);
            btnAgree = (Button) itemView.findViewById(R.id.ml_btn_invited_agree);
        }
    }
}
