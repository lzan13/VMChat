package net.melove.app.chat.application;

/**
 * Class ${FILE_NAME}
 * <p>
 * Created by lzan13 on 2015/9/10 22:04.
 */
public class MLConstants {

    // GCM number
    public static final String ML_GCM_NUMBER = "163141467698";

    // 集成小米推送需要的 appid 和 appkey
    public static final String ML_MI_APP_ID = "2882303761517493144";
    public static final String ML_MI_APP_KEY = "5601749395144";

    // 华为推送 APPID
    public static final String ML_HUAWEI_APP_ID = "10598250";

    /**
     * 设置自己扩展的 key，包括会话对象{@link com.hyphenate.chat.EMConversation}扩展，
     * 以及消息{@link com.hyphenate.chat.EMMessage}扩展
     */
    // at(@)
    public static final String ML_ATTR_AT = "ml_attr_at";
    // 是不是申请与通知类型
    public static final String ML_ATTR_APPLY_FOR = "ml_attr_apply_for";
    // 阅后即焚
    public static final String ML_ATTR_BURN = "ml_attr_burn";
    // 视频通话扩展
    public static final String ML_ATTR_CALL_VIDEO = "ml_attr_call_video";
    // 语音通话扩展
    public static final String ML_ATTR_CALL_VOICE = "ml_attr_call_voice";
    // 草稿
    public static final String ML_ATTR_DRAFT = "ml_attr_draft";
    // 群组id
    public static final String ML_ATTR_GROUP_ID = "ml_attr_group_id";
    // 最后时间
    public static final String ML_ATTR_LAST_TIME = "ml_attr_list_time";
    // 消息id
    public static final String ML_ATTR_MSG_ID = "ml_attr_msg_id";
    // 置顶
    public static final String ML_ATTR_PUSHPIN = "ml_attr_pushpin";
    // 理由
    public static final String ML_ATTR_REASON = "ml_attr_reason";
    // 撤回
    public static final String ML_ATTR_RECALL = "ml_attr_recall";
    // 状态
    public static final String ML_ATTR_STATUS = "ml_attr_status";
    // 类型
    public static final String ML_ATTR_TYPE = "ml_attr_type";
    // 会话未读
    public static final String ML_ATTR_UNREAD = "ml_attr_unread";
    // 用户名
    public static final String ML_ATTR_USERNAME = "ml_attr_username";

    /**
     * 自定义一些错误码，表示一些固定的错误
     */
    // 撤回消息错误码，超过时间限制
    public static final int ML_ERROR_I_RECALL_TIME = 5001;
    // 撤回消息错误文字描述
    public static final String ML_ERROR_S_RECALL_TIME = "ml_max_time";
    // 消息允许撤回时间
    public static final int ML_TIME_RECALL = 300000;

    // Intent 传递参数参数的 key
    public static final String ML_EXTRA_CALL_IS_INCOMING = "ml_call_is_incoming";
    public static final String ML_EXTRA_CHAT_ID = "ml_chat_id";
    public static final String ML_EXTRA_FROM = "from";
    public static final String ML_EXTRA_TO = "to";
    public static final String ML_EXTRA_TYPE = "type";


    // 定义好友申请与通知的 Conversation Id
    public static final String ML_CONVERSATION_ID_APPLY_FOR = "ml_conversation_id_apply_for";

    /**
     * 保存数据到 {@link android.content.SharedPreferences}的 key
     */
    public static final String ML_SHARED_USERNAME = "ml_username";
    public static final String ML_SHARED_PASSWORD = "ml_password";


    /**
     * 通话结束状态码
     */
    // 正常结束通话
    public static final int ML_CALL_NORMAL = 0x00;
    // 自己取消通话
    public static final int ML_CALL_CANCEL = 0x01;
    // 对方取消通话
    public static final int ML_CALL_CANCEL_IS_INCOMING = 0x02;
    // 对方在忙
    public static final int ML_CALL_BUSY = 0x03;
    // 对方不在线
    public static final int ML_CALL_OFFLINE = 0x04;
    // 对方拒绝自己的通话申请
    public static final int ML_CALL_REFUESD = 0x05;
    // 自己拒绝打来的通话
    public static final int ML_CALL_REFUESD_IS_INCOMING = 0x06;
    // 对方未接听
    public static final int ML_CALL_NORESPONSE = 0x07;
    // 建立连接失败
    public static final int ML_CALL_TRANSPORT = 0x08;
    // 双方通讯版本不同
    public static final int ML_CALL_VERSION_DIFFERENT = 0x09;

