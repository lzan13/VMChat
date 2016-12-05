package net.melove.app.chat.ui.contacts;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import java.util.List;
import java.util.Map;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.module.database.MLDBConstants;
import net.melove.app.chat.module.database.MLUserDao;
import net.melove.app.chat.module.network.MLMobManager;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.util.MLSPUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2016/12/3.
 * 自定义联系人管理类，处理联系人的数据同步，增删改查等
 */
public class MLContactsManager {

    // 私有实例对象
    private static MLContactsManager instance;
    // 内存中的联系人集合，防止每次都去 db 读取联系人
    private Map<String, MLUserEntity> userMap = null;
    // 是否已经同步过联系人列表
    private boolean isAlreadySync = false;

    /**
     * 私有构造方法
     */
    private MLContactsManager() {
        isAlreadySync = (boolean) MLSPUtil.get(MLConstants.ML_SHARED_ALREADY_SYNC_CONTACTS, false);
    }

    /**
     * 获取单例类实例
     */
    public static MLContactsManager getInstance() {
        if (instance == null) {
            instance = new MLContactsManager();
        }
        return instance;
    }

    /**
     * 添加一个用户，将添加的用户保存在内存和本地
     *
     * @param userEntity 需要添加的用户实体类
     */
    public void saveUser(MLUserEntity userEntity) {
        if (userMap != null) {
            userMap.put(userEntity.getUserName(), userEntity);
        }
        MLUserDao.getInstance().saveUser(userEntity);
    }

    /**
     * 删除一个用户，删除内存和本地
     *
     * @param username 需要删除的用户 username
     */
    public void deleteUser(String username) {
        if (userMap != null) {
            userMap.remove(username);
        }
        MLUserDao.getInstance().deleteUser(username);
    }

    /**
     * 修改一个用户
     *
     * @param userEntity 需要修改的用户实体类
     */
    public void updateUser(MLUserEntity userEntity) {
        if (userMap != null) {
            userMap.put(userEntity.getUserName(), userEntity);
        }
        MLUserDao.getInstance().updateUser(userEntity);
    }

    /***
     * 根据 username 获取用户实体类对象
     *
     * @param username 需要获取的用户对象 username
     * @return 返回获取到的用户实体类
     */
    public MLUserEntity getUser(String username) {
        MLUserEntity userEntity = null;
        if (userMap != null) {
            userEntity = userMap.get(username);
        }
        if (userEntity == null) {
            userEntity = MLUserDao.getInstance().getUser(username);
        }
        return userEntity;
    }

    /**
     * 获取用户集合，这个优先获取内存中的，如果内存为空然后读取数据库
     */
    public Map<String, MLUserEntity> getUserMap() {
        if (userMap == null) {
            userMap = MLUserDao.getInstance().getUserMap();
        }
        return userMap;
    }

    /**
     * 同步联系人到本地
     */
    public void syncContactsFromServer() {
        // 判断是否已经同步过联系人列表
        if (isAlreadySync) {
            return;
        }

        try {
            List<String> list = EMClient.getInstance().contactManager().getAllContactsFromServer();
            for (int i = 0; i < list.size(); i++) {
                syncUserInfo(list.get(i));
            }
            isAlreadySync = true;
            MLSPUtil.put(MLConstants.ML_SHARED_ALREADY_SYNC_CONTACTS, isAlreadySync);
            MLLog.d("同步联系人信息完成 %b", isAlreadySync);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步用户信息保存到本地，因为这里使用的是 MobAPI 进行保存的用户数据，他们不提供查询多条接口，
     * 因此只能一条条查询，正式开发可以使用自己的服务器保存数据，一下返回全部用户信息，然后解析保存
     *
     * @param username 需要查询的用户信息
     */
    private void syncUserInfo(String username) {
        MLUserEntity user = new MLUserEntity(username);
        String result = MLMobManager.getInstance().syncGetData(username);
        try {
            JSONObject object = new JSONObject(result);
            String msg = object.optString("msg");
            if (msg.equals("success")) {
                JSONObject info = object.optJSONObject("result");
                user.setNickName(info.optString(MLDBConstants.COL_NICKNAME));
                user.setEmail(info.optString(MLDBConstants.COL_EMAIL));
                user.setGender(info.optInt(MLDBConstants.COL_GENDER));
                user.setLocation(info.optString(MLDBConstants.COL_LOCATION));
                user.setSignature(info.optString(MLDBConstants.COL_SIGNATURE));
                user.setCreateAt(info.optString(MLDBConstants.COL_CREATE_AT));
                user.setUpdateAt(info.optString(MLDBConstants.COL_UPDATE_AT));
            } else {
                MLLog.d("查询数据失败：%s", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 保存用户
        saveUser(user);
    }
}
