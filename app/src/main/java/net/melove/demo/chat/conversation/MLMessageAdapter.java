package net.melove.demo.chat.conversation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.PathUtil;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.widget.MLImageView;

import java.util.List;


/**
 * Class ${FILE_NAME}
 * <p>
 * Created by lzan13 on 2016/1/6 18:51.
 */
public class MLMessageAdapter extends RecyclerView.Adapter<MLMessageAdapter.MessageViewHolder> {

    // 消息类型数
    private final int MSG_TYPE_COUNT = 5;
    // 正常的消息类型
    private final int MSG_TYPE_TXT_SEND = 0;
    private final int MSG_TYPE_TXT_RECEIVED = 1;
    private final int MSG_TYPE_IMAGE_SEND = 2;
    private final int MSG_TYPE_IMAGE_RECEIVED = 3;

    // 系统级消息类型
    // 撤回类型消息
    private final int MSG_TYPE_SYS_RECALL = 4;

    // 刷新类型
    private final int HANDLER_MSG_REFRESH = 0;
    private final int HANDLER_MSG_REFRESH_MORE = 1;

    private Context mContext;

    private LayoutInflater mInflater;

    private RecyclerView mRecyclerView;
    private EMConversation mConversation;
    private List<EMMessage> mMessages;

    // 自定义的回调接口
    private MLOnItemClickListener mOnItemClickListener;
    private MLHandler mHandler;
    private boolean isMove = false;

    /**
     * 构造方法
     *
     * @param context
     * @param chatId
     * @param recyclerView
     */
    public MLMessageAdapter(Context context, String chatId, RecyclerView recyclerView) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mRecyclerView = recyclerView;

