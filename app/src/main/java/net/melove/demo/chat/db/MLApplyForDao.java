package net.melove.demo.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import net.melove.demo.chat.entity.MLApplyForEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLApplyForDao {


    public MLApplyForDao(Context context) {
        MLDBManager.getInstance().init(context);
    }


    /**
     * 保存一条申请与通知消息
     *
     * @param applyForInfo
     */
    public synchronized void saveApplyFor(MLApplyForEntity applyForInfo) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_OBJ_ID, applyForInfo.getObjId());
        values.put(MLDBConstants.COL_USERNAME, applyForInfo.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, applyForInfo.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, applyForInfo.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, applyForInfo.getGroupName());
        values.put(MLDBConstants.COL_REASON, applyForInfo.getReason());
        values.put(MLDBConstants.COL_STATUS, applyForInfo.getStatus().ordinal());
        values.put(MLDBConstants.COL_TYPE, applyForInfo.getType());
        values.put(MLDBConstants.COL_TIME, applyForInfo.getTime());

        MLDBManager.getInstance().insterData(MLDBConstants.TB_APPLY_FOR, values);
    }

    /**
     * 删除一条申请与通知
     *
     * @param objId
     */
    public synchronized void deleteApplyFor(String objId) {
        MLDBManager.getInstance().delete(
                MLDBConstants.TB_APPLY_FOR,
                MLDBConstants.COL_OBJ_ID,
                new String[]{objId});
    }

    /**
     * 更新一条申请与通知消息
     *
     * @param applyForEntity
     */
    public synchronized void updateApplyFor(MLApplyForEntity applyForEntity) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_OBJ_ID, applyForEntity.getObjId());
        values.put(MLDBConstants.COL_USERNAME, applyForEntity.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, applyForEntity.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, applyForEntity.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, applyForEntity.getGroupName());
        values.put(MLDBConstants.COL_REASON, applyForEntity.getReason());
        values.put(MLDBConstants.COL_STATUS, applyForEntity.getStatus().ordinal());
        values.put(MLDBConstants.COL_TYPE, applyForEntity.getType());
        values.put(MLDBConstants.COL_TIME, applyForEntity.getTime());

        MLDBManager.getInstance().updateData(
                MLDBConstants.TB_APPLY_FOR, values,
                MLDBConstants.COL_OBJ_ID,
                new String[]{String.valueOf(applyForEntity.getObjId())});
    }


    /**
     * 根据 objId 获取一条申请的详情
     *
     * @param objId 查询条件
     * @return 返回一个 MLApplyForEntity实体类对象
     */
    public synchronized MLApplyForEntity getApplyForEntiry(String objId) {
        String selection = MLDBConstants.COL_OBJ_ID + "=?";
        String args[] = new String[]{objId};
        Cursor cursor = MLDBManager.getInstance().queryData(
                MLDBConstants.TB_APPLY_FOR, new String[]{MLDBConstants.COL_OBJ_ID},
                selection, args, null, null, null, null);
        MLApplyForEntity applyForEntity = null;
        if (cursor.moveToNext()) {
            applyForEntity = new MLApplyForEntity();
            String id = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_OBJ_ID));
            String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USERNAME));
            String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
            String groupId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_ID));
            String groupName = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_NAME));
            String reason = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_REASON));
            int status = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_STATUS));
            int type = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_TYPE));
            String time = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_TIME));

            applyForEntity.setObjId(id);
            applyForEntity.setUserName(username);
            applyForEntity.setNickName(nickname);
            applyForEntity.setGroupId(groupId);
            applyForEntity.setGroupName(groupName);
            applyForEntity.setReason(reason);
            if (status == MLApplyForEntity.ApplyForStatus.AGREED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.AGREED);
            } else if (status == MLApplyForEntity.ApplyForStatus.REFUSED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.REFUSED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEAGREED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEAGREED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEREFUSED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEREFUSED);
            } else if (status == MLApplyForEntity.ApplyForStatus.APPLYFOR.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.APPLYFOR);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEAPPLYFOR.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEAPPLYFOR);
            } else if (status == MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR);
            }
            applyForEntity.setType(type);
            applyForEntity.setTime(Long.valueOf(time));
        }
        return applyForEntity;
    }

    /**
     * 获取申请信息的集合
     *
     * @return
     */
    public synchronized List<MLApplyForEntity> getApplyForList() {
        Cursor cursor = MLDBManager.getInstance().queryData(
                MLDBConstants.TB_APPLY_FOR, null, null, null, null, null, null, null);

        List<MLApplyForEntity> applyForEntityList = new ArrayList<MLApplyForEntity>();
        while (cursor.moveToNext()) {
            MLApplyForEntity applyForEntity = new MLApplyForEntity();
            String objId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_OBJ_ID));
            String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USERNAME));
            String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
            String groupId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_ID));
            String groupName = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_NAME));
            String reason = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_REASON));
            int status = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_STATUS));
            int type = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_TYPE));
            String time = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_TIME));

            applyForEntity.setObjId(objId);
            applyForEntity.setUserName(username);
            applyForEntity.setNickName(nickname);
            applyForEntity.setGroupId(groupId);
            applyForEntity.setGroupName(groupName);
            applyForEntity.setReason(reason);
            if (status == MLApplyForEntity.ApplyForStatus.AGREED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.AGREED);
            } else if (status == MLApplyForEntity.ApplyForStatus.REFUSED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.REFUSED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEAGREED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEAGREED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEREFUSED.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEREFUSED);
            } else if (status == MLApplyForEntity.ApplyForStatus.APPLYFOR.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.APPLYFOR);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEAPPLYFOR.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEAPPLYFOR);
            } else if (status == MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR.ordinal()) {
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR);
            }
            applyForEntity.setType(type);
            applyForEntity.setTime(Long.valueOf(time));

            applyForEntityList.add(applyForEntity);
        }
        return applyForEntityList;
    }

}
