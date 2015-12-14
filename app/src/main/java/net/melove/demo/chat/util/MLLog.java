package net.melove.demo.chat.util;

import android.util.Log;

/**
 * Created by lzan13 on 2014/12/16.
 * log日志输出封装类
 */
public class MLLog {
    private static String mTag = "melove ";
    private static boolean isDebug = true;

    private static MLLogPrinter mPrinter = new MLLogPrinter();

    /**
     * 设置是否开启 debug模式（默认开启，正式发布一般关闭）
     *
     * @param mode
     */
    public static void setDebugMode(boolean mode) {
        isDebug = mode;
    }

    /**
     * 设置自己的 Tag （如果不设置使用默认）
     *
     * @param tag
     */
    public static void setTag(String tag) {
        mTag = tag;
    }

    /**
     * 获取当前类名
     *
     * @return
     */
    private static String getClassName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String result = elements[2].getClassName();
        return result;
    }

    /**
     * 使用默认 Tag 输出 Info 信息
     *
     * @param log
     */
    public static void i(String log) {
        if (isDebug) {
            Log.i(mTag, getClassName() + " i: " + log);
        }
    }

    /**
     * 使用默认 Tag 输出 Debug 信息
     * @param log
     */
    public static void d(String log) {
        if (isDebug) {
            Log.i(mTag, "d: " + log);
        }
    }

    /**
     * 使用默认 Tag 输出 Error 信息
     * @param log
     */
    public static void e(String log) {
        if (isDebug) {
            Log.i(mTag, "e: " + log);
        }
    }

    /**
     * 使用自己传入的 Tag 输出 Info 信息
     * @param tag
     * @param log
     */
    public static void i(String tag, String log) {
        if (isDebug) {
            Log.i(tag, "i: " + log);
        }
    }

    /**
     * 使用自己传入的 Tag 输出 Debug 信息
     * @param log
     */
    public static void d(String tag, String log) {
        if (isDebug) {
            Log.i(tag, "d: " + log);
        }
    }
    /**
     * 使用自己传入的 Tag 输出 Error 信息
     * @param log
     */
    public static void e(String tag, String log) {
        if (isDebug) {
            Log.i(tag, "e: " + log);
        }
    }

    /**
     * 使用格式化的方式输出 Info 信息
     * @param msg
     * @param args
     */
    public static void i(String msg, Object... args) {
        if (isDebug) {
            String log = args.length == 0 ? msg : String.format(msg, args);
            Log.i(mTag, "i: " + log);
        }
    }

    /**
     * 使用格式化的方式输出 Debug 信息
     * @param msg
     * @param args
     */
    public static void d(String msg, Object... args) {
        if (isDebug) {
            String log = args.length == 0 ? msg : String.format(msg, args);
            Log.i(mTag, "d: " + log);
        }
    }

    /**
     * 使用格式化的方式输出 Error 信息
     * @param msg
     * @param args
     */
    public static void e(String msg, Object... args) {
        if (isDebug) {
            String log = args.length == 0 ? msg : String.format(msg, args);
            Log.i(mTag, "e: " + log);
        }
    }

    private static class MLLogPrinter {


    }
}
