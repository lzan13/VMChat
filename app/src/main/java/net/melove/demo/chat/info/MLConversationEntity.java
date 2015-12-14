package net.melove.demo.chat.info;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.util.MLDate;

/**
 * Created by lzan13 on 2015/12/14 15:27.
 * 自定义会话实体类，用来展示会话信息，这里没有使用环信默认的会话对象来进行展示，是为了方便对会话进行扩展，
 * 比如展示会话者的头像，昵称等，TODO 置顶操作
 */
public class MLConversationEntity {
    private String username;
    private String nick;
    private String avatar;
    private String time;
    private String content;
    private int count;
    private int type;

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
            setTime(MLDate.formatDate(message.getMsgTime()));
            setCount(conversation.getUnreadMsgCount());
        } else {
            setContent("还没有聊天内容");
            setTime("00:00");
        }
        setUsername(conversation.getUserName());
        setNick("昵称");
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
