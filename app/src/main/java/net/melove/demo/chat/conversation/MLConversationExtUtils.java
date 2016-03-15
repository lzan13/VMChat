package net.melove.demo.chat.conversation;


import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;

import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLDate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2015/12/14 15:27.
 * 会话扩展处理类，用来处理会话对象的扩展信息，比如群组@标志，会话置顶
 */
public class MLConversationExtUtils {

    /**
     * 会话实体类构造函数，根据传入的会话对象去
     *
     * @param conversation
     */
    public MLConversationExtUtils(EMConversation conversation) {

    }

    /**
     * 获取当前会话是否置顶
     *
     * @param conversation 需要操作的会话对象
     * @return 返回当前会话是否置顶
     */
    public static boolean getConversationTop(EMConversation conversation) {
        String ext = conversation.getExtField();
        if (TextUtils.isEmpty(ext)) {
            return false;
        }
        try {
            // 根据扩展获取Json对象，然后获取置顶的属性，
            JSONObject jsonObject = new JSONObject(ext);
            return jsonObject.optBoolean(MLConstants.ML_ATTR_TOP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置会话置顶状态
     *
     * @param conversation 要置顶的会话对象
     * @param top          设置会话是否置顶
     */
    public static void setConversationTop(EMConversation conversation, boolean top) {
        String ext = conversation.getExtField();
        JSONObject jsonObject = null;
        try {
            if (TextUtils.isEmpty(ext)) {
                jsonObject = new JSONObject();
            } else {
                jsonObject = new JSONObject(ext);
            }
            jsonObject.put(MLConstants.ML_ATTR_TOP, top);
            // 这里现在必须调用两次 setExtField() 方法，这应该是3.x sdk 的一个bug
            conversation.setExtField(jsonObject.toString());
            conversation.setExtField(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置会话的最后时间
     *
     * @param conversation 要设置的会话对象
     */
    public static void setConversationLastTime(EMConversation conversation) {
        String ext = conversation.getExtField();
        JSONObject jsonObject = null;
        try {
            if (TextUtils.isEmpty(ext)) {
                jsonObject = new JSONObject();
            } else {
                jsonObject = new JSONObject(ext);
            }
            // 根据当前会话消息数是否为零来设置会话的最后时间
            if (conversation.getAllMessages().size() == 0) {
                jsonObject.put(MLConstants.ML_ATTR_LAST_TIME, MLDate.getCurrentMillisecond());
            } else {
                jsonObject.put(MLConstants.ML_ATTR_LAST_TIME, conversation.getLastMessage().getMsgTime());
            }
            // 这里现在必须调用两次 setExtField() 方法，这应该是3.x sdk 的一个bug
            conversation.setExtField(jsonObject.toString());
            conversation.setExtField(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取会话的最后时间
     *
     * @param conversation 需要获取的会话对象
     * @return 返回此会话最后的时间
     */
    public static long getConversationLastTime(EMConversation conversation) {
        String ext = conversation.getExtField();
        JSONObject jsonObject = null;
        try {
            if (TextUtils.isEmpty(ext)) {
                jsonObject = new JSONObject();
            } else {
                jsonObject = new JSONObject(ext);
            }
            return jsonObject.optLong(MLConstants.ML_ATTR_LAST_TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MLDate.getCurrentMillisecond();
    }
}
