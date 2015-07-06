package net.melove.demo.chat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/3/26.
 */
public class MLDate {

    private static SimpleDateFormat sdp1 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private static SimpleDateFormat sdp2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdp3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * 使用 SimpleDateFormat 方式获取当前格式化后的时间
     *
     * @return
     */
    public static String getCurrentDate() {
        return sdp1.format(new Date());
    }

    public static long getCurrentMillisecond() {
        return System.currentTimeMillis();
    }

    /**
     * 将给定的字符串型时间格式化为另一种样式
     * @param dateStr
     * @return
     */
    public static String formatDate(String dateStr) {
        try {
            Date date = sdp2.parse(dateStr);
            return sdp1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}
