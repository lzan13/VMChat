package net.melove.demo.chat.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.widget.MLImageView;

import java.util.List;

/**
 * Created by lz on 2015/12/13.
 * 会话列表的适配器，
 */
public class MLConversationAdapter extends BaseAdapter {

    // 当前 Adapter 的上下文对象
    private Context mContext;
    // 会话列表的数据源
    private List<EMConversation> mList;

    private final int HANDLER_MSG_REFRESH = 0;

    /**
     * 构造方法，需传递过来上下文对象和数据源
     *
     * @param context 上下文对象
     * @param list    需要展示的会话列表集合
     */
    public MLConversationAdapter(Context context, List<EMConversation> list) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        EMConversation conversation = (EMConversation) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String content = "";
        String time = "";
        // 判断当前会话在本地是否有聊天记录，并根据结果获取最后一条消息的内容
        if (conversation.getAllMessages().size() > 0) {
            EMMessage message = conversation.getLastMessage();
            // 获取最后消息的时间，并转为String类型
            time = MLDate.long2Time(message.getMsgTime());
            switch (message.getType()) {
                case TXT:
                    content = ((EMTextMessageBody) message.getBody()).getMessage();
                    break;
                case FILE:
                    content = "[" + mContext.getString(R.string.ml_file) + "]";
                    break;
                case IMAGE:
                    content = "[" + mContext.getString(R.string.ml_photo) + "]";
                    break;
                case LOCATION:
                    content = "[" + mContext.getString(R.string.ml_location) + "]";
                    break;
                case VIDEO:
                    content = "[" + mContext.getString(R.string.ml_video) + "]";
                    break;
                case VOICE:
                    content = "[" + mContext.getString(R.string.ml_voice) + "]";
                    break;
                default:
                    break;
            }
        }
        viewHolder.mContentView.setText(content);
        viewHolder.mUsernameView.setText(conversation.getUserName());
        viewHolder.mTimeView.setText(time);
        viewHolder.mCountView.setText(String.valueOf(conversation.getUnreadMsgCount()));
        return convertView;
    }

    /**
     * 供界面调用的刷新 Adapter 的方法
     */
    public void refreshList() {
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_MSG_REFRESH;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    Handler mHandler = new Handler() {
        private void refresh() {
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(Message msg) {
            //            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MSG_REFRESH:
                    refresh();
                    break;
            }
        }
    };

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
         *
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
