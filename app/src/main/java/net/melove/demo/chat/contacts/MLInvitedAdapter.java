package net.melove.demo.chat.contacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.widget.MLImageView;
import net.melove.demo.chat.database.MLInvitedDao;

import java.util.List;

/**
 * Created by lzan13 on 2016/3/17.
 */
public class MLInvitedAdapter extends RecyclerView.Adapter<MLInvitedAdapter.InvitedViewHolder> {

    // 上下文对象
    private Context mContext;

    private LayoutInflater mInflater;

    private List<MLInvitedEntity> mInvitedEntities;

    // 邀请与申请信息数据库操作类
    private MLInvitedDao mInvitedDao;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;

    public MLInvitedAdapter(Context context, List<MLInvitedEntity> invitedEntityList) {
        mContext = context;
        mInvitedEntities = invitedEntityList;
        mInflater = LayoutInflater.from(mContext);
        mInvitedDao = new MLInvitedDao(mContext);
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
        holder.textViewUsername.setText(invitedEntity.getUserName());
        holder.textViewReason.setText(invitedEntity.getReason());

        // 判断当前的申请与通知的状态，显示不同的提醒文字
        if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.AGREED) {
            holder.textViewStatus.setText(R.string.ml_agreed);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.REFUSED) {
            holder.textViewStatus.setText(R.string.ml_refused);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAGREED) {
            holder.textViewStatus.setText(R.string.ml_agreed);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEREFUSED) {
            holder.textViewStatus.setText(R.string.ml_refused);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.APPLYFOR) {
            holder.textViewStatus.setText(R.string.ml_waiting);
            holder.textViewStatus.setVisibility(View.VISIBLE);
            holder.btnAgree.setVisibility(View.GONE);
            holder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAPPLYFOR) {
            holder.textViewStatus.setText(R.string.ml_waiting);
            holder.textViewStatus.setVisibility(View.GONE);
            holder.btnAgree.setVisibility(View.VISIBLE);
            holder.btnRefuse.setVisibility(View.VISIBLE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.GROUPAPPLYFOR) {
            holder.textViewStatus.setText(R.string.ml_waiting);
            holder.btnAgree.setVisibility(View.VISIBLE);
            holder.btnRefuse.setVisibility(View.VISIBLE);
        }
        holder.btnAgree.setTag(position);
        holder.btnRefuse.setTag(position);
        holder.btnAgree.setOnClickListener(viewListener);
        holder.btnRefuse.setOnClickListener(viewListener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onItemLongClick(v, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInvitedEntities.size();
    }

    /**
     * 同意好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void agreeInvited(int position) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();

        final MLInvitedEntity invitedEntity = mInvitedEntities.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(invitedEntity.getUserName());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
                    mInvitedDao.updateInvited(invitedEntity);
                    dialog.dismiss();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    /**
     * 拒绝好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void refuseInvited(int positon) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();
        final MLInvitedEntity invitedEntity = mInvitedEntities.get(positon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(invitedEntity.getUserName());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
                    mInvitedDao.updateInvited(invitedEntity);
                    dialog.dismiss();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                    agreeInvited(position);
                    break;
                case R.id.ml_btn_invited_refuse:
                    refuseInvited(position);
                    break;
            }
        }
    };

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        public void onItemClick(View view, int position);

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
     * 自定义ViewHolder
     */
    protected static class InvitedViewHolder extends RecyclerView.ViewHolder {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;
        Button btnRefuse;

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
            btnRefuse = (Button) itemView.findViewById(R.id.ml_btn_invited_refuse);
        }
    }
}
