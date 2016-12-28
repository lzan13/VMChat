package net.melove.app.chat.contacts;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import java.util.List;
import java.util.Map;
import net.melove.app.chat.app.MLConstants;
import net.melove.app.chat.database.MLUserDao;
import net.melove.app.chat.network.MLNetworkManager;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.util.MLSPUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2016/12/3.
 * 用户信息管理类，处理联系人和陌生人的数据同步，增删改查等
 */
public class MLUserManager {

    // 私有实例对象
    private static MLUserManager instance;
    // 内存中的联系人集合，防止每次都去 db 读取联系人
    private Map<String, MLUserEntity> contactsMap = null;
    // 内存中的陌生人集合，作用同上
    private Map<String, MLUserEntity> strangerMap = null;

    /**
     * 私有构造方法
     */
    private MLUserManager() {
    }

    /**
     * 获取单例类实例
     */
    public static MLUserManager getInstance() {
        if (instance == null) {
            instance = new MLUserManager();
        }
        return instance;
    }

    /**
     * 保存一个用户，保存在内存和本地
     *
     * @param user 需要添加的用户实体对象
     */
    public void saveUser(MLUserEntity user) {
        if (contactsMap != null) {
            contactsMap.put(user.getUserName(), user);
        }
        MLUserDao.getInstance().saveUser(user);
    }

    /**
     * 删除一个用户，删除内存和本地
     *
     * @param username 需要删除的用户 username
     */
    public void deleteUser(String username) {
        if (contactsMap != null) {
            contactsMap.remove(username);
        }
        MLUserDao.getInstance().deleteUser(username);
    }

    /**
     * 修改一个用户信息
     *
     * @param userEntity 需要修改的用户实体对象
     */
    public void updateUser(MLUserEntity userEntity) {
        if (contactsMap != null) {
            contactsMap.put(userEntity.getUserName(), userEntity);
        }
        MLUserDao.getInstance().updateContacter(userEntity);
    }

    /***
     * 根据 username 获取用户信息
     *
     * @param username 需要获取的用户 username
     * @return 返回获取到的用户实体类
     */
    public MLUserEntity getUser(String username) {
        MLUserEntity user = null;
        if (contactsMap != null) {
            user = contactsMap.get(username);
        }
        if (user == null && strangerMap != null) {
            user = strangerMap.get(username);
        }
        if (user == null) {
            user = MLUserDao.getInstance().getContacter(username);
        }
        return user;
    }

    /**
     * 获取联系人集合，这个优先获取内存中的，如果内存为空然后读取数据库
     */
    public Map<String, MLUserEntity> getContactsMap() {
        if (contactsMap == null) {
            contactsMap = MLUserDao.getInstance().getContactsMap();
        }
        return contactsMap;
    }

    /**
     * 同步用户联系人到本地
     */
    public void syncContactsFromServer() {
        String accessToken = (String) MLSPUtil.get("access_token", "");
        try {
            List<String> list = EMClient.getInstance().contactManager().getAllContactsFromServer();
            String[] usernames = (String[]) list.toArray();
            String names = usernames.toString();
            JSONObject result = MLNetworkManager.getInstance().syncFriendsByNames(names, accessToken);


            MLLog.d("同步联系人信息完成");
        } catch (HyphenateException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
