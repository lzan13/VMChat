package net.melove.demo.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.entity.MLConversationEntity;
import net.melove.demo.chat.widget.MLImageView;

import java.util.List;

/**
 * Created by lz on 2015/12/13.
 */
public class MLConversationAdapter extends BaseAdapter {

    private Context mContext;
    private List<MLConversationEntity> mList;

    public MLConversationAdapter(Context context, List<MLConversationEntity> list) {
        mContext = context;
        mList = list;
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

        ViewHolder viewHolder = null;
        MLConversationEntity conversation = (MLConversationEntity) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mUsernameView.setText(conversation.getNick());
        viewHolder.mContentView.setText(conversation.getContent());
        viewHolder.mTimeView.setText(conversation.getTime());
        viewHolder.mCountView.setText(String.valueOf(conversation.getCount()));
        return convertView;
    }

    /**
     * 自定义会话列表项的 ViewHolder 用来显示会话列表项的内容
     */
    class ViewHolder {
        public MLImageView mAvatarView;
        public TextView mUsernameView;
        public TextView mContentView;
        public TextView mTimeView;
        public TextView mCountView;

        /**
         * 构造方法，初始化列表项的控件
         * @param view
         */
        public ViewHolder(View view) {
            mAvatarView = (MLImageView) view.findViewById(R.id.ml_img_conversation_avatar);
            mUsernameView = (TextView) view.findViewById(R.id.ml_text_conversation_username);
            mContentView = (TextView) view.findViewById(R.id.ml_text_conversation_content);
            mTimeView = (TextView) view.findViewById(R.id.ml_text_conversation_time);
            mCountView = (TextView) view.findViewById(R.id.ml_text_conversation_count);
        }
    }


}