    /**
     * 链接状态码
     */
    public static final int ML_CONNECTION_USER_LOGIN_OTHER_DIVERS = 0x00;
    public static final int ML_CONNECTION_USER_REMOVED = 0x01;
    public static final int ML_CONNECTION_CONNECTED = 0x02;
    public static final int ML_CONNECTION_DISCONNECTED = 0x03;


    /**
     * 当界面跳转需要返回结果时，定义跳转请求码
     */
    public static final int ML_REQUEST_CODE_CAMERA = 0x01;
    public static final int ML_REQUEST_CODE_GALLERY = 0x02;
    public static final int ML_REQUEST_CODE_VIDEO = 0x03;
    public static final int ML_REQUEST_CODE_FILE = 0x04;
    public static final int ML_REQUEST_CODE_LOCATION = 0x05;
    public static final int ML_REQUEST_CODE_GIFT = 0x06;
    public static final int ML_REQUEST_CODE_CONTACTS = 0x07;

    /**
     * 自定义聊天界面消息列表项的点击与长按 Action
     */
    public static final int ML_ACTION_MSG_CLICK = 0X00;
    public static final int ML_ACTION_MSG_RESEND = 0X01;
    public static final int ML_ACTION_MSG_COPY = 0X10;
    public static final int ML_ACTION_MSG_FORWARD = 0X11;
    public static final int ML_ACTION_MSG_DELETE = 0X12;
    public static final int ML_ACTION_MSG_RECALL = 0X13;

    /**
     * 自定义申请与通知列表项点击与长按的 Action
     */
    public static final int ML_ACTION_APPLY_FOR_CLICK = 0X00;
    public static final int ML_ACTION_APPLY_FOR_AGREE = 0X10;
    public static final int ML_ACTION_APPLY_FOR_REFUSE = 0X11;
    public static final int ML_ACTION_APPLY_FOR_DELETE = 0X12;
    // 申请与请求的状态
    public static final int ML_STATUS_AGREED = 0x00;         // 同意
    public static final int ML_STATUS_REFUSED = 0x01;        // 拒绝
    public static final int ML_STATUS_BE_AGREED = 0x02;      // 对方同意
    public static final int ML_STATUS_BE_REFUSED = 0x03;     // 对方拒绝
    public static final int ML_STATUS_APPLY_FOR = 0x04;      // 自己申请
    public static final int ML_STATUS_BE_APPLY_FOR = 0x05;   // 对方申请
    // 申请与通知类型
    public static final int ML_APPLY_FOR_CONTACTS = 0x00;   // 联系人申请
    public static final int ML_APPLY_FOR_GROUP = 0x01;      // 群组申请

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
     * 首先是SDK支持的正常的消息类型，紧接着是扩展类型
     */
    public static final int MSG_TYPE_TEXT_SEND = 0x00;
    public static final int MSG_TYPE_TEXT_RECEIVED = 0x01;
    public static final int MSG_TYPE_IMAGE_SEND = 0x02;
    public static final int MSG_TYPE_IMAGE_RECEIVED = 0x03;
    public static final int MSG_TYPE_FILE_SEND = 0x04;
    public static final int MSG_TYPE_FILE_RECEIVED = 0x05;
    public static final int MSG_TYPE_VIDEO_SEND = 0x06;
    public static final int MSG_TYPE_VIDEO_RECEIVED = 0x07;
    public static final int MSG_TYPE_VOICE_SEND = 0x08;
    public static final int MSG_TYPE_VOICE_RECEIVED = 0x09;
    public static final int MSG_TYPE_LOCATION_SEND = 0x0A;
    public static final int MSG_TYPE_LOCATION_RECEIVED = 0x0B;
    // 撤回类型消息
    public static final int MSG_TYPE_SYS_RECALL = 0x10;
    // 通话类型消息
    public static final int MSG_TYPE_CALL_SEND = 0x11;
    public static final int MSG_TYPE_CALL_RECEIVED = 0x12;
    // 名片消息
    public static final int MSG_TYPE_CARD_SEND = 0x13;
    public static final int MSG_TYPE_CARD_RECEIVED = 0x14;
    // 礼物消息
    public static final int MSG_TYPE_GIFT_SEND = 0x15;
    public static final int MSG_TYPE_GIFT_RECEIVED = 0x16;

}
