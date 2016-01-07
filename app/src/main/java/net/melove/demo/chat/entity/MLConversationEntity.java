package net.melove.demo.chat.entity;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.util.MLDate;
import net.melove.demo.chat.util.MLLog;

/**
 * Created by lzan13 on 2015/12/14 15:27.
 * 自定义会话实体类，用来展示会话信息，这里没有使用环信默认的会话对象来进行展示，是为了方便对会话进行扩展，
 * 比如展示会话者的头像，昵称等，TODO 置顶操作
 */
public class MLConversationEntity {
    // 会话id
    private String chatId;
    // 会话的昵称
    private String nick;
    // 会话的头像
    private String avatar;
    // 最后会话时间
    private String time;
    // 最后一条消息内容
    private String content;
    // 聊天未读数
    private int count;
    // 聊天类型
    private int type;
    // 标记是否置顶
    private int top;


    /**
     * 会话实体类构造函数，根据传入的会话对象去
     *
     * @param conversation
     */
    public MLConversationEntity(EMConversation conversation) {
        if (conversation.getAllMessages().size() > 0) {
            EMMessage message = conversation.getLastMessage();
            String str = "";
            if (message.getType() == EMMessage.Type.TXT) {
                str = ((TextMessageBody) message.getBody()).getMessage();
            } else if (message.getType() == EMMessage.Type.IMAGE) {
                str = "图片";
            } else if (message.getType() == EMMessage.Type.FILE) {
                str = "文件";
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                str = "位置";
            } else if (message.getType() == EMMessage.Type.VIDEO) {
                str = "视频";
            } else if (message.getType() == EMMessage.Type.VOICE) {
                str = "语音";
            } else if (message.getType() == EMMessage.Type.CMD) {
                str = "CMD";
            }
            setContent(str);
            setTime(MLDate.long2Time(message.getMsgTime()));
            setCount(conversation.getUnreadMsgCount());
        } else {
            setContent("还没有聊天内容");
            setTime("00:00");
        }
        setChatId(conversation.getUserName());
        setNick(conversation.getUserName());
        setAvatar("");
        setType(conversation.getType().ordinal());
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
