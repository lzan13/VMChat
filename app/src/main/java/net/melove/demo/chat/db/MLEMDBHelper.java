package net.melove.demo.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.melove.demo.chat.util.MLSPUtil;

/**
 * Created by lzan13 on 2015/8/5.
 */
public class MLEMDBHelper extends SQLiteOpenHelper {

    private static String db_name = "_emmsg.db";

    private static MLEMDBHelper instance;

    /**
     * 这里
     *
     * @param context
     */
    private MLEMDBHelper(Context context) {
        super(context, getDBName(context), null, 12);
    }


    public static MLEMDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MLEMDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDBName(Context context) {
        String username = (String) MLSPUtil.get(context, "username", "");
        return username + db_name;
    }
}
