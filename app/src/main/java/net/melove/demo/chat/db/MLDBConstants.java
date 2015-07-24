package net.melove.demo.chat.db;

/**
 * Created by Administrator on 2015/3/24.
 */
public class MLDBConstants {

//    public static final String DB_NAME = "ml_app_timeline.db";

    //table name
    public static final String TB_USER = "user";
    public static final String TB_APPLY_FOR = "apply_for";

    // column name
    public static final String COL_USER_NAME = "user_name";
    public static final String COL_NICKNAME = "nickname";
    public static final String COL_EMAIL = "email";
    public static final String COL_AVATAR = "avatar";
    public static final String COL_COVER = "cover";
    public static final String COL_GENDER = "gender";
    public static final String COL_LOCATION = "location";
    public static final String COL_SIGNATURE = "signature";
    public static final String COL_CREATE_AT = "create_at";
    public static final String COL_UPDATE_AT = "update_at";
    public static final String COL_TIME = "time";

    public static final String COL_GROUP_ID = "group_id";
    public static final String COL_GROUP_NAME = "group_name";
    public static final String COL_REASON = "reason";
    public static final String COL_STATUS = "status";


    /**
     * 创建数据表 sql 语句
     */

    /*创建 User 表*/
    public static final String CTABLE_USER = "create table if not exists "
            + TB_USER + " ("
            + COL_USER_NAME + " varchar not null unique, "
            + COL_NICKNAME + " varchar, "
            + COL_EMAIL + " varchar, "
            + COL_AVATAR + " text, "
            + COL_COVER + " text, "
            + COL_GENDER + " integer, "
            + COL_LOCATION + " text, "
            + COL_SIGNATURE + " text, "
            + COL_CREATE_AT + " varchar, "
            + COL_UPDATE_AT + " varchar" + " )";

    /*创建 User 表*/
    public static final String CTABLE_APPLY_FOR = "create table if not exists "
            + TB_APPLY_FOR + " ("
            + COL_USER_NAME + " varchar not null unique, "
            + COL_NICKNAME + " varchar, "
            + COL_GROUP_ID + " varchar, "
            + COL_GROUP_NAME + " varchar, "
            + COL_REASON + " text, "
            + COL_STATUS + " integer, "
            + COL_TIME + " varchar" + " )";


}
