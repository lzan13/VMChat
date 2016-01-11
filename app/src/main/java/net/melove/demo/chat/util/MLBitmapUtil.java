package net.melove.demo.chat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by lzan13 on 2016/1/11.
 */
public class MLBitmapUtil {

    /**
     * 将Bitmap 转为 base64 字符串
     * @param bitmap
     * @return
     */
    public static String bitmp2String(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        result = Base64.encodeToString(b, Base64.DEFAULT);
        MLLog.i(result);
        return result;
    }

    /**
     * 将 base64 的字符串 转为Bimmap
     * @param imageData
     * @return
     */
    public static Bitmap string2Bitmap(String imageData) {
        Bitmap bitmap = null;
        byte[] decode = Base64.decode(imageData, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }
}
