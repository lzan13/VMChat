package net.melove.demo.chat.contacts;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.database.MLInvitedDao;
import net.melove.demo.chat.common.widget.MLImageView;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 申请与请求的适配器类，继承自BaseAdapter类，并实现了OnClickListener接口
 */
public class MLInvitedAdapter extends BaseAdapter implements View.OnClickListener {

    // 上下文对象
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MLInvitedEntity> mInvitedList;

    // 邀请与申请信息数据库操作类
    private MLInvitedDao mInvitedDao;
    // 自定义Handler
    private MLHandler mHandler;


    public MLInvitedAdapter(Context context, List<MLInvitedEntity> list) {
        mContext = context;
        mInvitedList = list;
        mInflater = LayoutInflater.from(mContext);
        mInvitedDao = new MLInvitedDao(mContext);
        mHandler = new MLHandler();
    }

    @Override
    public int getCount() {
        return mInvitedList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        MLInvitedEntity invitedEntity = (MLInvitedEntity) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_invited, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageViewAvatar.setImageResource(R.mipmap.ic_character_blackcat);
        viewHolder.textViewUsername.setText(invitedEntity.getUserName());
        viewHolder.textViewReason.setText(invitedEntity.getReason());

        // 判断当前的申请与通知的状态，显示不同的提醒文字
        if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.AGREED) {
            viewHolder.textViewStatus.setText(R.string.ml_agreed);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.REFUSED) {
            viewHolder.textViewStatus.setText(R.string.ml_refused);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAGREED) {
            viewHolder.textViewStatus.setText(R.string.ml_agreed);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEREFUSED) {
            viewHolder.textViewStatus.setText(R.string.ml_refused);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.APPLYFOR) {
            viewHolder.textViewStatus.setText(R.string.ml_waiting);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAPPLYFOR) {
            viewHolder.textViewStatus.setText(R.string.ml_waiting);
            viewHolder.textViewStatus.setVisibility(View.GONE);
            viewHolder.btnAgree.setVisibility(View.VISIBLE);
            viewHolder.btnRefuse.setVisibility(View.VISIBLE);
        } else if (invitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.GROUPAPPLYFOR) {
            viewHolder.textViewStatus.setText(R.string.ml_waiting);
            viewHolder.btnAgree.setVisibility(View.VISIBLE);
            viewHolder.btnRefuse.setVisibility(View.VISIBLE);
        }
        viewHolder.btnAgree.setTag(position);
        viewHolder.btnRefuse.setTag(position);
        viewHolder.btnAgree.setOnClickListener(this);
        viewHolder.btnRefuse.setOnClickListener(this);
        return convertView;
    }

    /**
     * by lzan13 2015-11-2 11:10:18
     * 同意好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void agreeInvited(int position) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();

        final MLInvitedEntity invitedEntity = (MLInvitedEntity) getItem(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(invitedEntity.getUserName());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
                    mInvitedDao.updateInvited(invitedEntity);
                    dialog.dismiss();
                    refreshList();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    /**
     * by lzan13 2015-11-2 11:10:44
     * 拒绝好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void refuseInvited(int positon) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();
        final MLInvitedEntity invitedEntity = (MLInvitedEntity) getItem(positon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(invitedEntity.getUserName());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
                    mInvitedDao.updateInvited(invitedEntity);
                    dialog.dismiss();
                    refreshList();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 申请与通知列表内Button点击事件
     */
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

    /**
     * 刷新申请与通知列表
     */
    public void refreshList() {
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        mHandler.sendMessage(msg);
    }

    /**
     * 处理申请与通知列表的刷新操作
     */
    class MLHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 用户申请与通知的 ViewHolder
     */
    static class ViewHolder {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;
        Button btnRefuse;

        public ViewHolder(View view) {
            imageViewAvatar = (MLImageView) view.findViewById(R.id.ml_img_invited_avatar);
            textViewUsername = (TextView) view.findViewById(R.id.ml_text_invited_username);
            textViewReason = (TextView) view.findViewById(R.id.ml_text_invited_reason);
            textViewStatus = (TextView) view.findViewById(R.id.ml_text_invited_status);
            btnAgree = (Button) view.findViewById(R.id.ml_btn_invited_agree);
            btnRefuse = (Button) view.findViewById(R.id.ml_btn_invited_refuse);

        }
    }
}
