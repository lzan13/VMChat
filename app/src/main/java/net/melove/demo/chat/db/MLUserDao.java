package net.melove.demo.chat.db;

import android.content.Context;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLUserDao {


    public MLUserDao(Context context) {
        MLDBManager.getInstance().onInit(context);
    }



}
