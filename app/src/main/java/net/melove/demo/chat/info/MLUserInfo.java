package net.melove.demo.chat.info;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLUserInfo {

    public String header;
    public String userName;
    public String nickName;
    public String email;
    public String avatar;
    public String cover;
    public int gender;
    public String location;
    public String signature;
    public String createAt;
    public String updateAt;
    public String time;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return nickName == null ? userName : nickName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o instanceof MLUserInfo) {
            return false;
        }
        return userName.equals(((MLUserInfo) o).getUserName());
    }

    @Override
    public int hashCode() {
        return 17 * userName.hashCode();
    }
}
