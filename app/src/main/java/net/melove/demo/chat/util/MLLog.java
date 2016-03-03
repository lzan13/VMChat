package net.melove.demo.chat.util;

import android.util.Log;

/**
 * Created by lzan13 on 2014/12/16.
 * log日志输出封装类
 */
public class MLLog {

    // 这里设置默认的 Tag
    private static String mTag = "lzan13";
    private static boolean isDebug = true;


    /**
     * 使用自己传入的 Tag 输出 Info 信息
     *
     * @param message
     */
    public static void i(String message) {
        if (isDebug) {
            Log.i(mTag, message);
        }
    }

    /**
     * 使用自己传入的 Tag 输出 Debug 信息
     *
     * @param message
     */
    public static void d(String message) {
        if (isDebug) {
            Log.d(mTag, message);
        }
    }

    /**
     * 使用自己传入的 Tag 输出 Error 信息
     *
     * @param message
     */
    public static void e(String message) {
        if (isDebug) {
            Log.e(mTag, message);
        }
    }

    /**
     * 使用格式化的方式输出 Info 信息
     *
     * @param msg
     * @param args
     */
    public static void i(String msg, Object... args) {
        if (isDebug) {
            String message = args.length == 0 ? msg : String.format(msg, args);
            Log.i(mTag, message);
        }
    }

    /**
     * 使用格式化的方式输出 Debug 信息
     *
     * @param msg
     * @param args
     */
    public static void d(String msg, Object... args) {
        if (isDebug) {
            String message = args.length == 0 ? msg : String.format(msg, args);
            Log.d(mTag, message);
        }
    }

    /**
     * 使用格式化的方式输出 Error 信息
     *
     * @param msg
     * @param args
     */
    public static void e(String msg, Object... args) {
        if (isDebug) {
            String message = args.length == 0 ? msg : String.format(msg, args);
            Log.e(mTag, message);
        }
    }
}