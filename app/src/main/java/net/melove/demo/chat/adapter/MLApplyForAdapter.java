package net.melove.demo.chat.adapter;

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
import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.entity.MLApplyForEntity;
import net.melove.demo.chat.widget.MLImageView;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 申请与请求的适配器类，继承自BaseAdapter类，并实现了OnClickListener接口
 */
public class MLApplyForAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MLApplyForEntity> mList;

    private MLApplyForDao mApplyForDao;


    public MLApplyForAdapter(Context context, List<MLApplyForEntity> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mApplyForDao = new MLApplyForDao(mContext);
    }

    /**
     * 处理申请与通知列表的刷新操作
     */
    Handler mHandler = new Handler() {
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
    };

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        MLApplyForEntity applyFor = (MLApplyForEntity) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_apply_for, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageViewAvatar.setImageResource(R.mipmap.ic_character_blackcat);
        viewHolder.textViewUsername.setText(applyFor.getUserName());
        viewHolder.textViewReason.setText(applyFor.getReason());

        // 判断当前的申请与通知的状态，显示不同的提醒文字
        if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.AGREED) {
            viewHolder.textViewStatus.setText(R.string.ml_agreed);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.REFUSED) {
            viewHolder.textViewStatus.setText(R.string.ml_refused);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.BEAGREED) {
            viewHolder.textViewStatus.setText(R.string.ml_agreed);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.BEREFUSED) {
            viewHolder.textViewStatus.setText(R.string.ml_refused);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.APPLYFOR) {
            viewHolder.textViewStatus.setText(R.string.ml_waiting);
            viewHolder.textViewStatus.setVisibility(View.VISIBLE);
            viewHolder.btnAgree.setVisibility(View.GONE);
            viewHolder.btnRefuse.setVisibility(View.GONE);
        } else if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.BEAPPLYFOR) {
            viewHolder.textViewStatus.setText(R.string.ml_waiting);
            viewHolder.textViewStatus.setVisibility(View.GONE);
            viewHolder.btnAgree.setVisibility(View.VISIBLE);
            viewHolder.btnRefuse.setVisibility(View.VISIBLE);
        } else if (applyFor.getStatus() == MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR) {
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
    private void agreeApplyFor(int position) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();

        final MLApplyForEntity applyForEntity = (MLApplyForEntity) getItem(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(applyForEntity.getUserName());
                    applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.AGREED);
                    dialog.dismiss();
                    refreshList(applyForEntity);
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
    private void refuseApplyFor(int positon) {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();
        final MLApplyForEntity applyForEntity = (MLApplyForEntity) getItem(positon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(applyForEntity.getUserName());
                    applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.REFUSED);
                    dialog.dismiss();
                    refreshList(applyForEntity);
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
            case R.id.ml_btn_apply_for_agree:
                agreeApplyFor(position);
                break;
            case R.id.ml_btn_apply_for_refuse:
                refuseApplyFor(position);
                break;
        }
    }

    /**
     * 刷新申请与通知列表
     *
     * @param applyForEntity
     */
    private void refreshList(MLApplyForEntity applyForEntity) {
        // 这里进行一下筛选，如果已存在则去更新本地内容
        MLApplyForEntity temp = mApplyForDao.getApplyForEntiry(applyForEntity.getObjId());
        if (temp != null) {
            mApplyForDao.updateApplyFor(applyForEntity);
        } else {
            mApplyForDao.saveApplyFor(applyForEntity);
        }
        mHandler.sendMessage(mHandler.obtainMessage(0));
    }

    /**
     * 用户申请与通知的 ViewHolder
     */
    private class ViewHolder {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;
        Button btnRefuse;

        public ViewHolder(View view) {
            imageViewAvatar = (MLImageView) view.findViewById(R.id.ml_img_apply_for_avatar);
            textViewUsername = (TextView) view.findViewById(R.id.ml_text_apply_for_username);
            textViewReason = (TextView) view.findViewById(R.id.ml_text_apply_for_reason);
            textViewStatus = (TextView) view.findViewById(R.id.ml_text_apply_for_status);
            btnAgree = (Button) view.findViewById(R.id.ml_btn_apply_for_agree);
            btnRefuse = (Button) view.findViewById(R.id.ml_btn_apply_for_refuse);

        }
    }
}
