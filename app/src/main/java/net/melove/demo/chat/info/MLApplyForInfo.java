package net.melove.demo.chat.info;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLApplyForInfo {

    public String userName;
    public String nickName;
    public String groupId;
    public String groupName;
    public String reason;
    public long time;
    public ApplyForStatus status;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApplyForStatus getStatus() {
        return status;
    }

    public void setStatus(ApplyForStatus status) {
        this.status = status;
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

    public enum ApplyForStatus {
        AGREED,         // 同意
        REFUSED,        // 据绝
        BEAGREED,       // 对方同意
        BEREFUSED,      // 对方拒绝
        BEAPPLYFOR,     // 对方申请
        GROUPAPPLYFOR   // 群组请求
    }
}
