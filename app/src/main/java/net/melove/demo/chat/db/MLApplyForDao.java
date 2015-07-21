package net.melove.demo.chat.db;

import android.content.Context;

/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLApplyForDao {


    public MLApplyForDao(Context context) {
        MLDBManager.getInstance().onInit(context);
    }
}
