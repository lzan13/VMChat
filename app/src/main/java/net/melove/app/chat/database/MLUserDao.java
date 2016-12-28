package net.melove.app.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;

import java.util.HashMap;
import java.util.Map;
import net.melove.app.chat.MLApplication;
import net.melove.app.chat.contacts.MLUserEntity;

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
        values.put(MLDBHelper.COL_USERNAME, userEntity.getUserName());
        values.put(MLDBHelper.COL_NICKNAME, userEntity.getNickName());
        values.put(MLDBHelper.COL_EMAIL, userEntity.getEmail());
        values.put(MLDBHelper.COL_AVATAR, userEntity.getAvatar());
        values.put(MLDBHelper.COL_COVER, userEntity.getCover());
        values.put(MLDBHelper.COL_GENDER, userEntity.getGender());
        values.put(MLDBHelper.COL_LOCATION, userEntity.getLocation());
        values.put(MLDBHelper.COL_SIGNATURE, userEntity.getSignature());
        values.put(MLDBHelper.COL_CREATE_AT, userEntity.getCreateAt());
        values.put(MLDBHelper.COL_UPDATE_AT, userEntity.getUpdateAt());
        MLDBManager.getInstance().insterData(MLDBHelper.TB_CONTACTS, values);
    }

    /**
     * 从数据看删除一个联系人
     *
     * @param username 根据联系人的 username 确定唯一的一个值
     */
    public synchronized void deleteUser(String username) {
        MLDBManager.getInstance()
                .delete(MLDBHelper.TB_CONTACTS, MLDBHelper.COL_USERNAME, new String[] { username });
    }

    /**
     * 更新联系人信息
     *
     * @param userEntity 需要修改的用户信息
     */
    public synchronized void updateContacter(MLUserEntity userEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBHelper.COL_USERNAME, userEntity.getUserName());
        values.put(MLDBHelper.COL_NICKNAME, userEntity.getNickName());
        values.put(MLDBHelper.COL_EMAIL, userEntity.getEmail());
        values.put(MLDBHelper.COL_AVATAR, userEntity.getAvatar());
        values.put(MLDBHelper.COL_COVER, userEntity.getCover());
        values.put(MLDBHelper.COL_GENDER, userEntity.getGender());
        values.put(MLDBHelper.COL_LOCATION, userEntity.getLocation());
        values.put(MLDBHelper.COL_SIGNATURE, userEntity.getSignature());
        values.put(MLDBHelper.COL_CREATE_AT, userEntity.getCreateAt());
        values.put(MLDBHelper.COL_UPDATE_AT, userEntity.getUpdateAt());
        MLDBManager.getInstance()
                .updateData(MLDBHelper.TB_CONTACTS, values, MLDBHelper.COL_USERNAME,
                        new String[] { userEntity.getUserName() });
    }

    /**
     * 保存联系人列表，一次保存多个联系人
     *
     * @param userMap 需要保存的联系人集合
     */
    public synchronized void saveContactsMap(Map<String, MLUserEntity> userMap) {
        for (MLUserEntity contactEntity : userMap.values()) {
            saveUser(contactEntity);
        }
    }

    /**
     * 根据 username 获取指定的联系人信息
     *
     * @param username 需要查询的 username，根据此 username 确定唯一用户信息
     * @return 返回查询结果，有可能为 null
     */
    public synchronized MLUserEntity getContacter(String username) {
        MLUserEntity contacter = null;
        String selection = MLDBHelper.COL_USERNAME + "=?";
        String args[] = new String[] { username };
        Cursor cursor = MLDBManager.getInstance()
                .queryData(MLDBHelper.TB_CONTACTS, null, selection, args, null, null, null, null);
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
    public synchronized Map<String, MLUserEntity> getContactsMap() {
        Map<String, MLUserEntity> contactsMap = new HashMap<>();
        String selection = MLDBHelper.COL_STATUS + "=?";
        int args[] = new int[] { MLDBHelper.STATUS_NORMAL };
        // 查询联系人表
        Cursor cursor = MLDBManager.getInstance()
                .queryData(MLDBHelper.TB_CONTACTS, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int status = cursor.getInt(cursor.getColumnIndex(MLDBHelper.COL_STATUS));
            if (status == MLDBHelper.STATUS_NORMAL) {
                MLUserEntity temp = cursorToEntity(cursor);
                contactsMap.put(temp.getUserName(), temp);
            }
        }
        cursor.close();
        return contactsMap;
    }

    /**
     * 获取陌生人集合
     */
    public synchronized Map<String, MLUserEntity> getStrangerMap() {
        Map<String, MLUserEntity> strangerMap = new HashMap<>();
        // 查询 User 表
        Cursor cursor = MLDBManager.getInstance()
                .queryData(MLDBHelper.TB_CONTACTS, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int status = cursor.getInt(cursor.getColumnIndex(MLDBHelper.COL_STATUS));
            if (status == MLDBHelper.STATUS_STRANGER) {
                MLUserEntity temp = cursorToEntity(cursor);
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
    private MLUserEntity cursorToEntity(Cursor cursor) {
        MLUserEntity user = null;
        String username = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_USERNAME));
        String nickname = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_NICKNAME));
        String email = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_EMAIL));
        String avatar = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_AVATAR));
        String cover = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_COVER));
        int gender = cursor.getInt(cursor.getColumnIndex(MLDBHelper.COL_GENDER));
        String location = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_LOCATION));
        String signature = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_SIGNATURE));
        String createAt = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_CREATE_AT));
        String updateAt = cursor.getString(cursor.getColumnIndex(MLDBHelper.COL_UPDATE_AT));

        if (TextUtils.isEmpty(nickname)) {
            nickname = username;
        }
        user = new MLUserEntity(username);
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
}
