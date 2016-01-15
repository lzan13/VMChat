package net.melove.demo.chat.entity;

/**
 * Created by lzan13 on 2015/7/21.
 * 好友申请实体类
 */
public class MLApplyForEntity {

    private String objId;
    private String userName;
    private String nickName;
    private String groupId;
    private String groupName;
    private String reason;
    private ApplyForStatus status;
    private int type;
    private long time;

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ApplyForStatus getStatus() {
        return status;
    }

    public void setStatus(ApplyForStatus status) {
        this.status = status;
    }

    public enum ApplyForStatus {
        AGREED,         // 同意
        REFUSED,        // 据绝
        BEAGREED,       // 对方同意
        BEREFUSED,      // 对方拒绝
        APPLYFOR,       // 自己申请
        BEAPPLYFOR,     // 对方申请
        GROUPAPPLYFOR   // 加群申请
    }
}
