package net.melove.app.chat.communal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by lzan13 on 2016/1/11.
 * 图片处理类，压缩，转换
 */
public class MLBitmapUtil {

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
     * 获取图片文件的缩略图
     *
     * @param path      图片文件路径
     * @param dimension 设置缩略图最大尺寸
     * @return 返回压缩后的缩略图
     */
    public static Bitmap loadBitmapThumbnail(String path, int dimension) {
        Bitmap bitmap = loadBitmapByFile(path, dimension);
        // 调用矩阵方法压缩图片
        return compressBitmapByMatrix(bitmap, dimension);
    }

    /**
     * 通过文件加载图片，这里也可以加载大图，保证不会出现 OOM，
     *
     * @param path      要压缩的图片路径
     * @param dimension 定义压缩后的最大尺寸
     * @return 返回经过压缩处理的图片
     */
    public static Bitmap loadBitmapByFile(String path, int dimension) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设为true
        // 这个参数的意义是仅仅解析边缘区域，从而可以得到图片的一些信息，比如大小，而不会整个解析图片，防止OOM
        options.inJustDecodeBounds = true;

        // 此时bitmap还是为空的
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        // 得到图片文件的实际宽高
        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;

        // 根据宽高计算缩放比例
        float scale = getZoomScale(actualWidth, actualHeight, dimension);
        if (scale % 2 > 0.4) {
            scale += 1;
        }
        // 设置压缩比，必须是整形数，这样加载出的bitmap不会占用过大内存，又能显示清晰
        options.inSampleSize = (int) scale;
        // 设置图片文件仅仅加载边界为false
        options.inJustDecodeBounds = false;
        // 加载图片到 Bitmap 对象并返回
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 获取 bitmap 的缩略图
     *
     * @param bitmap    需要获取缩略图的Bitmap对象
     * @param dimension 缩略图的最大尺寸
     * @return 返回缩略图对象
     */
    public static Bitmap compressBitmapByMatrix(Bitmap bitmap, int dimension) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scale = 0.5f;
        if (w > h) {
            scale = (float) dimension / w;
        } else {
            scale = (float) dimension / h;
        }
        // 使用矩阵进行压缩图片
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
     * @param dimension    定义压缩后最大尺寸
     * @return 返回最佳缩放比例
     */
    public static float getZoomScale(int actualWidth, int actualHeight, int dimension) {
        float scale = 1.0f;
        if (actualWidth > actualHeight) {
            scale = (float) actualWidth / dimension;
        } else {
            scale = (float) actualHeight / dimension;
        }
        return scale;
    }

    public static void MLBlurImage(Context context, Bitmap bitmap, ImageView view, float scaleFactor, float radius) {

//        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
//                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(overlay);
//
//        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
//        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//        Paint paint = new Paint();
//        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//
//        RenderScript rs = RenderScript.create(context);
//
//        Allocation input = Allocation.createFromBitmap(rs, overlay);
//        Allocation output = Allocation.createTyped(rs, input.getType());
//
//        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, input.getElement());
//
//        blur.setRadius(radius);
//        blur.setInput(input);
//        blur.forEach(output);
//        output.copyTo(overlay);
//
//        view.setImageBitmap(overlay);
//        rs.destroy();
    }

    public static void MLBlurBackground(Context context, Bitmap bitmap, View view) {
        float scaleFactor = 8;
        float radius = 5;

//        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
//                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(overlay);
//
//        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
//        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//        Paint paint = new Paint();
//        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//
//        RenderScript rs = RenderScript.create(context);
//
//        Allocation input = Allocation.createFromBitmap(rs, overlay);
//        Allocation output = Allocation.createTyped(rs, input.getType());
//
//        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, input.getElement());
//
//        blur.setRadius(radius);
//        blur.setInput(input);
//        blur.forEach(output);
//        output.copyTo(overlay);
//
//        view.setBackground(new BitmapDrawable(context.getResources(), overlay));
//        rs.destroy();
    }
}
