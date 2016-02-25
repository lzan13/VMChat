package net.melove.demo.chat.application;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/9/10 22:04.
 */
public class MLConstants {

    // 设置消息中 msgId 扩展的 key
    public static final String ML_ATTR_MSG_ID = "msg_id";
    // 设置自己扩展的消息类型的 key
    public static final String ML_ATTR_MSG_TYPE = "msg_type";
    public static final String ML_ATTR_MSG_TYPE_RECALL = "recall";
    public static final String ML_ATTR_MSG_TYPE_BURN = "burn";


    // 界面跳转传递 username/groupid 参数的 key
    public static final String ML_EXTRA_CHAT_ID = "chat_id";


    public static final String ML_SHARED_USERNAME = "username";
    public static final String ML_SHARED_PASSWORD = "password";


    // 请求码
    public static final int ML_REQUEST_CODE_PHOTO = 0x01;
    public static final int ML_REQUEST_CODE_GALLERY = 0x02;
    public static final int ML_REQUEST_CODE_VIDEO = 0x03;
    public static final int ML_REQUEST_CODE_FILE = 0x04;
    public static final int ML_REQUEST_CODE_LOCATION = 0x05;
    public static final int ML_REQUEST_CODE_GIFT = 0x06;
    public static final int ML_REQUEST_CODE_CONTACTS = 0x07;


}
