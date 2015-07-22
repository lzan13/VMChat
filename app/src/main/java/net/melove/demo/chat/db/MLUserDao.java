package net.melove.demo.chat.db;

import android.content.Context;

import net.melove.demo.chat.info.MLUserInfo;

import java.util.List;

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


}
