package net.melove.demo.chat.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/3/26.
 */
public class MLDate {

    private static SimpleDateFormat dateTimeFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat dateTimeFormat2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private static SimpleDateFormat dateTimeFormat3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * 获取当前格式化后的时间
     *
     * @return
     */
    public static String getCurrentDate() {
        return dateTimeFormat1.format(new Date());
    }

    public static long getCurrentMillisecond() {
        return System.currentTimeMillis();
    }

    /**
     * 将给定的字符串型时间格式化为另一种样式
     *
     * @param dateStr
     * @return
     */
    public static String formatDate(String dateStr) {
        try {
            Date date = dateTimeFormat1.parse(dateStr);
            return dateTimeFormat2.format(date);
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
        return dateFormat1.format(date);
    }


}
