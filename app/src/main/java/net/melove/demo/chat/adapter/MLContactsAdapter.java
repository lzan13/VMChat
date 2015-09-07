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
public class MLContactsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MLApplyForInfo> mList;


    public MLContactsAdapter(Context context, List<MLApplyForInfo> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return 0;
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
            convertView = mInflater.inflate(R.layout.item_contact, null);
            viewHendler = new ViewHendler(convertView);
            convertView.setTag(viewHendler);
        } else {
            viewHendler = (ViewHendler) convertView.getTag();
        }
        viewHendler.imageViewAvatar.setImageResource(R.mipmap.icon_avatar_01);
        viewHendler.textViewUsername.setText(info.getUserName());

        return convertView;
    }

    private class ViewHendler {
        MLImageView imageViewAvatar;
        TextView textViewUsername;


        public ViewHendler(View view) {
            imageViewAvatar = (MLImageView) view.findViewById(R.id.ml_img_apply_for_avatar);
            textViewUsername = (TextView) view.findViewById(R.id.ml_text_apply_for_username);

        }
    }
}
