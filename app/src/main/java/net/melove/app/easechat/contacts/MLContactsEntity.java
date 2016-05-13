package net.melove.app.easechat.contacts;

/**
 * Created by lzan13 on 2015/8/21.
 * 联系人信息实体类
 */
public class MLContactsEntity {

    // 联系人对象的头，用来排序和根据字母查找
    public String header;
    // 联系人的username
    public String userName;
    // 联系人的昵称
    public String nickName;
    // 联系人的email
    public String email;
    // 联系人头像
    public String avatar;
    // 联系人封面
    public String cover;
    // 联系人性别
    public int gender;
    // 联系人位置
    public String location;
    // 联系人签名
    public String signature;
    // 联系人创建时间
    public String createAt;
    // 联系人更新时间
    public String updateAt;

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
        if (o == null || o instanceof MLContactsEntity) {
            return false;
        }
        return userName.equals(((MLContactsEntity) o).getUserName());
    }

    @Override
    public int hashCode() {
        return 17 * userName.hashCode();
    }
}
