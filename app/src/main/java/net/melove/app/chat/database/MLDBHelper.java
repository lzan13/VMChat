package net.melove.app.chat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.melove.app.chat.MLConstants;
import net.melove.app.chat.util.MLSPUtil;

/**
 * Created by Administrator on 2014/12/18.
 * 自定义数据库帮助类，用户操作数据库
 */
public class MLDBHelper extends SQLiteOpenHelper {

    // 项目需要创建的表名
    public static final String TB_CONTACTS = "contacts";

    // 表中需要保存的列名称
    public static final String COL_USERNAME = "username";
    public static final String COL_NICKNAME = "nickname";
    public static final String COL_EMAIL = "email";
    public static final String COL_AVATAR = "avatar";
    public static final String COL_COVER = "cover";
    public static final String COL_GENDER = "gender";
    public static final String COL_LOCATION = "location";
    public static final String COL_SIGNATURE = "signature";
    public static final String COL_CREATE_AT = "create_at";
    public static final String COL_UPDATE_AT = "update_at";
    public static final String COL_ACCESS_TOKEN = "access_token";
    // 用户状态，0 正常，1 黑名单，2 陌生人
    public static final String COL_STATUS = "status";
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_BLOCKLIST = 1;
    public static final int STATUS_STRANGER = 2;


    // 创建联系人数据表语句
    public static final String SQL_CONTACTS = "create table if not exists "
            + TB_CONTACTS + " ("
            + COL_USERNAME + " varchar(128) primary key, "
            + COL_NICKNAME + " varchar(128), "
            + COL_EMAIL + " varchar(128), "
            + COL_AVATAR + " text, "
            + COL_COVER + " text, "
            + COL_GENDER + " integer, "
            + COL_LOCATION + " text, "
            + COL_SIGNATURE + " text, "
            + COL_CREATE_AT + " integer, "
            + COL_UPDATE_AT + " integer, "
            + COL_STATUS + " integer"
            + ")";

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
     */
    private MLDBHelper(Context context) {
        super(context, getDBName(), null, db_version);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CONTACTS);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDBName() {
        String username = (String) MLSPUtil.get(MLConstants.ML_SHARED_USERNAME, "");
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
