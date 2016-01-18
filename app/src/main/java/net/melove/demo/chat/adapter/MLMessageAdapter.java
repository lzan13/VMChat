package net.melove.demo.chat.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
 * <p>
 * Created by lzan13 on 2016/1/6 18:51.
 */
public class MLMessageAdapter extends BaseAdapter {

    private static final int MSG_TYPE_TXT_SEND = 0;
    private static final int MSG_TYPE_TXT_received = 1;
    private static final int MSG_TYPE_IMAGE_SEND = 2;
    private static final int MSG_TYPE_IMAGE_received = 3;

    private Context mContext;

    private LayoutInflater mInflater;

    private ListView mListView;
    private EMConversation mConversation;
    private List<EMMessage> messages;


    /**
     * 构造方法
     *
     * @param context
     * @param chatId
     * @param listView
     */
    public MLMessageAdapter(Context context, String chatId, ListView listView) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        mListView = listView;
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
        // 获取当前要显示的 message 对象
        EMMessage message = (EMMessage) getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = createItemView(message);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.avatarView.setVisibility(View.GONE);
        viewHolder.contentView.setText(((TextMessageBody) message.getBody()).getMessage().toString());
        viewHolder.usernameView.setText(message.getFrom());
        viewHolder.timeView.setText(MLDate.long2Time(message.getMsgTime()));
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        if (message.status == EMMessage.Status.FAIL) {
            viewHolder.msgState.setVisibility(View.VISIBLE);
            viewHolder.msgState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 重新发送  在这里实现重发逻辑

                }
            });
        } else {
            viewHolder.msgState.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 重写 Adapter 的 view 类型数目方法（此方法必须重写，否则在不同类型的 Item 重用时会出现错误）
     *
     * @return 返回当前 adapter 当前 ListView 最多显示的 Item 类型数
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * 重写 Adapter 的获取当前 Item 类型的方法（必须重写，同上）
     *
     * @param position 当前 Item 位置
     * @return 当前 Item 的类型
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = (EMMessage) getItem(position);
        int itemType = -1;
        switch (message.getType()) {
            case TXT:
                itemType = message.direct == EMMessage.Direct.SEND ? MSG_TYPE_TXT_SEND : MSG_TYPE_TXT_received;
                break;
            default:
                itemType = message.direct == EMMessage.Direct.SEND ? MSG_TYPE_TXT_SEND : MSG_TYPE_TXT_received;
                break;
        }
        return itemType;
    }

    /**
     * 获取 item 的布局，根据传入的消息类型不同，返回不同的布局
     *
     * @param message 要展示的消息
     * @return 要显示的布局
     */
    private View createItemView(EMMessage message) {
        View itemView = null;
        switch (message.getType()) {
            case TXT:
                itemView = message.direct == EMMessage.Direct.SEND
                        ? mInflater.inflate(R.layout.item_msg_text_send, null)
                        : mInflater.inflate(R.layout.item_msg_text_received, null);
                break;
            default:
                itemView = message.direct == EMMessage.Direct.SEND
                        ? mInflater.inflate(R.layout.item_msg_text_send, null)
                        : mInflater.inflate(R.layout.item_msg_text_received, null);
                break;
        }
        return itemView;
    }

    /**
     * 供界面调用的刷新 Adapter 的方法
     */
    public void refreshList() {
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    Handler mHandler = new Handler() {

        private void refresh() {
//            messages = mConversation.getAllMessages();
            notifyDataSetChanged();
            mListView.setSelection(messages.size() - 1);
        }

        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    refresh();
                    break;
            }
        }
    };

    /**
     * 非静态内部类会隐式持有外部类的引用，就像大家经常将自定义的adapter在Activity类里，
     * 然后在adapter类里面是可以随意调用外部activity的方法的。当你将内部类定义为static时，
     * 你就调用不了外部类的实例方法了，因为这时候静态内部类是不持有外部类的引用的。
     * 声明ViewHolder静态内部类，可以将ViewHolder和外部类解引用。
     * 大家会说一般ViewHolder都很简单，不定义为static也没事吧。
     * 确实如此，但是如果你将它定义为static的，说明你懂这些含义。
     * 万一有一天你在这个ViewHolder加入一些复杂逻辑，做了一些耗时工作，
     * 那么如果ViewHolder是非静态内部类的话，就很容易出现内存泄露。如果是静态的话，
     * 你就不能直接引用外部类，迫使你关注如何避免相互引用。 所以将 ViewHolder内部类 定义为静态的，是一种好习惯
     */
    static class ViewHolder {
        MLImageView avatarView;
        TextView usernameView;
        TextView contentView;
        TextView timeView;
        ImageView msgState;

        public ViewHolder(View view) {
            avatarView = (MLImageView) view.findViewById(R.id.ml_img_msg_avatar);
            contentView = (TextView) view.findViewById(R.id.ml_text_msg_content);
            usernameView = (TextView) view.findViewById(R.id.ml_text_msg_username);
            timeView = (TextView) view.findViewById(R.id.ml_text_msg_time);
            msgState = (ImageView) view.findViewById(R.id.ml_img_msg_state);
        }
    }
}
