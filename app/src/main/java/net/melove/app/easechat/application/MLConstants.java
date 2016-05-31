package net.melove.app.easechat.application;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/9/10 22:04.
 */
public class MLConstants {

    public static final String ML_GCM_NUMBER = "163141467698";

    // 集成小米推送需要的 appid 和 appkey
    public static final String ML_MI_APP_ID = "2882303761517430984";
    public static final String ML_MI_APP_KEY = "5191743065984";

    // 设置消息中 msgId 扩展的 key
    public static final String ML_ATTR_MSG_ID = "ml_msg_id";

    /**
     * 设置自己扩展的 key，包括会话对象{@link com.hyphenate.chat.EMConversation}扩展，
     * 以及消息{@link com.hyphenate.chat.EMMessage}扩展
     */
    public static final String ML_ATTR_AT = "ml_attr_at";
    public static final String ML_ATTR_BURN = "ml_attr_burn";
    public static final String ML_ATTR_LAST_TIME = "ml_attr_list_time";
    public static final String ML_ATTR_RECALL = "ml_attr_recall";
    public static final String ML_ATTR_TOP = "ml_attr_top";
    public static final String ML_ATTR_TYPE = "ml_attr_type";
    public static final String ML_ATTR_DRAFT = "ml_attr_draft";

    /**
     * 自定义一些错误码，表示一些固定的错误
     */
    // 撤回消息错误码，超过时间限制
    public static final int ML_ERROR_I_RECALL_TIME = 5001;
    // 撤回消息错误文字描述
    public static final String ML_ERROR_S_RECALL_TIME = "ml_max_time";


    // 界面跳转传递 username/groupid 参数的 key
    public static final String ML_EXTRA_CHAT_ID = "ml_chat_id";
    public static final String ML_EXTRA_INVITED_ID = "ml_invited_id";

    public static final String ML_EXTRA_USER_LOGIN_OTHER_DIVERS = "ml_user_login_another_devices";
    public static final String ML_EXTRA_USER_REMOVED = "ml_remove";

    // 自定义广播的 action
    public static final String ML_EVENT_ACTION_INVITED = "ml_action_invited";
    public static final String ML_EVENT_ACTION_CONTACT = "ml_action_contact";
    public static final String ML_EVENT_ACTION_MESSAGE = "ml_action_message";
    public static final String ML_EVENT_ACTION_CONNCETION = "ml_action_connection";


    /**
     * 保存数据到 {@link android.content.SharedPreferences}的 key
     */
    public static final String ML_SHARED_USERNAME = "ml_username";
    public static final String ML_SHARED_PASSWORD = "ml_password";

    public static final int ML_TIME_RECALL = 300000;


    /**
     * 当界面跳转需要返回结果时，定义跳转请求码
     */
    public static final int ML_REQUEST_CODE_PHOTO = 0x01;
    public static final int ML_REQUEST_CODE_GALLERY = 0x02;
    public static final int ML_REQUEST_CODE_VIDEO = 0x03;
    public static final int ML_REQUEST_CODE_FILE = 0x04;
    public static final int ML_REQUEST_CODE_LOCATION = 0x05;
    public static final int ML_REQUEST_CODE_GIFT = 0x06;
    public static final int ML_REQUEST_CODE_CONTACTS = 0x07;

    /**
     * 自定义会话界面消息列表项的点击与长按 Action
     */
    public static final int ML_ACTION_MSG_CLICK = 0X00;
    public static final int ML_ACTION_MSG_RESEND = 0X01;
    public static final int ML_ACTION_MSG_COPY = 0X10;
    public static final int ML_ACTION_MSG_FORWARD = 0X11;
    public static final int ML_ACTION_MSG_DELETE = 0X12;
    public static final int ML_ACTION_MSG_RECALL = 0X13;

    /**
     * 自定义申请与请求列表项点击与长按的 Action
     */
    public static final int ML_ACTION_INVITED_CLICK = 0X00;
    public static final int ML_ACTION_INVITED_AGREE = 0X10;
    public static final int ML_ACTION_INVITED_REFUSE = 0X11;
    public static final int ML_ACTION_INVITED_DELETE = 0X12;

    /**
     * RecyclerView Adapter 列表刷新类型
     */
    public static final int ML_NOTIFY_REFRESH_ALL = 0x00;
    public static final int ML_NOTIFY_REFRESH_CHANGED = 0x01;
    public static final int ML_NOTIFY_REFRESH_RANGE_CHANGED = 0x02;
    public static final int ML_NOTIFY_REFRESH_INSERTED = 0x03;
    public static final int ML_NOTIFY_REFRESH_RANGE_INSERTED = 0x04;
    public static final int ML_NOTIFY_REFRESH_MOVED = 0x05;
    public static final int ML_NOTIFY_REFRESH_REMOVED = 0x06;
    public static final int ML_NOTIFY_REFRESH_RANGE_REMOVED = 0x07;

    /**
     * 聊天消息类型
     * 首先是SDK支持的正常的消息类型
     */
    public static final int MSG_TYPE_TEXT_SEND = 0;
    public static final int MSG_TYPE_TEXT_RECEIVED = 1;
    public static final int MSG_TYPE_IMAGE_SEND = 2;
    public static final int MSG_TYPE_IMAGE_RECEIVED = 3;
    public static final int MSG_TYPE_FILE_SEND = 4;
    public static final int MSG_TYPE_FILE_RECEIVED = 5;

    /**
     * 系统级消息类型
     */
    // 撤回类型消息
    public static final int MSG_TYPE_SYS_RECALL = 10;

}
