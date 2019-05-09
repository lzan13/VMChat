package com.vmloft.develop.app.chat.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;

import com.vmloft.develop.app.chat.ui.contacts.UserEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzan13 on 2015/7/21.
 * 用户信息数据库操作类
 */
public class UserDao {

    private static UserDao instance;

    private UserDao() {
        DBManager.getInstance().init();
    }

    /**
     * 获取当前类的实例
     *
     * @return 返回当前类的实例
     */
    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    /**
     * 保存用户信息到数据库
     *
     * @param userEntity 需要保存的用户
     */
    public synchronized void saveUser(UserEntity userEntity) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USERNAME, userEntity.getUserName());
        values.put(DBHelper.COL_NICKNAME, userEntity.getNickName());
        values.put(DBHelper.COL_EMAIL, userEntity.getEmail());
        values.put(DBHelper.COL_AVATAR, userEntity.getAvatar());
        values.put(DBHelper.COL_COVER, userEntity.getCover());
        values.put(DBHelper.COL_GENDER, userEntity.getGender());
        values.put(DBHelper.COL_LOCATION, userEntity.getLocation());
        values.put(DBHelper.COL_SIGNATURE, userEntity.getSignature());
        values.put(DBHelper.COL_CREATE_AT, userEntity.getCreateAt());
        values.put(DBHelper.COL_UPDATE_AT, userEntity.getUpdateAt());
        values.put(DBHelper.COL_STATUS, userEntity.getStatus());
        DBManager.getInstance().insterData(DBHelper.TB_USERS, values);
    }

    /**
     * 保存用户集合
     *
     * @param userMap 用户集合
     */
    public synchronized void saveUserMap(Map<String, UserEntity> userMap) {
        for (UserEntity contactEntity : userMap.values()) {
            saveUser(contactEntity);
        }
    }

    /**
     * 保存用户集合
     *
     * @param userList 用户集合
     */
    public synchronized void saveUserList(List<String> userList) {
        for (String username : userList) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_USERNAME, username);
            DBManager.getInstance().insterData(DBHelper.TB_USERS, values);
        }
    }

    /**
     * 删除一个用户
     *
     * @param username 用户 username
     */
    public synchronized void deleteUser(String username) {
        DBManager.getInstance()
                .delete(DBHelper.TB_USERS, DBHelper.COL_USERNAME, new String[] { username });
    }

    /**
     * 更新用户信息
     *
     * @param userEntity 用户对象
     */
    public synchronized void updateUser(UserEntity userEntity) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_NICKNAME, userEntity.getNickName());
        values.put(DBHelper.COL_EMAIL, userEntity.getEmail());
        values.put(DBHelper.COL_AVATAR, userEntity.getAvatar());
        values.put(DBHelper.COL_COVER, userEntity.getCover());
        values.put(DBHelper.COL_GENDER, userEntity.getGender());
        values.put(DBHelper.COL_LOCATION, userEntity.getLocation());
        values.put(DBHelper.COL_SIGNATURE, userEntity.getSignature());
        values.put(DBHelper.COL_CREATE_AT, userEntity.getCreateAt());
        values.put(DBHelper.COL_UPDATE_AT, userEntity.getUpdateAt());
        values.put(DBHelper.COL_STATUS, userEntity.getStatus());

        String selection = DBHelper.COL_USERNAME + "=?";
        DBManager.getInstance()
                .updateData(DBHelper.TB_USERS, values, selection,
                        new String[] { userEntity.getUserName() });
    }

    /**
     * 根据 username 获取指定的用户信息
     *
     * @param username 需要查询的 username，根据此 username 确定唯一用户信息
     * @return 返回查询结果，有可能为 null
     */
    public synchronized UserEntity getContacter(String username) {
        UserEntity contacter = null;
        String selection = DBHelper.COL_USERNAME + "=?";
        String args[] = new String[] { username };
        Cursor cursor = DBManager.getInstance()
                .queryData(DBHelper.TB_USERS, null, selection, args, null, null, null, null);
        if (cursor.moveToNext()) {
            contacter = cursorToEntity(cursor);
        }
        return contacter;
    }

    /**
     * 获取联系人列表
     *
     * @return 获取当前登录账户所有联系人
     */
    public synchronized Map<String, UserEntity> getContactsMap() {
        Map<String, UserEntity> contactsMap = new HashMap<>();
        String selection = DBHelper.COL_STATUS + "=?";
        int args[] = new int[] { DBHelper.STATUS_NORMAL };
        // 查询联系人表
        Cursor cursor = DBManager.getInstance()
                .queryData(DBHelper.TB_USERS, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_STATUS));
            if (status == DBHelper.STATUS_NORMAL) {
                UserEntity temp = cursorToEntity(cursor);
                contactsMap.put(temp.getUserName(), temp);
            }
        }
        cursor.close();
        return contactsMap;
    }

    /**
     * 获取陌生人集合
     *
     * @return 获取除了好友以外的用户信息列表
     */
    public synchronized Map<String, UserEntity> getStrangerMap() {
        Map<String, UserEntity> strangerMap = new HashMap<>();
        // 查询 User 表
        Cursor cursor = DBManager.getInstance()
                .queryData(DBHelper.TB_USERS, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_STATUS));
            if (status == DBHelper.STATUS_STRANGER) {
                UserEntity temp = cursorToEntity(cursor);
                strangerMap.put(temp.getUserName(), temp);
            }
        }
        cursor.close();
        return strangerMap;
    }

    /**
     * 根据指针获取对应的值
     *
     * @param cursor 指针
     * @return 返回得到的实体类
     */
    private UserEntity cursorToEntity(Cursor cursor) {
        UserEntity user = null;
        String username = cursor.getString(cursor.getColumnIndex(DBHelper.COL_USERNAME));
        String nickname = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NICKNAME));
        String email = cursor.getString(cursor.getColumnIndex(DBHelper.COL_EMAIL));
        String avatar = cursor.getString(cursor.getColumnIndex(DBHelper.COL_AVATAR));
        String cover = cursor.getString(cursor.getColumnIndex(DBHelper.COL_COVER));
        int gender = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_GENDER));
        String location = cursor.getString(cursor.getColumnIndex(DBHelper.COL_LOCATION));
        String signature = cursor.getString(cursor.getColumnIndex(DBHelper.COL_SIGNATURE));
        String createAt = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CREATE_AT));
        String updateAt = cursor.getString(cursor.getColumnIndex(DBHelper.COL_UPDATE_AT));

        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        }
        user = new UserEntity(username);
        user.setNickName(nickname);
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setCover(cover);
        user.setGender(gender);
        user.setLocation(location);
        user.setSignature(signature);
        user.setCreateAt(createAt);
        user.setUpdateAt(updateAt);

        // 判断是否为阿拉伯数字 设置联系人列表的header
        if (Character.isDigit(user.getNickName().charAt(0))) {
            user.setHeader("");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(user.getNickName().substring(0, 1))
                    .get(0).target.substring(0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    /**
     * 清空表数据
     */
    public void clearTable() {
        DBManager.getInstance().clearTable(DBHelper.TB_USERS);
    }

    /**
     * 重置
     */
    public void resetUserDao() {
        if (instance != null) {
            instance = null;
        }
    }
}