        mHandler = new MLHandler();

        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(chatId, null, true);
        mMessages = mConversation.getAllMessages();

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * 重写 Adapter 的 view 类型数目方法（此方法必须重写，否则在不同类型的 Item 重用时会出现错误）
     *
     * @return 返回当前 adapter 当前 ListView 最多显示的 Item 类型数
     */


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createItemView(parent, viewType);
        MessageViewHolder holder = new MessageViewHolder(view);
        switch (viewType) {
            // 文字类消息
            case MSG_TYPE_TXT_SEND:
            case MSG_TYPE_TXT_RECEIVED:
                holder.avatarView = (MLImageView) view.findViewById(R.id.ml_img_msg_avatar);
                holder.contentView = (TextView) view.findViewById(R.id.ml_text_msg_content);
                holder.usernameView = (TextView) view.findViewById(R.id.ml_text_msg_username);
                holder.timeView = (TextView) view.findViewById(R.id.ml_text_msg_time);
                holder.msgState = (ImageView) view.findViewById(R.id.ml_img_msg_state);
                break;
            case MSG_TYPE_IMAGE_SEND:
            case MSG_TYPE_IMAGE_RECEIVED:
                holder.avatarView = (MLImageView) view.findViewById(R.id.ml_img_msg_avatar);
                holder.imageView = (MLImageView) view.findViewById(R.id.ml_img_msg_image);
                holder.usernameView = (TextView) view.findViewById(R.id.ml_text_msg_username);
                holder.timeView = (TextView) view.findViewById(R.id.ml_text_msg_time);
                holder.msgState = (ImageView) view.findViewById(R.id.ml_img_msg_state);
                break;
            // 自定义类型的消息
            case MSG_TYPE_SYS_RECALL:
                holder.timeView = (TextView) view.findViewById(R.id.ml_text_msg_time);
                holder.contentView = (TextView) view.findViewById(R.id.ml_text_msg_content);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, final int position) {
        // 获取当前要显示的 message 对象
        EMMessage message = mMessages.get(position);

        // viewHolder.avatarView.setImageBitmap();
        holder.usernameView.setText(message.getFrom());
        holder.timeView.setText(MLDate.long2Time(message.getMsgTime()));
        switch (message.getType()) {
            case TXT:
                handleTextMessage(message, holder);
                break;
            case IMAGE:
                handleImageMessage(message, holder);
                break;
            case FILE:
                break;
            case LOCATION:
                break;
            case VIDEO:
                break;
            case VOICE:
                break;
            default:
                break;
        }

        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        if (message.status() == EMMessage.Status.FAIL) {
            holder.msgState.setVisibility(View.VISIBLE);
            holder.msgState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 重新发送  在这里实现重发逻辑
                }
            });
        } else {
            holder.msgState.setVisibility(View.GONE);
        }
        /**
         * 为每个Item设置点击与长按监听
         */
        holder.timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onItemLongClick(v, position);
                return false;
            }
        });
    }

    /**
     * 重写 Adapter 的获取当前 Item 类型的方法（必须重写，同上）
     *
     * @param position 当前 Item 位置
     * @return 当前 Item 的类型
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = mMessages.get(position);
        int itemType = -1;
        switch (message.getType()) {
            case TXT:
                // 判断是否为撤回类型的消息
                if (message.getBooleanAttribute(MLConstants.ML_ATTR_TYPE, false)) {
                    itemType = MSG_TYPE_SYS_RECALL;
                } else {
                    itemType = message.direct() == EMMessage.Direct.SEND ? MSG_TYPE_TXT_SEND : MSG_TYPE_TXT_RECEIVED;
                }
                break;
            case IMAGE:
                itemType = message.direct() == EMMessage.Direct.SEND ? MSG_TYPE_IMAGE_SEND : MSG_TYPE_IMAGE_RECEIVED;
                break;
            default:
                // 默认返回txt类型
                itemType = message.direct() == EMMessage.Direct.SEND ? MSG_TYPE_TXT_SEND : MSG_TYPE_TXT_RECEIVED;
                break;
        }
        return itemType;
    }

    /**
     * 获取 item 的布局，根据传入的消息类型不同，返回不同的布局
     *
     * @param viewType 消息类型
     * @return 要显示的布局
     */
    private View createItemView(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case MSG_TYPE_SYS_RECALL:
                itemView = mInflater.inflate(R.layout.item_msg_recall, parent, false);
                break;
            case MSG_TYPE_TXT_SEND:
                itemView = mInflater.inflate(R.layout.item_msg_text_send, parent, false);
                break;
            case MSG_TYPE_TXT_RECEIVED:
                itemView = mInflater.inflate(R.layout.item_msg_text_received, parent, false);
                break;
            case MSG_TYPE_IMAGE_SEND:
                itemView = mInflater.inflate(R.layout.item_msg_image_send, parent, false);
                break;
            case MSG_TYPE_IMAGE_RECEIVED:
                itemView = mInflater.inflate(R.layout.item_msg_image_received, parent, false);
                break;
            default:
                itemView = mInflater.inflate(R.layout.item_msg_recall, parent, false);
                break;
        }
        return itemView;
    }

    /**
     * 处理文字类消息
     *
     * @param message    要展示的消息对象
     * @param viewHolder 展示消息内容的 ViewHolder
     */
    private void handleTextMessage(EMMessage message, MessageViewHolder viewHolder) {
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String messageStr = body.getMessage().toString();
        // 判断是不是阅后即焚的消息
        if (message.getBooleanAttribute(MLConstants.ML_ATTR_BURN, false)) {
            viewHolder.contentView.setText(String.format("【内容长度%d】点击阅读", messageStr.length()));
        } else {
            viewHolder.contentView.setText(messageStr);
        }
    }

    private void handleImageMessage(EMMessage message, MessageViewHolder viewHolder) {
        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设为true了
        // 这个参数的意义是仅仅解析边缘区域，从而可以得到图片的一些信息，比如大小，而不会整个解析图片，防止OOM
        options.inJustDecodeBounds = true;
        // 此时bitmap还是为空的
        String thumbName = body.getThumbnailUrl().substring(body.getThumbnailUrl().indexOf("/") + 1, body.getThumbnailUrl().length());
        Bitmap bitmap = BitmapFactory.decodeFile(PathUtil.imagePathName + thumbName, options);
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        viewHolder.imageView.setLayoutParams(new ViewGroup.LayoutParams(actualWidth, actualHeight));
        viewHolder.imageView.setImageBitmap(bitmap);
    }

    /**
     * 自定义回调接口，用来实现 RecyclerView 中 Item 长按和点击事件监听
     */
    protected interface MLOnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    /**
     * 设置回调监听
     *
     * @param listener 自定义回调接口
     */
    public void setOnItemClickListener(MLOnItemClickListener listener) {
        mOnItemClickListener = listener;
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
     * 刷新列表，并滚动到指定位置
     *
     * @param position 要滚动到的位置
     */
    public void refreshList(int position) {
        Message msg = mHandler.obtainMessage();
        msg.what = HANDLER_MSG_REFRESH_MORE;
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }

    /**
     * 自定义Handler，用来处理消息的刷新等
     */
    private class MLHandler extends Handler {

        /**
         * 刷新聊天信息列表，并滚动到底部
         */
        private void refresh() {
            mMessages.clear();
            mMessages = mConversation.getAllMessages();
            notifyDataSetChanged();
            if (mMessages.size() > 1) {
                // 平滑滚动到最后一条
                mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
            }
        }

        /**
         * 刷新界面并平滑滚动到新加载的记录位置
         *
         * @param position 新加载的内容的最后一个位置
         */
        private void refresh(final int position) {
            mMessages.clear();
            mMessages = mConversation.getAllMessages();
            notifyDataSetChanged();
            /**
             * 平滑滚动到最后一条，这里使用了ListView的两个方法:setSelection()/smoothScrollToPosition();
             * 如果单独使用setSelection()就会直接跳转到指定位置，没有平滑的效果
             * 如果单独使用smoothScrollToPosition() 就会因为我们的item高度不同导致滚动有偏差
             * 所以我们要先使用setSelection()跳转到指定位置，然后再用smoothScrollToPosition()平滑滚动到上一个
             */
            /**
             * 平滑滚动到指定条目，在 RecyclerView 控件中，scrollToPosition() 方法可以将指定条目滚动到底部，
             * 需要自己实现滚动的监听
             */
            mRecyclerView.scrollToPosition(position);
            final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            // 设置设置为 true 表示需要检测 RecyclerView 的滚动
            isMove = true;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // 这里必须加上一个判断，不然 RecyclerView 每次滑动都会触发这个事件
                    if (isMove) {
                        int index = position - layoutManager.findFirstCompletelyVisibleItemPosition() - 1;
                        if (index > 0 && index < mRecyclerView.getChildCount()) {
                            int top = mRecyclerView.getChildAt(index).getTop();
                            mRecyclerView.scrollBy(0, top);
                        }
                        isMove = false;
                    }
                }
            });
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_REFRESH:
                    refresh();
                    break;
                case HANDLER_MSG_REFRESH_MORE:
                    refresh(msg.arg1);
                    break;
            }
        }
    }

    /**
     * 非静态内部类会隐式持有外部类的引用，就像大家经常将自定义的adapter在Activity类里，
     * 然后在adapter类里面是可以随意调用外部activity的方法的。当你将内部类定义为static时，
     * 你就调用不了外部类的实例方法了，因为这时候静态内部类是不持有外部类的引用的。
     * 声明ViewHolder静态内部类，可以将ViewHolder和外部类解引用。
     * 大家会说一般ViewHolder都很简单，不定义为static也没事吧。
     * 确实如此，但是如果你将它定义为static的，说明你懂这些含义。
     * 万一有一天你在这个ViewHolder加入一些复杂逻辑，做了一些耗时工作，
     * 那么如果ViewHolder是非静态内部类的话，就很容易出现内存泄露。如果是静态的话，
     * 你就不能直接引用外部类，迫使你关注如何避免相互引用。 所以将 ViewHolder 定义为静态的
     */
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        MLImageView avatarView;
        MLImageView imageView;
        TextView usernameView;
        TextView contentView;
        TextView timeView;
        ImageView msgState;

        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
