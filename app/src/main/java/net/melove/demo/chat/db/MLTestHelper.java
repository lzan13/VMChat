package net.melove.demo.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lzan13 on 2015/8/5.
 */
public class MLTestHelper extends SQLiteOpenHelper {

    private static String dbName = "lz12_emmsg.db";

    private static MLTestHelper instance;

    private MLTestHelper(Context context) {
        super(context, dbName, null, 12);
    }


    public static MLTestHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MLTestHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
