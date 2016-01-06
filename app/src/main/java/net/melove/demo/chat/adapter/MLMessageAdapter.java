package net.melove.demo.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.util.MLDate;
import net.melove.demo.chat.widget.MLImageView;

import java.util.List;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2016/1/6 18:51.
 */
public class MLMessageAdapter extends BaseAdapter {

    private static final int MSG_TYPE_TXT_SEND = 0;
    private static final int MSG_TYPE_TXT_received = 1;
    private static final int MSG_TYPE_IMAGE_SEND = 2;
    private static final int MSG_TYPE_IMAGE_received = 3;

    private Context mContext;

    private LayoutInflater mInflater;

    private EMConversation mConversation;
    private List<EMMessage> messages;

    public MLMessageAdapter(Context context, String chatId) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mConversation = EMChatManager.getInstance().getConversation(chatId);
        messages = mConversation.getAllMessages();

    }

    @Override
    public int getCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public Object getItem(int position) {
        if (messages != null && position < messages.size()) {
            return messages.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = (EMMessage) getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = createItemView(message);
            viewHolder = new ViewHolder();
            switch (message.getType()) {
                case TXT:
                    viewHolder.avatarView = (MLImageView) convertView.findViewById(R.id.ml_img_msg_avatar);
                    viewHolder.contentView = (TextView) convertView.findViewById(R.id.ml_text_msg_content);
                    viewHolder.usernameView = (TextView) convertView.findViewById(R.id.ml_text_msg_username);
                    viewHolder.timeView = (TextView) convertView.findViewById(R.id.ml_text_msg_time);
                    break;
                case IMAGE:
                    break;
                default:
                    break;
            }

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.contentView.setText(((TextMessageBody) message.getBody()).getMessage());
        viewHolder.usernameView.setText(message.getFrom());
        viewHolder.timeView.setText(MLDate.formatDate(message.getMsgTime()));
        return convertView;
    }

    /**
     * 重写 Adapter 的 view 类型数目方法（此方法必须重写，否则在不同类型的 Item 重用时会出现错误）
     *
     * @return 返回当前 adapter 可能显示的 View 类型数
     */
    @Override
    public int getViewTypeCount() {
        return 12;
    }

    /**
     * 重写 Adapter 的获取当前 Item 类型的方法（必须重写，同上）
     *
     * @param position 当前 Item 位置
     * @return 返回当前 Item 的类型
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = (EMMessage) getItem(position);
        int itemType = -1;
        switch (message.getType()) {
            case TXT:
                itemType = message.direct == EMMessage.Direct.SEND ? MSG_TYPE_TXT_SEND : MSG_TYPE_TXT_received;
                break;
            case IMAGE:
                break;
            case FILE:
                break;
            case VOICE:
                break;
            case VIDEO:
                break;
            case LOCATION:
                break;
            case CMD:

                break;
            default:
                break;
        }
        return itemType;
    }

    private View createItemView(EMMessage message) {
        View itemView = null;
        switch (message.getType()) {
            case TXT:
                itemView = message.direct == EMMessage.Direct.SEND
                        ? mInflater.inflate(R.layout.item_msg_text_send, null)
                        : mInflater.inflate(R.layout.item_msg_text_received, null);
                break;
            case IMAGE:

        }
        return itemView;
    }

    static class ViewHolder {
        MLImageView avatarView;
        TextView usernameView;
        TextView contentView;
        TextView timeView;
    }
}
