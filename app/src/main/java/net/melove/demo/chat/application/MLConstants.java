package net.melove.demo.chat.application;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/9/10 22:04.
 */
public class MLConstants {

    // 设置消息中 msgId 扩展的 key
    public static final String ML_ATTR_MSG_ID = "ml_msg_id";

    // 设置自己扩展的 key
    public static final String ML_ATTR_BURN = "ml_burn";
    public static final String ML_ATTR_RECALL = "ml_recall";
    public static final String ML_ATTR_TYPE = "ml_type";
    public static final String ML_ATTR_AT = "ml_at";
    public static final String ML_ATTR_TOP = "ml_top";
    public static final String ML_ATTR_LAST_TIME = "ml_list_time";


    // 界面跳转传递 username/groupid 参数的 key
    public static final String ML_EXTRA_CHAT_ID = "ml_chat_id";

    // 自定义广播的 action
    public static final String ML_ACTION_INVITED = "ml_action_invited";
    public static final String ML_ACTION_CONTACT = "ml_action_contact";
    public static final String ML_ACTION_MESSAGE = "ml_action_message";


    // SharedPreference 保存内容的 key
    public static final String ML_SHARED_USERNAME = "ml_username";
    public static final String ML_SHARED_PASSWORD = "ml_password";


    // 请求码
    public static final int ML_REQUEST_CODE_PHOTO = 0x01;
    public static final int ML_REQUEST_CODE_GALLERY = 0x02;
    public static final int ML_REQUEST_CODE_VIDEO = 0x03;
    public static final int ML_REQUEST_CODE_FILE = 0x04;
    public static final int ML_REQUEST_CODE_LOCATION = 0x05;
    public static final int ML_REQUEST_CODE_GIFT = 0x06;
    public static final int ML_REQUEST_CODE_CONTACTS = 0x07;


}
