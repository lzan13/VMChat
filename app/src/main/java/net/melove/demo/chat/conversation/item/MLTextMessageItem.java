package net.melove.demo.chat.conversation.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.widget.MLImageView;

/**
 * Created by lz on 2016/3/20.
 * 文字消息处理类
 */
public class MLTextMessageItem extends MLMessageItem {

    // 当前 Item 需要处理的 EMMessage 对象
    private EMMessage mMessage;

    public MLTextMessageItem(Context context, int viewType) {
        super(context, viewType);

        onInflateView();
    }

    @Override
    public void onSetupView(EMMessage message) {
        usernameView.setText(message.getFrom());
        timeView.setText(MLDate.long2Time(message.getMsgTime()));

        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
        String messageStr = body.getMessage().toString();
        // 判断是不是阅后即焚的消息
        if (message.getBooleanAttribute(MLConstants.ML_ATTR_BURN, false)) {
            contentView.setText(String.format("【内容长度%d】点击阅读", messageStr.length()));
        } else {
            contentView.setText(messageStr);
        }
        // 判断消息的状态，如果发送失败就显示重发按钮，并设置重发按钮的监听
        switch (message.status()) {
            case SUCCESS:
                break;
            case FAIL:
                msgState.setVisibility(View.VISIBLE);
                msgState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 重新发送  在这里实现重发逻辑
                    }
                });
                break;
            case INPROGRESS:
                break;
            case CREATE:
                break;
            default:
                msgState.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 解析对应的xml 布局，填充当前 ItemView
     */
    @Override
    protected void onInflateView() {
        if (viewType == MLConstants.MSG_TYPE_TXT_SEND) {
            inflater.inflate(R.layout.item_msg_text_send, this);
            // 文字消息中接收方没有接收失败的情况，也不存在接收进度问题
            msgState = (ImageView) findViewById(R.id.ml_img_msg_state);
            progressBar = (ProgressBar) findViewById(R.id.ml_progressbar_msg);

        } else {
            inflater.inflate(R.layout.item_msg_text_received, this);
        }

        avatarView = (MLImageView) findViewById(R.id.ml_img_msg_avatar);
        contentView = (TextView) findViewById(R.id.ml_text_msg_content);
        usernameView = (TextView) findViewById(R.id.ml_text_msg_username);
        timeView = (TextView) findViewById(R.id.ml_text_msg_time);
    }
}
