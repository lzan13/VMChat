package net.melove.app.chat.module.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;

import java.util.HashMap;
import java.util.Map;
import net.melove.app.chat.MLApplication;
import net.melove.app.chat.ui.contacts.MLUserEntity;

import java.util.List;

/**
 * Created by lzan13 on 2015/7/21.
 * 联系人信息数据库操作类
 */
public class MLUserDao {

    private static MLUserDao instance;

    private MLUserDao() {
        Context context = MLApplication.getContext();
        MLDBManager.getInstance().init(context);
    }

    /**
     * 获取当前类的实例
     *
     * @return 返回当前类的实例
     */
    public static MLUserDao getInstance() {
        if (instance == null) {
            instance = new MLUserDao();
        }
        return instance;
    }

    /**
     * 保存用户信息到数据库
     *
     * @param userEntity 需要保存的用户
     */
    public synchronized void saveUser(MLUserEntity userEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USERNAME, userEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, userEntity.getNickName());
        values.put(MLDBConstants.COL_EMAIL, userEntity.getEmail());
        values.put(MLDBConstants.COL_AVATAR, userEntity.getAvatar());
        values.put(MLDBConstants.COL_COVER, userEntity.getCover());
        values.put(MLDBConstants.COL_GENDER, userEntity.getGender());
        values.put(MLDBConstants.COL_LOCATION, userEntity.getLocation());
        values.put(MLDBConstants.COL_SIGNATURE, userEntity.getSignature());
        values.put(MLDBConstants.COL_CREATE_AT, userEntity.getCreateAt());
        values.put(MLDBConstants.COL_UPDATE_AT, userEntity.getUpdateAt());
        MLDBManager.getInstance().insterData(MLDBConstants.TB_USER, values);
    }

    /**
     * 从数据看删除一个联系人
     *
     * @param username 根据联系人的 username 确定唯一的一个值
     */
    public synchronized void deleteUser(String username) {
        MLDBManager.getInstance()
                .delete(MLDBConstants.TB_USER, MLDBConstants.COL_USERNAME,
                        new String[] { username });
    }

    /**
     * 更新用户信息
     *
     * @param userEntity 需要修改的用户信息
     */
    public synchronized void updateUser(MLUserEntity userEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USERNAME, userEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, userEntity.getNickName());
        values.put(MLDBConstants.COL_EMAIL, userEntity.getEmail());
        values.put(MLDBConstants.COL_AVATAR, userEntity.getAvatar());
        values.put(MLDBConstants.COL_COVER, userEntity.getCover());
        values.put(MLDBConstants.COL_GENDER, userEntity.getGender());
        values.put(MLDBConstants.COL_LOCATION, userEntity.getLocation());
        values.put(MLDBConstants.COL_SIGNATURE, userEntity.getSignature());
        values.put(MLDBConstants.COL_CREATE_AT, userEntity.getCreateAt());
        values.put(MLDBConstants.COL_UPDATE_AT, userEntity.getUpdateAt());
        MLDBManager.getInstance()
                .updateData(MLDBConstants.TB_USER, values, MLDBConstants.COL_USERNAME,
                        new String[] { userEntity.getUserName() });
    }

    /**
     * 保存用户到本地，一次保存多个
     *
     * @param userList 需要保存的用户集合
     */
    public synchronized void saveUserList(List<MLUserEntity> userList) {
        for (MLUserEntity contactEntity : userList) {
            saveUser(contactEntity);
        }
    }

    /**
     * 根据 username 获取指定的用户信息
     *
     * @param username 需要查询的 username，根据此 username 确定唯一用户信息
     * @return 返回查询结果，有可能为 null
     */
    public synchronized MLUserEntity getUser(String username) {
        MLUserEntity contactEntity = null;
        String selection = MLDBConstants.COL_USERNAME + "=?";
        String args[] = new String[] { username };
        Cursor cursor = MLDBManager.getInstance()
                .queryData(MLDBConstants.TB_USER, null, selection, args, null, null, null,
                        null);
        if (cursor.moveToNext()) {
            contactEntity = cursorToEntity(cursor);
        }
        return contactEntity;
    }

    /**
     * 获取用户列表
     *
     * @return 获取所有用户
     */
    public synchronized Map<String, MLUserEntity> getUserList() {
        Map<String, MLUserEntity> userMap = new HashMap<>();
        // 查询 User 表
        Cursor cursor = MLDBManager.getInstance()
                .queryData(MLDBConstants.TB_USER, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            MLUserEntity temp = cursorToEntity(cursor);
            userMap.put(temp.getUserName(), temp);
        }
        cursor.close();
        return userMap;
    }

    /**
     * 根据指针获取对应的值
     *
     * @param cursor 指针
     * @return 返回得到的实体类
     */
    private MLUserEntity cursorToEntity(Cursor cursor) {
        MLUserEntity userEntity = new MLUserEntity();
        String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USERNAME));
        String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
        String email = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_EMAIL));
        String avatar = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_AVATAR));
        String cover = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_COVER));
        int gender = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_GENDER));
        String location = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_LOCATION));
        String signature = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_SIGNATURE));
        String createAt = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_CREATE_AT));
        String updateAt = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_UPDATE_AT));

        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        }
        userEntity.setUserName(username);
        userEntity.setNickName(nickname);
        userEntity.setEmail(email);
        userEntity.setAvatar(avatar);
        userEntity.setCover(cover);
        userEntity.setGender(gender);
        userEntity.setLocation(location);
        userEntity.setSignature(signature);
        userEntity.setCreateAt(createAt);
        userEntity.setUpdateAt(updateAt);

        // 判断是否为阿拉伯数字 设置联系人列表的header
        if (Character.isDigit(userEntity.getNickName().charAt(0))) {
            userEntity.setHeader("");
        } else {
            userEntity.setHeader(HanziToPinyin.getInstance()
                    .get(userEntity.getNickName().substring(0, 1))
                    .get(0).target.substring(0, 1).toUpperCase());
            char header = userEntity.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                userEntity.setHeader("#");
            }
        }
        return userEntity;
    }
}
