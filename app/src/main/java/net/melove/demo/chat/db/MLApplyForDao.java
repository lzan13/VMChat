package net.melove.demo.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import net.melove.demo.chat.info.MLApplyForEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLApplyForDao {


    public MLApplyForDao(Context context) {
        MLDBManager.getInstance().onInit(context);
    }


    /**
     * 保存一条申请与通知消息
     *
     * @param applyForInfo
     */
    public synchronized void saveApplyFor(MLApplyForEntity applyForInfo) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USER_NAME, applyForInfo.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, applyForInfo.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, applyForInfo.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, applyForInfo.getGroupName());
        values.put(MLDBConstants.COL_REASON, applyForInfo.getReason());
        values.put(MLDBConstants.COL_STATUS, applyForInfo.getStatus().ordinal());
        values.put(MLDBConstants.COL_TIME, applyForInfo.getTime());

        MLDBManager.getInstance().insterData(MLDBConstants.TB_APPLY_FOR, values);
    }

    /**
     * 删除一条申请与通知
     *
     * @param time
     */
    public synchronized void deleteApplyFor(String time) {
        MLDBManager.getInstance().delete(
                MLDBConstants.TB_APPLY_FOR,
                MLDBConstants.COL_TIME,
                new String[]{time});
    }

    /**
     * 更新一条申请与通知消息
     *
     * @param applyForInfo
     */
    public synchronized void updateApplyFor(MLApplyForEntity applyForInfo) {
        ContentValues values = new ContentValues();
        values.put(MLDBConstants.COL_USER_NAME, applyForInfo.getUserName());
        values.put(MLDBConstants.COL_NICKNAME, applyForInfo.getNickName());
        values.put(MLDBConstants.COL_GROUP_ID, applyForInfo.getGroupId());
        values.put(MLDBConstants.COL_GROUP_NAME, applyForInfo.getGroupName());
        values.put(MLDBConstants.COL_REASON, applyForInfo.getReason());
        values.put(MLDBConstants.COL_STATUS, applyForInfo.getStatus().ordinal());

        MLDBManager.getInstance().updateData(
                MLDBConstants.TB_APPLY_FOR, values,
                MLDBConstants.COL_TIME,
                new String[]{String.valueOf(applyForInfo.getTime())});
    }


    /**
     * 获取申请与通知集合
     *
     * @return
     */
    public synchronized List<MLApplyForEntity> getApplyForList() {
        Cursor cursor = MLDBManager.getInstance().queryData(
                MLDBConstants.TB_APPLY_FOR, null, null, null, null, null, null, null);

        List<MLApplyForEntity> applyForInfos = new ArrayList<MLApplyForEntity>();
        while (cursor.moveToNext()) {
            MLApplyForEntity applyForInfo = new MLApplyForEntity();
            String username = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_USER_NAME));
            String nickname = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_NICKNAME));
            String groupId = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_ID));
            String groupName = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_GROUP_NAME));
            String reason = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_REASON));
            int status = cursor.getInt(cursor.getColumnIndex(MLDBConstants.COL_STATUS));
            String time = cursor.getString(cursor.getColumnIndex(MLDBConstants.COL_TIME));

            if (TextUtils.isEmpty(nickname)) {
                nickname = username;
            }
            applyForInfo.setUserName(username);
            applyForInfo.setNickName(nickname);
            applyForInfo.setGroupId(groupId);
            applyForInfo.setGroupName(groupName);
            applyForInfo.setReason(reason);
            if (status == MLApplyForEntity.ApplyForStatus.AGREED.ordinal()) {
                applyForInfo.setStatus(MLApplyForEntity.ApplyForStatus.AGREED);
            } else if (status == MLApplyForEntity.ApplyForStatus.REFUSED.ordinal()) {
                applyForInfo.setStatus(MLApplyForEntity.ApplyForStatus.REFUSED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEAGREED.ordinal()) {
                applyForInfo.setStatus(MLApplyForEntity.ApplyForStatus.BEAGREED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEREFUSED.ordinal()) {
                applyForInfo.setStatus(MLApplyForEntity.ApplyForStatus.BEREFUSED);
            } else if (status == MLApplyForEntity.ApplyForStatus.BEAPPLYFOR.ordinal()) {
                applyForInfo.setStatus(MLApplyForEntity.ApplyForStatus.BEAPPLYFOR);
            } else if (status == MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR.ordinal()) {
                applyForInfo.setStatus(MLApplyForEntity.ApplyForStatus.GROUPAPPLYFOR);
            }
            applyForInfo.setTime(Long.valueOf(time));
            applyForInfos.add(applyForInfo);
        }
        return applyForInfos;
    }


}
