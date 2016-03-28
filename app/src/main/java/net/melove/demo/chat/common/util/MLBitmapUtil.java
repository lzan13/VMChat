package net.melove.demo.chat.common.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by lzan13 on 2016/1/11.
 * 图片处理类，压缩，转换
 */
public class MLBitmapUtil {

    private static int maxWidth = 1200;
    private static int maxHeight = 1920;

    /**
     * 将Bitmap 转为 base64 字符串
     *
     * @param bitmap 需要转为Base64 字符串的Bitmap对象
     * @return 返回转换后的字符串
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
     *
     * @param imageData 需要转为Bitmap的 Base64 字符串
     * @return 转为Bitmap的对象
     */
    public static Bitmap string2Bitmap(String imageData) {
        Bitmap bitmap = null;
        byte[] decode = Base64.decode(imageData, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }

    /**
     * 压缩图片 通过
     *
     * @param path 要压缩的图片路径
     * @return
     */
    public static Bitmap compressBitmap(String path, int thumbWidth, int thumbHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设为true了
        // 这个参数的意义是仅仅解析边缘区域，从而可以得到图片的一些信息，比如大小，而不会整个解析图片，防止OOM
        options.inJustDecodeBounds = true;

        // 此时bitmap还是为空的
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;

        // 根据宽高计算缩放比例
        int scale = getZoomScale(actualWidth, actualHeight);
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(path, options);
        return getThumbImage(bitmap, thumbWidth, thumbHeight);
    }

    /**
     * 获取bitmap的缩略图
     *
     * @param bitmap      需要获取缩略图的Bitmap对象
     * @param thumbWidth  缩略图的最大宽
     * @param thumbHeight 缩略图的高最大高
     * @return 返回缩略图对象
     */
    private static Bitmap getThumbImage(Bitmap bitmap, int thumbWidth, int thumbHeight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scale = 0.5f;
        if (w > h) {
            scale = (float) thumbWidth / w;
        } else {
            scale = (float) thumbHeight / h;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return result;
    }

    /**
     * 获取最佳缩放比例
     *
     * @param actualWidth  Bitmap的实际宽度
     * @param actualHeight Bitmap的实际高度
     * @return
     */
    private static int getZoomScale(int actualWidth, int actualHeight) {
        float scale = 1;
        if (actualWidth > actualHeight) {
            float ws = actualWidth / maxHeight;
            float hs = actualHeight / maxWidth;
            scale = ((ws + hs) / 2);
        } else {
            float ws = actualWidth / maxWidth;
            float hs = actualHeight / maxHeight;
            scale = ((ws + hs) / 2);
        }
        if (scale % 2 > 0.4) {
            scale += 1;
        }
        return (int) scale;
    }


}
