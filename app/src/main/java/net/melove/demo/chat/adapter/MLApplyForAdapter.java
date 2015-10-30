package net.melove.demo.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.info.MLApplyForInfo;
import net.melove.demo.chat.widget.MLImageView;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 */
public class MLApplyForAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MLApplyForInfo> mList;


    public MLApplyForAdapter(Context context, List<MLApplyForInfo> list) {
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
        ViewHendler viewHendler = null;
        MLApplyForInfo info = (MLApplyForInfo) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_apply_for, null);
            viewHendler = new ViewHendler(convertView);
            convertView.setTag(viewHendler);
        } else {
            viewHendler = (ViewHendler) convertView.getTag();
        }
        viewHendler.imageViewAvatar.setImageResource(R.mipmap.icon_avatar_01);
        viewHendler.textViewUsername.setText(info.getUserName());
        viewHendler.textViewReason.setText(info.getReason());
        if (info.getStatus() == MLApplyForInfo.ApplyForStatus.AGREED) {
            viewHendler.textViewStatus.setText("agreed");
            viewHendler.textViewStatus.setVisibility(View.VISIBLE);
            viewHendler.btnAgree.setVisibility(View.GONE);
            viewHendler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForInfo.ApplyForStatus.REFUSED) {
            viewHendler.textViewStatus.setText("refused");
            viewHendler.textViewStatus.setVisibility(View.VISIBLE);
            viewHendler.btnAgree.setVisibility(View.GONE);
            viewHendler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForInfo.ApplyForStatus.BEAGREED) {
            viewHendler.textViewStatus.setText("be agreed");
            viewHendler.textViewStatus.setVisibility(View.VISIBLE);
            viewHendler.btnAgree.setVisibility(View.GONE);
            viewHendler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForInfo.ApplyForStatus.BEREFUSED) {
            viewHendler.textViewStatus.setText("be refused");
            viewHendler.textViewStatus.setVisibility(View.VISIBLE);
            viewHendler.btnAgree.setVisibility(View.GONE);
            viewHendler.btnRefuse.setVisibility(View.GONE);
        } else if (info.getStatus() == MLApplyForInfo.ApplyForStatus.BEAPPLYFOR) {
            viewHendler.textViewStatus.setText("be apply for");
            viewHendler.textViewStatus.setVisibility(View.GONE);
            viewHendler.btnAgree.setVisibility(View.VISIBLE);
            viewHendler.btnRefuse.setVisibility(View.VISIBLE);
        } else if (info.getStatus() == MLApplyForInfo.ApplyForStatus.GROUPAPPLYFOR) {
            viewHendler.textViewStatus.setText("Agreed");
        }
        return convertView;
    }

    private class ViewHendler {
        MLImageView imageViewAvatar;
        TextView textViewUsername;
        TextView textViewReason;
        TextView textViewStatus;
        Button btnAgree;
        Button btnRefuse;

        public ViewHendler(View view) {
            imageViewAvatar = (MLImageView) view.findViewById(R.id.ml_img_apply_for_avatar);
            textViewUsername = (TextView) view.findViewById(R.id.ml_text_apply_for_username);
            textViewReason = (TextView) view.findViewById(R.id.ml_text_apply_for_reason);
            textViewStatus = (TextView) view.findViewById(R.id.ml_text_apply_for_status);
            btnAgree = (Button) view.findViewById(R.id.ml_btn_apply_for_agree);
            btnRefuse = (Button) view.findViewById(R.id.ml_btn_apply_for_refuse);

        }
    }
}
