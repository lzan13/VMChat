package net.melove.demo.chat.conversation.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.PathUtil;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.widget.MLImageView;

/**
 * Created by lz on 2016/3/20.
 * 图片消息处理类
 */
public class MLImageMessageItem extends MLMessageItem {

    // 当前 Item 需要处理的 EMMessage 对象
    private EMMessage mMessage;

    public MLImageMessageItem(Context context, int viewType) {
        super(context, viewType);

    }

    @Override
    public void onSetupView(EMMessage message) {
        usernameView.setText(message.getFrom());
        timeView.setText(MLDate.long2Time(message.getMsgTime()));

        EMImageMessageBody body = (EMImageMessageBody) message.getBody();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 判断是不是阅后即焚的消息
        if (message.getBooleanAttribute(MLConstants.ML_ATTR_BURN, false)) {
        } else {
        }
        // 开始读入图片，此时把options.inJustDecodeBounds 设为true了
        // 这个参数的意义是仅仅解析边缘区域，从而可以得到图片的一些信息，比如大小，而不会整个解析图片，防止OOM
        options.inJustDecodeBounds = true;
        // 此时bitmap还是为空的
        String thumbName = body.getThumbnailUrl().substring(body.getThumbnailUrl().indexOf("/") + 1, body.getThumbnailUrl().length());
        Bitmap bitmap = BitmapFactory.decodeFile(PathUtil.imagePathName + thumbName, options);
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(actualWidth, actualHeight));
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onInflateView() {
        if (viewType == MLConstants.MSG_TYPE_IMAGE_SEND) {
            inflater.inflate(R.layout.item_msg_image_send, this);
        } else {
            inflater.inflate(R.layout.item_msg_image_received, this);
        }

        avatarView = (MLImageView) findViewById(R.id.ml_img_msg_avatar);
        imageView = (MLImageView) findViewById(R.id.ml_img_msg_image);
        usernameView = (TextView) findViewById(R.id.ml_text_msg_username);
        timeView = (TextView) findViewById(R.id.ml_text_msg_time);
        msgState = (ImageView) findViewById(R.id.ml_img_msg_state);
    }

}
