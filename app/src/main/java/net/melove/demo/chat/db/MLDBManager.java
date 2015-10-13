package net.melove.demo.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by lzan13 on 2015/7/21.
 */
public class MLDBManager {

    private static MLDBManager instance;
    private MLDBHelper mDBHelper;

    private MLDBManager() {

    }

    public static MLDBManager getInstance() {
        if (instance == null) {
            instance = new MLDBManager();
        }
        return instance;
    }

    public void onInit(Context context) {
        mDBHelper = MLDBHelper.getInstance(context);
    }

/**
 * ------------- 数据库的增删改查操作 ----------------
 */
    /**
     * sql语句方式插入数据
     * sqlStr = "insert into table(col1, col2, ... coln) values('value1', 'value2', ... valuen)";
     * or
     * sqlStr = "insert into table(col1, col2, ... coln) values(?, ?, ... ?)"
     * args = {value1, value2, ... valuen};
     * mDB.execSQL(sqlStr, args);
     *
     * @param sqlStr 进行操作的sql语句
     */
    public void insterData(String sqlStr) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL(sqlStr);
        }
    }

    /**
     * 插入数据 第二种方式
     * 使用ContentValues
     * ContentValues values = new ContentValues();
     * values.put("col1", "value1");
     * values.put("col2", "value2");
     *
     * @param table  要插入的表
     * @param values 需要插入的值
     */
    public long insterData(String table, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            long result = db.insert(table, null, values);
            return result;
        }
        return -1;
    }

    /**
     * 删除数据 1.通过执行sql语句
     * sqlStr = "delete from table where id=?";
     * args = {"1", "3"};
     *
     * @param sqlStr 进行操作的sql语句
     * @param args   替换sql语句中的值
     */
    public void delete(String sqlStr, String[] args) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL(sqlStr, args);
        }
    }

    /**
     * 删除数据 2.通过封装好的方法
     * whereClause = "id=?";(null 删除所有内容)
     * args = {"1", 3};
     *
     * @param table       要删除的数据所在的表
     * @param whereClause 要删除的过滤条件，如果为null则删除整个表的内容
     * @param args        要删除的数据条件值
     *                    return result      返回影响的行数
     */
    public long delete(String table, String whereClause, String[] args) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            long result = db.delete(table, whereClause, args);
            return result;
        }
        return -1;
    }

    /**
     * 修改数据 1.通过执行sql语句修改
     * sqlStr = "update table set col1='value' where id=1";
     *
     * @param sqlStr 进行操作的sql语句
     */
    public void updateData(String sqlStr) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL(sqlStr);
        }
    }

    /**
     * 修改数据 2.通过使用ContentValues
     * values = new ContentValues();
     * values.put("col1", "value1");
     * whereClause = "id=1";
     * args = "1";
     *
     * @param table       修改数据所在的表
     * @param values      要修改的值
     * @param whereClause 需要修改的数据查询条件
     * @param args        修改数据查询条件的值
     * @return 返回影响的行
     */
    public long updateData(String table, ContentValues values, String whereClause, String[] args) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            long result = db.update(table, values, whereClause, args);
            return result;
        }
        return -1;
    }

    /**
     * 查询数据 1.执行原生的select查询语句
     * sqlStr = "select from table";
     *
     * @param sqlStr 执行查询的sql语句
     * @return cursor 返回查询到的游标，通过游标根据列名获取数据
     */
    public Cursor queryData(String sqlStr) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(sqlStr, null);
            return cursor;
        }
        return null;
    }

    /**
     * 查询数据 2.通过goole封装的query方法查询
     *
     * @param table     表名
     * @param columns   指定查询的列，如果不指定则查询所有
     * @param selection 查询过滤条件
     * @param args      查询过滤条件对应的值（必须是数组）
     * @param groupBy   分组依据
     * @param having    分组后的过滤条件
     * @param orderBy   排序方式
     * @param limit     分页方式
     * @return 返回查询到的游标，通过游标根据列名获取数据
     */
    public Cursor queryData(String table, String[] columns, String selection, String[] args,
                            String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.query(table, columns, selection, args, groupBy, having, orderBy, limit);
            return cursor;
        }
        return null;
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (mDBHelper != null) {
            mDBHelper.closeDB();
        }
    }


}
