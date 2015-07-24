package net.melove.demo.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.easemob.util.HanziToPinyin;

import net.melove.demo.chat.info.MLUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLUserDao {


    public MLUserDao(Context context) {
        MLDBManager.getInstance().onInit(context);
    }

    public List<MLUserInfo> getUserList() {
        List<MLUserInfo> list = null;

        return list;
    }


    /**
     * 保存一个User
     *
     * @param user
     */
    public synchronized void saveContact(MLUserInfo user) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USER_NAME, user.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, user.getNickName());
        values.put(MLDBConstants.COL_EMAIL, user.getEmail());
        values.put(MLDBConstants.COL_AVATAR, user.getAvatar());
        values.put(MLDBConstants.COL_COVER, user.getCover());
        values.put(MLDBConstants.COL_GENDER, user.getGender());
        values.put(MLDBConstants.COL_LOCATION, user.getLocation());
        values.put(MLDBConstants.COL_SIGNATURE, user.getSignature());
        values.put(MLDBConstants.COL_CREATE_AT, user.getCreateAt());
        values.put(MLDBConstants.COL_UPDATE_AT, user.getUpdateAt());
        MLDBManager.getInstance().insterData(MLDBConstants.TB_USER, values);
    }

    /**
     * 删除一个User
     *
     * @param username
     */
    public synchronized void deleteContact(String username) {
        MLDBManager.getInstance().delete(MLDBConstants.TB_USER, MLDBConstants.COL_USER_NAME, new String[]{username});
    }

    /**
     * 更新一个User信息
     *
     * @param user
     */
    public synchronized void updateContact(MLUserInfo user) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USER_NAME, user.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, user.getNickName());
        values.put(MLDBConstants.COL_EMAIL, user.getEmail());
        values.put(MLDBConstants.COL_AVATAR, user.getAvatar());
        values.put(MLDBConstants.COL_COVER, user.getCover());
        values.put(MLDBConstants.COL_GENDER, user.getGender());
        values.put(MLDBConstants.COL_LOCATION, user.getLocation());
        values.put(MLDBConstants.COL_SIGNATURE, user.getSignature());
        values.put(MLDBConstants.COL_CREATE_AT, user.getCreateAt());
        values.put(MLDBConstants.COL_UPDATE_AT, user.getUpdateAt());
        MLDBManager.getInstance().updateData(MLDBConstants.TB_USER, values, MLDBConstants.COL_USER_NAME, new String[]{user.getUserName()});

    }

    /**
     * 获取联系人列表
     *
     * @return
     */
    public synchronized Map<String, MLUserInfo> getContactList() {
        Cursor cursor = MLDBManager.getInstance().queryData(MLDBConstants.TB_USER, null, null, null, null, null, null, null);

        Map<String, MLUserInfo> users = new HashMap<String, MLUserInfo>();
        while (cursor.moveToNext()) {
            MLUserInfo user = new MLUserInfo();
            String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USER_NAME));
            String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
            String email = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_EMAIL));
            String avatar = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_AVATAR));
            String cover = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_COVER));
            int gender = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_GENDER));
            String location = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_LOCATION));
            String signature = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_SIGNATURE));
            String createAt = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_CREATE_AT));
            String updateAt = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_UPDATE_AT));

            user.setUserName(username);
            if (TextUtils.isEmpty(nickname)) {
                nickname = username;
            }
            user.setNickName(nickname);
            user.setEmail(email);
            user.setAvatar(avatar);
            user.setCover(cover);
            user.setGender(gender);
            user.setLocation(location);
            user.setSignature(signature);
            user.setCreateAt(createAt);
            user.setUpdateAt(updateAt);

            if (Character.isDigit(user.getNickName().charAt(0))) {
                user.setHeader("");
            } else {
                user.setHeader(HanziToPinyin.getInstance().get(user.getNickName().substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
                char header = user.getHeader().toLowerCase().charAt(0);
                if (header < 'a' || header > 'z') {
                    user.setHeader("#");
                }
            }
            users.put(username, user);
        }
        cursor.close();
        return users;
    }

}
