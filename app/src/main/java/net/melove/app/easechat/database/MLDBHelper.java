package net.melove.app.easechat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.melove.app.easechat.application.MLConstants;
import net.melove.app.easechat.communal.util.MLSPUtil;


/**
 * Created by Administrator on 2014/12/18.
 * 自定义数据库帮助类，用户操作数据库
 */
public class MLDBHelper extends SQLiteOpenHelper {

    private static String db_name = "_ml_chat.db";
    private static int db_version = 1;

    private static MLDBHelper instance;


    /**
     * 单例模式获取 获取数据库操作类实例
     *
     * @return 返回当前类的单例对象
     */
    public static MLDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MLDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 私有化的构造函数
     *
     * @param context
     */
    private MLDBHelper(Context context) {
        super(context, getDBName(context), null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MLDBConstants.SQL_CONTACTS);
        db.execSQL(MLDBConstants.SQL_INVITED);
        db.execSQL(MLDBConstants.SQL_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDBName(Context context) {
        String username = (String) MLSPUtil.get(context, MLConstants.ML_SHARED_USERNAME, "");
        return username + db_name;
    }


    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
            instance = null;
        }
    }

    public void resetDBHelper() {
        instance = null;
    }

}
