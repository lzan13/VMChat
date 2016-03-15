package net.melove.demo.chat.conversation;

import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.widget.MLImageView;

import java.util.List;

/**
 * Created by lz on 2015/12/13.
 * 会话列表的适配器，供{@link MLConversationsFragment}使用
 */
public class MLConversationAdapter extends BaseAdapter {

    // 当前 Adapter 的上下文对象
    private Context mContext;
    // 会话列表的数据源
    private List<EMConversation> mList;

    // 刷新会话列表
    private final int HANDLER_CONVERSATION_REFRESH = 0;
    private MLHandler mHandler;

    /**
     * 构造方法，需传递过来上下文对象和数据源
     *
     * @param context 上下文对象
     * @param list    需要展示的会话列表集合
     */
    public MLConversationAdapter(Context context, List<EMConversation> list) {
        mContext = context;
        mList = list;
        mHandler = new MLHandler();
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

        // 设置当前会话的最后时间
        String time = "";
        // 获取最后消息的时间，并转为String类型
        // time = MLDate.long2Time(conversation.getLastMessage().getMsgTime());
        /**
         * 这里改为通过给 EMConversation 对象添加了一个时间扩展，这样可以避免在会话没有消息时，无法显示时间的问题
         * 调用{@link MLConversationExtUtils#getConversationLastTime(EMConversation)}获取扩展里的时间
         */
        time = MLDate.long2Time(MLConversationExtUtils.getConversationLastTime(conversation));
        viewHolder.timeView.setText(time);

        String content = "";
        // 判断当前会话在本地是否有聊天记录，并根据结果获取最后一条消息的内容
        if (conversation.getAllMessages().size() > 0) {
            switch (conversation.getLastMessage().getType()) {
                case TXT:
                    content = ((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage();
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
        } else {
            content = "-空-";
        }
        viewHolder.contentView.setText(content);
        // 设置当前会话联系人名称
        viewHolder.usernameView.setText(conversation.getUserName());


        // 设置当前会话未读数
        int unreadCount = conversation.getUnreadMsgCount();
        if (unreadCount == 0) {
            viewHolder.countView.setVisibility(View.GONE);
        } else if (unreadCount >= 100) {
            viewHolder.countView.setVisibility(View.VISIBLE);
            viewHolder.countView.setText("99+");
        } else {
            viewHolder.countView.setVisibility(View.VISIBLE);
            viewHolder.countView.setText(String.valueOf(unreadCount));
        }
        /**
         * 判断当前会话是否置顶
         * 调用工具类{@link MLConversationExtUtils#setConversationTop(EMConversation, boolean)}进行设置
         */
        if (MLConversationExtUtils.getConversationTop(conversation)) {
            viewHolder.pushpinView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.pushpinView.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 供界面调用的刷新 Adapter 的方法
     */
    public void refreshList() {
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_CONVERSATION_REFRESH;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    private class MLHandler extends Handler {
        private void refresh() {
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(Message msg) {
            //            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_CONVERSATION_REFRESH:
                    refresh();
                    break;
            }
        }
    }

    ;

    /**
     * 自定义会话列表项的 ViewHolder 用来显示会话列表项的内容
     */
    class ViewHolder {
        public MLImageView avatarView;
        public TextView usernameView;
        public TextView contentView;
        public TextView timeView;
        public ImageView pushpinView;
        public TextView countView;

        /**
         * 构造方法，初始化列表项的控件
         *
         * @param view
         */
        public ViewHolder(View view) {
            avatarView = (MLImageView) view.findViewById(R.id.ml_img_conversation_avatar);
            usernameView = (TextView) view.findViewById(R.id.ml_text_conversation_username);
            contentView = (TextView) view.findViewById(R.id.ml_text_conversation_content);
            timeView = (TextView) view.findViewById(R.id.ml_text_conversation_time);
            pushpinView = (ImageView) view.findViewById(R.id.ml_img_conversation_pushpin);
            countView = (TextView) view.findViewById(R.id.ml_text_conversation_count);
        }
    }


}
