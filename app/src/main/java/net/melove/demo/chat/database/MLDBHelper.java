package net.melove.demo.chat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.melove.demo.chat.common.util.MLSPUtil;


/**
 * Created by Administrator on 2014/12/18.
 */
public class MLDBHelper extends SQLiteOpenHelper {

    private static String db_name = "_chat.db";
    private static int db_version = 1;

    private static MLDBHelper instance;


    /**
     * 单例模式获取 获取数据库操作类实例
     *
     * @return
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
        db.execSQL(MLDBConstants.SQL_INVITED);
        db.execSQL(MLDBConstants.SQL_GROUP);
        db.execSQL(MLDBConstants.SQL_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDBName(Context context) {
        String username = (String) MLSPUtil.get(context, "username", "");
        return username + db_name;
    }


    public void closeDB() {
        if (instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
            instance = null;
        }
    }

}
