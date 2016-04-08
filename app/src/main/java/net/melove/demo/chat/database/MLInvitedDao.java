package net.melove.demo.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.melove.demo.chat.invited.MLInvitedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/21.
 * 邀请信息数据库操作类
 */
public class MLInvitedDao {

    /**
     * 构造方法，初始化数据库操作类
     *
     * @param context
     */
    public MLInvitedDao(Context context) {
        MLDBManager.getInstance().init(context);
    }


    /**
     * 保存一条申请与通知消息
     *
     * @param invitedEntity 需要保存的实体类
     */
    public synchronized void saveInvited(MLInvitedEntity invitedEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_OBJ_ID, invitedEntity.getObjId());
        values.put(MLDBConstants.COL_USERNAME, invitedEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, invitedEntity.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, invitedEntity.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, invitedEntity.getGroupName());
        values.put(MLDBConstants.COL_REASON, invitedEntity.getReason());
        values.put(MLDBConstants.COL_STATUS, invitedEntity.getStatus().ordinal());
        values.put(MLDBConstants.COL_TYPE, invitedEntity.getType().ordinal());
        values.put(MLDBConstants.COL_TIME, invitedEntity.getCreateTime());

        MLDBManager.getInstance().insterData(MLDBConstants.TB_INVITED, values);
    }

    /**
     * 删除一条申请与通知
     *
     * @param objId 需要删除的记录的id
     */
    public synchronized void deleteInvited(String objId) {
        MLDBManager.getInstance().delete(
                MLDBConstants.TB_INVITED,
                MLDBConstants.COL_OBJ_ID,
                new String[]{objId});
    }

    /**
     * 更新一条申请与通知消息
     *
     * @param invitedEntity 需要添加的邀请信息对象
     */
    public synchronized void updateInvited(MLInvitedEntity invitedEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_OBJ_ID, invitedEntity.getObjId());
        values.put(MLDBConstants.COL_USERNAME, invitedEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, invitedEntity.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, invitedEntity.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, invitedEntity.getGroupName());
        values.put(MLDBConstants.COL_REASON, invitedEntity.getReason());
        values.put(MLDBConstants.COL_STATUS, invitedEntity.getStatus().ordinal());
        values.put(MLDBConstants.COL_TYPE, invitedEntity.getType().ordinal());
        values.put(MLDBConstants.COL_TIME, invitedEntity.getCreateTime());

        String whereClause = MLDBConstants.COL_OBJ_ID + "=?";
        MLDBManager.getInstance().updateData(
                MLDBConstants.TB_INVITED, values,
                whereClause,
                new String[]{String.valueOf(invitedEntity.getObjId())});
    }


    /**
     * 根据 objId 获取一条申请的详情
     *
     * @param objId 查询条件
     * @return 返回一个 MLInvitedEntity 实体类对象
     */
    public synchronized MLInvitedEntity getInvitedEntiry(String objId) {
        String selection = MLDBConstants.COL_OBJ_ID + "=?";
        String args[] = new String[]{objId};
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
        String objId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_OBJ_ID));
        String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USERNAME));
        String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
        String groupId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_ID));
        String groupName = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_NAME));
        String reason = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_REASON));
        int status = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_STATUS));
        int type = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_TYPE));
        String time = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_TIME));

        invitedEntity.setObjId(objId);
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
        invitedEntity.setCreateTime(Long.valueOf(time));

        return invitedEntity;
    }

}
