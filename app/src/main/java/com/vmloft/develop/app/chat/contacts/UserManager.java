package com.vmloft.develop.app.chat.contacts;

import android.content.Context;
import android.text.TextUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.app.chat.base.App;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.database.DBHelper;
import java.util.List;
import java.util.Map;
import com.vmloft.develop.app.chat.database.UserDao;
import com.vmloft.develop.app.chat.network.NetworkManager;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;
import com.vmloft.develop.library.tools.utils.VMStr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2016/12/3.
 * 用户信息管理类，处理联系人和陌生人的数据同步，增删改查等
 */
public class UserManager {

    private Context context;

    // 私有实例对象
    private static UserManager instance;
    // 内存中的联系人集合，防止每次都去 db 读取联系人
    private Map<String, UserEntity> contactsMap = null;
    // 内存中的陌生人集合，作用同上
    private Map<String, UserEntity> strangerMap = null;

    /**
     * 私有构造方法
     */
    private UserManager(Context context) {
        this.context = context;
    }

    /**
     * 获取单例类实例
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager(App.getContext());
        }
        return instance;
    }

    /**
     * 保存一个用户，保存在内存和本地
     *
     * @param user 用户实体对象
     */
    public void saveUser(UserEntity user) {
        if (contactsMap != null && user.getStatus() == DBHelper.STATUS_NORMAL) {
            contactsMap.put(user.getUserName(), user);
        } else if (strangerMap != null && user.getStatus() == DBHelper.STATUS_STRANGER) {
            strangerMap.put(user.getUserName(), user);
        }
        UserDao.getInstance().saveUser(user);
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
        if (strangerMap != null) {
            strangerMap.remove(username);
        }
        UserDao.getInstance().deleteUser(username);
    }

    /**
     * 修改一个用户信息
     *
     * @param user 用户实体类对象
     */
    public void updateUser(UserEntity user) {
        if (contactsMap != null && user.getStatus() == DBHelper.STATUS_NORMAL) {
            contactsMap.put(user.getUserName(), user);
        } else if (strangerMap != null && user.getStatus() == DBHelper.STATUS_STRANGER) {
            strangerMap.put(user.getUserName(), user);
        }
        UserDao.getInstance().updateUser(user);
    }

    /***
     * 根据 username 获取用户信息
     *
     * @param username 需要获取的用户 username
     * @return 返回获取到的用户实体类
     */
    public UserEntity getUser(String username) {
        UserEntity user = null;
        if (contactsMap != null) {
            user = contactsMap.get(username);
        }
        if (user == null && strangerMap != null) {
            user = strangerMap.get(username);
        }
        if (user == null) {
            user = UserDao.getInstance().getContacter(username);
        }
        return user;
    }

    /**
     * 获取联系人集合，这个优先获取内存中的，如果内存为空然后读取数据库
     */
    public Map<String, UserEntity> getContactsMap() {
        if (contactsMap == null) {
            contactsMap = UserDao.getInstance().getContactsMap();
        }
        return contactsMap;
    }

    /**
     * 同步用户联系人到本地
     */
    public void syncContactsFromServer() {
        // 同步联系人时先将数据清空
        UserDao.getInstance().clearTable();
        String accessToken = (String) VMSPUtil.get(context, AConstants.USER_ACCESS_TOKEN, "");
        try {
            // 从环信服务器同步好友列表
            List<String> list = EMClient.getInstance().contactManager().getAllContactsFromServer();
            String[] usernames = list.toArray(new String[list.size()]);
            String names = VMStr.arrayToStr(usernames, ",");
            if (TextUtils.isEmpty(names)) {
                return;
            }
            UserDao.getInstance().saveUserList(list);
            VMLog.d("同步好友列表完成，好友总数：%d", list.size());

            // 从自己的服务器获取好友详细信息
            JSONObject result =
                    NetworkManager.getInstance().getUsersInfo(names, accessToken);
            if (result.optInt("code") != 0) {
                return;
            }
            JSONArray jsonArray = result.optJSONArray("data");
            UserEntity userEntity = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                userEntity = new UserEntity();
                userEntity.setUserName(object.optString(DBHelper.COL_USERNAME));
                userEntity.setNickName(object.optString(DBHelper.COL_NICKNAME));
                userEntity.setEmail(object.optString(DBHelper.COL_EMAIL));
                userEntity.setAvatar(object.optString(DBHelper.COL_AVATAR));
                userEntity.setCover(object.optString(DBHelper.COL_COVER));
                userEntity.setGender(object.optInt(DBHelper.COL_GENDER));
                userEntity.setLocation(object.optString(DBHelper.COL_LOCATION));
                userEntity.setSignature(object.optString(DBHelper.COL_SIGNATURE));
                userEntity.setCreateAt(object.optString(DBHelper.COL_CREATE_AT));
                userEntity.setUpdateAt(object.optString(DBHelper.COL_UPDATE_AT));
                userEntity.setStatus(0);
                UserDao.getInstance().updateUser(userEntity);
            }
            VMLog.d("获取好友详细信息完成，获取到详情数：%d", jsonArray.length());
        } catch (HyphenateException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
