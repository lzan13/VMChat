package net.melove.app.easechat.communal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/3/26.
 */
public class MLDate {

    private static SimpleDateFormat dtf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat dtf2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private static SimpleDateFormat dtf3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static SimpleDateFormat dtf4 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat df2 = new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * 获取当前格式化后的时间
     *
     * @return 返回格式化后的时间
     */
    public static String getCurrentDate1() {
        return dtf1.format(new Date());
    }

    /**
     * 获取当前格式化后的时间
     *
     * @return 返回格式化后的时间
     */
    public static String getCurrentDate4() {
        return dtf4.format(new Date());
    }


    /**
     * 获取当前时间的毫秒值
     *
     * @return
     */
    public static long getCurrentMillisecond() {
        return System.currentTimeMillis();
    }

    /**
     * 将给定的字符串型时间格式化为另一种样式
     *
     * @param dateStr 原来的时间格式
     * @return 返回格式化后的时间格式
     */
    public static String formatDate(String dateStr) {
        try {
            Date date = dtf1.parse(dateStr);
            return dtf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从 long 整型的时间戳里取出时间
     *
     * @param time
     * @return
     */
    public static String long2Time(long time) {
        Date date = new Date(time);
        return timeFormat.format(date);
    }

    /**
     * 从 long 整型的时间戳里取出日期
     *
     * @param time
     * @return
     */
    public static String long2Date(long time) {
        Date date = new Date(time);
        return df1.format(date);
    }


}
