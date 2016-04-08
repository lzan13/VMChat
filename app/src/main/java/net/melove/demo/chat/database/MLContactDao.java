package net.melove.demo.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.hyphenate.util.HanziToPinyin;

import net.melove.demo.chat.contacts.MLContactEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLContactDao {


    public MLContactDao(Context context) {
        MLDBManager.getInstance().init(context);
    }

    /**
     * 保存一个联系人到数据库
     *
     * @param contactEntity 需要保存的联系人
     */
    public synchronized void saveContacts(MLContactEntity contactEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USERNAME, contactEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, contactEntity.getNickName());
        values.put(MLDBConstants.COL_EMAIL, contactEntity.getEmail());
        values.put(MLDBConstants.COL_AVATAR, contactEntity.getAvatar());
        values.put(MLDBConstants.COL_COVER, contactEntity.getCover());
        values.put(MLDBConstants.COL_GENDER, contactEntity.getGender());
        values.put(MLDBConstants.COL_LOCATION, contactEntity.getLocation());
        values.put(MLDBConstants.COL_SIGNATURE, contactEntity.getSignature());
        values.put(MLDBConstants.COL_CREATE_AT, contactEntity.getCreateAt());
        values.put(MLDBConstants.COL_UPDATE_AT, contactEntity.getUpdateAt());
        MLDBManager.getInstance().insterData(MLDBConstants.TB_CONTACTS, values);
    }

    /**
     * 从数据看删除一个联系人
     *
     * @param username 根据联系人的username 确定唯一的一个值
     */
    public synchronized void deleteContacts(String username) {
        MLDBManager.getInstance().delete(
                MLDBConstants.TB_CONTACTS,
                MLDBConstants.COL_USERNAME,
                new String[]{username});
    }

    /**
     * 更新本地联系人信息
     *
     * @param contactEntity 需要修改的联系人信息
     */
    public synchronized void updateContacts(MLContactEntity contactEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USERNAME, contactEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, contactEntity.getNickName());
        values.put(MLDBConstants.COL_EMAIL, contactEntity.getEmail());
        values.put(MLDBConstants.COL_AVATAR, contactEntity.getAvatar());
        values.put(MLDBConstants.COL_COVER, contactEntity.getCover());
        values.put(MLDBConstants.COL_GENDER, contactEntity.getGender());
        values.put(MLDBConstants.COL_LOCATION, contactEntity.getLocation());
        values.put(MLDBConstants.COL_SIGNATURE, contactEntity.getSignature());
        values.put(MLDBConstants.COL_CREATE_AT, contactEntity.getCreateAt());
        values.put(MLDBConstants.COL_UPDATE_AT, contactEntity.getUpdateAt());
        MLDBManager.getInstance().updateData(
                MLDBConstants.TB_CONTACTS, values,
                MLDBConstants.COL_USERNAME,
                new String[]{contactEntity.getUserName()});

    }

    /**
     * 保存联系人集合
     *
     * @param contactEntities 需要保存的联系人列表
     */
    public synchronized void saveContactsList(List<MLContactEntity> contactEntities) {
        for (MLContactEntity contactEntity : contactEntities) {
            saveContacts(contactEntity);
        }
    }

    /**
     * lzan13 create 2015-10-13 16:14:35
     * 根据username 获取指定的用户信息
     *
     * @param username 需要获取的联系人的 username，根据此 useranme 确定唯一的联系人
     * @return 返回查询结果，有可能为 @null
     */
    public synchronized MLContactEntity getContact(String username) {
        MLContactEntity contactEntity = null;
        String selection = MLDBConstants.COL_USERNAME + "=?";
        String args[] = new String[]{username};
        Cursor cursor = MLDBManager.getInstance().queryData(MLDBConstants.TB_CONTACTS,
                null, selection, args, null, null, null, null);
        if (cursor.moveToNext()) {
            contactEntity = cursorToEntity(cursor);
        }
        return contactEntity;
    }


    /**
     * 获取联系人列表
     *
     * @return 获取所有联系人
     */
    public synchronized List<MLContactEntity> getContactList() {
        Cursor cursor = MLDBManager.getInstance().queryData(MLDBConstants.TB_CONTACTS,
                null, null, null, null, null, null, null);

        List<MLContactEntity> contactEntities = new ArrayList<MLContactEntity>();
        while (cursor.moveToNext()) {
            contactEntities.add(cursorToEntity(cursor));
        }
        cursor.close();
        return contactEntities;
    }

    /**
     * 根据指针获取对应的值
     *
     * @param cursor 指针
     * @return 返回得到的实体类
     */
    private MLContactEntity cursorToEntity(Cursor cursor) {
        MLContactEntity contactEntity = new MLContactEntity();
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
        contactEntity.setUserName(username);
        contactEntity.setNickName(nickname);
        contactEntity.setEmail(email);
        contactEntity.setAvatar(avatar);
        contactEntity.setCover(cover);
        contactEntity.setGender(gender);
        contactEntity.setLocation(location);
        contactEntity.setSignature(signature);
        contactEntity.setCreateAt(createAt);
        contactEntity.setUpdateAt(updateAt);

        // 判断是否为阿拉伯数字 设置联系人列表的header
        if (Character.isDigit(contactEntity.getNickName().charAt(0))) {
            contactEntity.setHeader("");
        } else {
            contactEntity.setHeader(HanziToPinyin.getInstance()
                    .get(contactEntity.getNickName().substring(0, 1))
                    .get(0).target.substring(0, 1).toUpperCase());
            char header = contactEntity.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                contactEntity.setHeader("#");
            }
        }
        return contactEntity;
    }

}
