package net.melove.demo.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.entity.MLApplyForEntity;
import net.melove.demo.chat.widget.MLImageView;
import net.melove.demo.chat.widget.MLToast;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 申请与请求的适配器类，继承自BaseAdapter类，并实现了OnClickListener接口
 */
public class MLApplyForAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MLApplyForEntity> mList;


    public MLApplyForAdapter(Context context, List<MLApplyForEntity> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

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
        ViewHandler viewHandler = null;
        MLApplyForEntity info = (MLApplyForEntity) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_apply_for, null);
            viewHandler = new ViewHandler(convertView);
            convertView.setTag(viewHandler);
        } else {
            viewHandler = (ViewHandler) convertView.getTag();
        }
        viewHandler.imageViewAvatar.setImageResource(R.mipmap.ic_character_blackcat);
        viewHandler.textViewUsername.setText(info.getUserName());
        viewHandler.textViewReason.setText(info.getReason());
        if (info.getStatus() == MLApplyForEntity.ApplyForStatus.AGREED) {
            viewHandler.textViewStatus.setText("agreed");
            viewHandler.textViewStatus.setVisibility(View.VISIBLE);
            viewHandler.btnAgree.setVisibility(View.GONE);
            viewHandler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForEntity.ApplyForStatus.REFUSED) {
            viewHandler.textViewStatus.setText("refused");
            viewHandler.textViewStatus.setVisibility(View.VISIBLE);
            viewHandler.btnAgree.setVisibility(View.GONE);
            viewHandler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForEntity.ApplyForStatus.BEAGREED) {
            viewHandler.textViewStatus.setText("be agreed");
            viewHandler.textViewStatus.setVisibility(View.VISIBLE);
            viewHandler.btnAgree.setVisibility(View.GONE);
            viewHandler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForEntity.ApplyForStatus.BEREFUSED) {
            viewHandler.textViewStatus.setText("be refused");
            viewHandler.textViewStatus.setVisibility(View.VISIBLE);
            viewHandler.btnAgree.setVisibility(View.GONE);
            viewHandler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForEntity.ApplyForStatus.BEAPPLYFOR) {
            viewHandler.textViewStatus.setText("be apply for");
            viewHandler.textViewStatus.setVisibility(View.GONE);
            viewHandler.btnAgree.setVisibility(View.VISIBLE);
            viewHandler.btnRefuse.setVisibility(View.VISIBLE);
        } else if (info.getStatus() == MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR) {
            viewHandler.textViewStatus.setText("Agreed");
        }
        viewHandler.btnAgree.setTag(position);
        viewHandler.btnRefuse.setTag(position);
        viewHandler.btnAgree.setOnClickListener(this);
        viewHandler.btnRefuse.setOnClickListener(this);
        return convertView;
    }

    /**
     * by lzan13 2015-11-2 11:10:18
     * 同意好友请求
     */
    private void agreeApplyFor(int position) {
        MLToast.makeToast("agree apply for").show();
        final MLApplyForEntity info = (MLApplyForEntity) getItem(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().acceptInvitation(info
                            .getUserName());
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * by lzan13 2015-11-2 11:10:44
     * 拒绝好友请求
     */
    private void refuseApplyFor(int positon) {
        MLToast.makeToast("refuse apply for").show();
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
     *
     */
    private class ViewHandler {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;
        Button btnRefuse;

        public ViewHandler(View view) {
            imageViewAvatar = (MLImageView) view.findViewById(R.id.ml_img_apply_for_avatar);
            textViewUsername = (TextView) view.findViewById(R.id.ml_text_apply_for_username);
            textViewReason = (TextView) view.findViewById(R.id.ml_text_apply_for_reason);
            textViewStatus = (TextView) view.findViewById(R.id.ml_text_apply_for_status);
            btnAgree = (Button) view.findViewById(R.id.ml_btn_apply_for_agree);
            btnRefuse = (Button) view.findViewById(R.id.ml_btn_apply_for_refuse);

        }
    }
}
