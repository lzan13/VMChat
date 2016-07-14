package net.melove.app.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.melove.app.chat.application.MLApplication;
import net.melove.app.chat.invited.MLInvitedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/21.
 * 邀请信息数据库操作类
 */
public class MLInvitedDao {

    private static MLInvitedDao instance;

    /**
     * 构造方法，初始化数据库操作类
     */
    private MLInvitedDao() {
        Context context = MLApplication.getContext();
        MLDBManager.getInstance().init(context);
    }

    /**
     * 获取单例对象
     *
     * @return 返回当前类的实例
     */
    public static MLInvitedDao getInstance() {
        if (instance == null) {
            instance = new MLInvitedDao();
        }
        return instance;
    }


    /**
     * 保存一条申请与通知消息
     *
     * @param invitedEntity 需要保存的实体类
     */
    public synchronized void saveInvited(MLInvitedEntity invitedEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_ID, invitedEntity.getInvitedId());
        values.put(MLDBConstants.COL_USERNAME, invitedEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, invitedEntity.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, invitedEntity.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, invitedEntity.getGroupName());
        values.put(MLDBConstants.COL_REASON, invitedEntity.getReason());
        values.put(MLDBConstants.COL_STATUS, invitedEntity.getStatus().ordinal());
        values.put(MLDBConstants.COL_TYPE, invitedEntity.getType().ordinal());
        values.put(MLDBConstants.COL_TIME, invitedEntity.getTime());

        MLDBManager.getInstance().insterData(MLDBConstants.TB_INVITED, values);
    }

    /**
     * 删除一条申请与通知
     *
     * @param invitedId 需要删除的记录的id
     */
    public synchronized void deleteInvited(String invitedId) {
        MLDBManager.getInstance().delete(
                MLDBConstants.TB_INVITED,
                MLDBConstants.COL_ID,
                new String[]{invitedId});
    }

    /**
     * 更新一条申请与通知消息
     *
     * @param invitedEntity 需要更新的的邀请信息实体类对象
     */
    public synchronized void updateInvited(MLInvitedEntity invitedEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_ID, invitedEntity.getInvitedId());
        values.put(MLDBConstants.COL_USERNAME, invitedEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, invitedEntity.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, invitedEntity.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, invitedEntity.getGroupName());
        values.put(MLDBConstants.COL_REASON, invitedEntity.getReason());
        values.put(MLDBConstants.COL_STATUS, invitedEntity.getStatus().ordinal());
        values.put(MLDBConstants.COL_TYPE, invitedEntity.getType().ordinal());
        values.put(MLDBConstants.COL_TIME, invitedEntity.getTime());

        String whereClause = MLDBConstants.COL_ID + "=?";
        // 调用DB的更新方法
        MLDBManager.getInstance().updateData(
                MLDBConstants.TB_INVITED, values,
                whereClause,
                new String[]{String.valueOf(invitedEntity.getInvitedId())});
    }


    /**
     * 根据 invitedId 获取一条申请的详情
     *
     * @param invitedId 查询条件
     * @return 返回一个 MLInvitedEntity 实体类对象
     */
    public synchronized MLInvitedEntity getInvitedEntiry(String invitedId) {
        String selection = MLDBConstants.COL_ID + "=?";
        String args[] = new String[]{invitedId};
        Cursor cursor = MLDBManager.getInstance().queryData(
                MLDBConstants.TB_INVITED, null,
                selection, args, null, null, null, null);
        MLInvitedEntity invitedEntity = null;
        if (cursor.moveToNext()) {
            invitedEntity = cursorToEntity(cursor);
        }
        return invitedEntity;
    }

    /**
     * 获取申请信息的集合
     *
     * @return 返回查询到的集合
     */
    public synchronized List<MLInvitedEntity> getInvitedList() {
        Cursor cursor = MLDBManager.getInstance().queryData(
                MLDBConstants.TB_INVITED, null, null, null, null, null, null, null);

        List<MLInvitedEntity> invitedEntities = new ArrayList<MLInvitedEntity>();
        while (cursor.moveToNext()) {
            invitedEntities.add(cursorToEntity(cursor));
        }
        // 返回申请消息集合
        return invitedEntities;
    }

    /**
     * 根据指针获取对应的值
     *
     * @param cursor 指针
     * @return 返回得到的实体类
     */
    private MLInvitedEntity cursorToEntity(Cursor cursor) {
        MLInvitedEntity invitedEntity = new MLInvitedEntity();
        String invitedId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_ID));
        String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USERNAME));
        String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
        String groupId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_ID));
        String groupName = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_NAME));
        String reason = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_REASON));
        int status = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_STATUS));
        int type = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_TYPE));
        String time = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_TIME));

        invitedEntity.setInvitedId(invitedId);
        invitedEntity.setUserName(username);
        invitedEntity.setNickName(nickname);
        invitedEntity.setGroupId(groupId);
        invitedEntity.setGroupName(groupName);
        invitedEntity.setReason(reason);
        if (status == MLInvitedEntity.InvitedStatus.AGREED.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
        } else if (status == MLInvitedEntity.InvitedStatus.REFUSED.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
        } else if (status == MLInvitedEntity.InvitedStatus.BEAGREED.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEAGREED);
        } else if (status == MLInvitedEntity.InvitedStatus.BEREFUSED.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEREFUSED);
        } else if (status == MLInvitedEntity.InvitedStatus.APPLYFOR.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.APPLYFOR);
        } else if (status == MLInvitedEntity.InvitedStatus.BEAPPLYFOR.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.BEAPPLYFOR);
        } else if (status == MLInvitedEntity.InvitedStatus.GROUPAPPLYFOR.ordinal()) {
            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.GROUPAPPLYFOR);
        }
        // 设置申请类型，有好友申请，和群组申请两种
        if (type == MLInvitedEntity.InvitedType.GROUP.ordinal()) {
            invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
        } else if (type == MLInvitedEntity.InvitedType.CONTACTS.ordinal()) {
            invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
        }
        // 设置当前信息时间，用于排序
        invitedEntity.setTime(Long.valueOf(time));
        // 返回包含数据的实体类对象
        return invitedEntity;
    }

}
