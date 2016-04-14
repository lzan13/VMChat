package net.melove.demo.chat.invited;

/**
 * Created by lzan13 on 2015/8/21.
 * 好友邀请与申请实体类，主要表示一条好友请求与申请、群组邀请与申请类型的消息
 */
public class MLInvitedEntity {

    // 保存申请消息时要给这条消息创建一个唯一的id，用来和其他申请消息区分
    private String objId;
    // 申请人/被申请人的username
    private String userName;
    // 申请人/被申请人的昵称
    private String nickName;
    // 申请的群组id
    private String groupId;
    // 申请的群组名称
    private String groupName;
    // 申请的理由
    private String reason;
    // 本条申请消息的状态
    private InvitedStatus status;
    // 消息类型，有群申请和好友申请两种
    private InvitedType type;
    // 消息时间
    private long time;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InvitedStatus getStatus() {
        return status;
    }

    public void setStatus(InvitedStatus status) {
        this.status = status;
    }

    public InvitedType getType() {
        return type;
    }

    public void setType(InvitedType type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public enum InvitedStatus {
        AGREED,         // 同意
        REFUSED,        // 拒绝
        BEAGREED,       // 对方同意
        BEREFUSED,      // 对方拒绝
        APPLYFOR,       // 自己申请
        BEAPPLYFOR,     // 对方申请
        GROUPAPPLYFOR   // 加群申请
    }

    public enum InvitedType {
        GROUP,      // 群组邀请
        CONTACTS     // 联系人申请
    }
}
