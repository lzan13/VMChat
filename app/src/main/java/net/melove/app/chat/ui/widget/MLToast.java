package net.melove.app.chat.ui.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.melove.app.chat.MLApplication;
import net.melove.app.chat.R;
import net.melove.app.chat.util.MLBitmapUtil;


/**
 * Created by lzan13 on 2015/4/1.
 */
public class MLToast {
    private static Context mContext = MLApplication.getContext();

    // 默认的图标 表示Success 以Base64 字符串的方式保存的图片资源
    private String rightIcon = "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAABHNCSVQICAgIfAhkiAAAA5JJREFU\n" +
            "eJztmk+LFEcYh5/X1bguKmpGUCEIHtRDglE0h4gH0RhIJBo9RNDPkK+QY76KN/FgjMkScghhD+I/\n" +
            "BPGgRETdQESJf+Lq7vxyqBp9d9zZ6ZnpHqvHeqChu6e7pvo39XRXVQ9kMplMJpPJZDKZTCaTyWTe\n" +
            "J+xdV6BqJI0B64EPgRXADTN7VvT8pVVVLAViOKuAj4GtwBrgX0l3zey/ImWMdEDAOmAH8D2wDZgD\n" +
            "lgG/An8WKWAkA3Ja7QWOArvj9ixwGBiX1ASum9nTxcoauYDatDoEHAPGgSWE691NCOpv4C/g/QqI\n" +
            "+VrtJITT98NoZAJaRKsl7rAXwHXgd2CKLq0HRiSgLlq1mAGmgUngZzObKlL2SAREMa1uAj8Bp4Hb\n" +
            "RQuudUCx5TQIWn3L4lpNAmeBW92eXJ7aBuS0+gT4khK18tQ2IGAt8CkVaOWpXUDD0MpTq4CGpZWn\n" +
            "VgExJK08tQho2Fp5kg+orRM4FK08yQdE0KrVCdzFELTyJBtQm1ZHgT0MSStPzwFJMmAiLk+Bl2Y2\n" +
            "V2alFhhbHQeWA2PusMq08vQUUAxnHNgH7AfOE5r3g5Lr1a7Vcua3HKhQK0/hgOKvuhY4GJfPgA3A\n" +
            "b5IuAA/N7NUglVlEK99yKtfK00sLWk2Y1/2O8JjdBGwhvCm4D1yW9Khf3VLSqmckLZX0taQzku5J\n" +
            "mpHUlDQraVrSBUlfSdo4wHc0JB2QdFbS3Vh2U/O5KulHSTskrSrzGjtRtAU14wLhHvRBXB8jvG/a\n" +
            "BZwC1veqW4paeQoFZGZNSdPAFWA7QasVrox1wJG4Xli3WmvVjqQJSdsk/SDpj7am73X7JerYVbdU\n" +
            "tfIUvkmb2XNJd4BzhCY/RvjlVxJ6ti3ddhJ0a3TSzWn1OWFslZRWnp76QWb2ArgoaY5wL1oDbOZt\n" +
            "3b6J6w+AS5Iem9ksLDi2qq9WnehRt8OSNrlzk9fK09dYrEfdThJ0m4yn7yFxrTx9D1b70O1x3F8r\n" +
            "rQb+f5CkCeAj4ATwBaE/8/pjQv/pH96M1xrARsLYyn//NdzYysyeDFq3Mhh4uqOgbg1CCyNuJ62V\n" +
            "p5T5oAK6tYfSIkmtPGVPmN0kKAJv69bp+MqnLAah1IC66OZJWitP6VOuBXRLXitPlXPSnXRLXitP\n" +
            "ZQEtoFtrPDZF4lp5Kn2r0abbTNx9OXWtPEP5I3nsTK6Om89S6QRmMplMJpPJZDKZTCaTGUX+B1qt\n" +
            "mfkCdPHVAAAAAElFTkSuQmCC\n";

    // 默认的图标 表示Error  以Base64 字符串的方式保存的图片资源
    private String errorIcon = "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAABHNCSVQICAgIfAhkiAAABIRJREFU\n" +
            "eJztm02PVEUUhp8z3yriqOAC3NkojvEjoisxxgTjwgzsJNH458SguETZ6AZjSIzOEImOGkhcqbgA\n" +
            "6ZkJDkOGPi5OXbuYubf7flRVa1Jv0ou+ydQ9/d7z3FN1qgaysrKysrKysrKysrKysrL+X5IQg6jq\n" +
            "NPCw+7oNbIvIIMTYDWIQYAGYA6aATRHZ6TruTKDAFoH3gHvAD8D3wGbXsRtqATgGPOPi+VhVr4uI\n" +
            "dhm0k0HOnKeBN4Bl7Om9CMyp6pqI/Nll/AZxPA4sAacxg+aAO8AlVb3SxaTWBjmsFoFXXWCvAPtc\n" +
            "oDeBgaquExE3D6secAI4CTwJbAEDDLXfVLXfFrcuGbQfeB94GzPnIeydtgh8ABwEdoiLW4HVKewh\n" +
            "PeGuzwMvu3jmgY+AVtncxSAF7mJPaZ8LRtyYh4DXsacYBTdVPQA8ixlzHMscXAxFTLMYaukRw6rV\n" +
            "LxhSx7DMmWFo1BHgMQLj5mH1FPdjVVRkxYpFH/gVWMOQa6WuBq1iWbKFVbFDXqCxcKvCqpBi5pwB\n" +
            "vnAx3m57s9YGuUzYVNU1F9SDWKq/QCTcHFZLwLvsxUrd5xrwFfA58JOIbLT6gU6d50EiclNVV4Bp\n" +
            "F+BhAuPWAKtbwLfAJ8BqV3MggEFOsXFbwCrlKSx7yrC6hVWrzlj5CmJQTNw8rE4Dr1GN1UUCYeUr\n" +
            "VAYBYXErmQQuMxqrcwTCyldQg5xC4baAzdJPMhqrM8CXBMTKV3CDQuBWE6urWLW6AKyFzpxCMTII\n" +
            "aI3bhrveA97Csucw5Vh9h2G1IiLROgfRDHJqituci+lNLHsO7hpvN1YrRMDKV1SDWuD2CLaO6mGZ\n" +
            "A3uxusiwWkXvOcXOIKARbj3v+7hqlaQhl8Qgpzq4lbWAC6w+JGK1qlIyg2ri5ms3VhdIhJWvlBkE\n" +
            "/IvbZaxfsx94nurNgwE2S/4MuCwi62miHGoq9Q0bqmh+Fe+q5EqeQV6D/R2GeFWpeHkvA1uq+qOI\n" +
            "XI8f5VDJDBrRYPerla/ivdQDHgX+wiaTfRLuu6XMoDqdwAF7y7y/73YAK/mrJNp3S2LQmAa7X62u\n" +
            "YO+cI5RPJo9jJs6mwi2qQS06gedcTCcon0wmxy12BtXByp8EfoMZ0Wf02i0ZbtEMqtlg3z0JXHd/\n" +
            "W2ftlgS34Aa1xOq+tVXNtVsS3GJkUN0G+7hOYN1WSVTcghrUshNY+oMatkqi4RbEoIYN9kadwEnj\n" +
            "FiqDmjbYm3YCJ4ZbiBNmTfatRmJVpUni1uUAlY/VuAZ7kH2rSeDWJYN8rEY12ENvByfFrYtB88Bz\n" +
            "7lPWYI+yHdwQt6Pu+s9MwCD/FNcWZtgUCbaDYSxuip0sE+x48nTb+3QxaAM4i7E+C7yEGdYnwimL\n" +
            "ClXhdsddP48dhbnR9gZdDlDtqOofwNdY5mxjRl0lwimLihjKcDvq4vgUuAT8PpFjwC5AVdVrWBYt\n" +
            "MjyIEAWrEXH4uC1hVew8Hc2BcP+KMINVMQX+Bm6LyL0QYzeIYQo7ivwA9uBviMjdlDFkZWVlZWVl\n" +
            "ZWVlZWVlZWX9F/QPd/y65dzlZGQAAAAASUVORK5CYII=\n";


    // Toast 默认显示时间
    private int defaultTime = 3500;
    private boolean isShow;

    private WindowManager mWindowManager;
    private View mToastView;
    private WindowManager.LayoutParams mParams;

    /**
     * 私有构造方法，传递一个参数，默认显示id 为0的图标
     *
     * @param text Toast 要显示的内容
     */
    private MLToast(String text) {
        initToast(0, text);
    }

    /**
     * 私有构造方法，传递两个参数，可以在第一个参数定义 Toast 前的图标
     *
     * @param id   图标id 默认是0，可以自己传递项目中的图片资源id
     * @param text Toast 要显示的内容
     */
    private MLToast(int id, String text) {
        initToast(id, text);
    }

    /**
     * 初始化Toast方法，传递两个参数，可以在第一个参数定义 Toast 前的图标
     *
     * @param id   图标id 默认是0，可以自己传递项目中的图片资源id
     * @param text Toast 要显示的内容
     */
    private void initToast(int id, String text) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mToastView = inflater.inflate(R.layout.widget_toast_layout, null);
        ImageView imageView = (ImageView) mToastView.findViewById(R.id.img_toast_icon);
        if (id == 0) {
            imageView.setImageBitmap(MLBitmapUtil.string2Bitmap(rightIcon));
        } else if (id == 1) {
            imageView.setImageBitmap(MLBitmapUtil.string2Bitmap(errorIcon));
        } else {
            imageView.setImageResource(id);
        }

        TextView textView = (TextView) mToastView.findViewById(R.id.text_toast_text);
        textView.setText(text);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        isShow = false;
        setParams();
    }

    /**
     * 设置自定义Toast显示的一些属性
     */
    private void setParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // MLToast 的进入和退出动画效果
        mParams.windowAnimations = R.style.ml_toast_anim;

        //
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = mContext.getResources().getDimensionPixelSize(R.dimen.ml_dimen_16);
        mParams.y = mContext.getResources().getDimensionPixelSize(R.dimen.ml_dimen_72);

    }

    /**
     * 根据字符串资源id创建一个正确的 Toast
     *
     * @param strId Toast 显示的内容的字符串资源id
     * @return 返回 MLToast 对象
     */
    public static MLToast rightToast(int strId) {
        String text = mContext.getResources().getString(strId);
        MLToast toast = new MLToast(0, text);
        return toast;
    }

    /**
     * 根据传入的字符串内容创建一个正确的 Toast
     *
     * @param str Toast显示的内容字符串资源
     * @return 返回 MLToast对象
     */
    public static MLToast rightToast(String str) {
        MLToast toast = new MLToast(0, str);
        return toast;
    }

    /**
     * 根据字符串资源id创建一个错误的 Toast
     *
     * @param strId Toast 显示的内容的字符串资源id
     * @return 返回 MLToast 对象
     */
    public static MLToast errorToast(int strId) {
        String text = mContext.getResources().getString(strId);
        MLToast toast = new MLToast(1, text);
        return toast;
    }

    /**
     * 根据传入的字符串内容创建一个错误的 Toast
     *
     * @param str Toast显示的内容字符串资源
     * @return 返回 MLToast对象
     */
    public static MLToast errorToast(String str) {
        MLToast toast = new MLToast(1, str);
        return toast;
    }

    /**
     * 根据传入的字符串资源id创建 Toast
     *
     * @param strId Toast 要显示的内容 在String 资源文件中定义的资源 id
     * @return 返回当前 MLToast 对象
     */
    public static MLToast makeToast(int strId) {
        String text = mContext.getResources().getString(strId);
        MLToast toast = new MLToast(text);
        return toast;
    }

    /**
     * 根据传入的字符串创建 Toast
     *
     * @param str Toast 要显示的内容
     * @return 返回当前 MLToast 对象
     */
    public static MLToast makeToast(String str) {
        MLToast toast = new MLToast(str);
        return toast;
    }

    /**
     * 根据传入的资源id（默认为 0 使用当前类的图片资源） 以及字符串创建Toast
     *
     * @param id    图标id 默认是0，可以自己传递项目中的图片资源id
     * @param strId Toast 要显示的内容 在String 资源文件中定义的资源 id
     * @return 返回当前 MLToast 对象
     */
    public static MLToast makeToast(int id, int strId) {
        String text = mContext.getResources().getString(strId);
        MLToast toast = new MLToast(id, text);
        return toast;
    }

    /**
     * 根据传入的资源id（默认为 0 使用当前类的图片资源） 以及字符串创建Toast
     *
     * @param id   图标id 默认是0，可以自己传递项目中的图片资源id
     * @param text Toast 要显示的内容 字符串
     * @return
     */
    public static MLToast makeToast(int id, String text) {
        MLToast toast = new MLToast(id, text);
        return toast;
    }

    /**
     * 显示 Toast
     */
    public void show() {
        show(defaultTime);
    }

    /**
     * 将Toast显示在界面
     *
     * @param time
     */
    public void show(int time) {
        if (!isShow) {
            isShow = true;
            mWindowManager.addView(mToastView, mParams);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mWindowManager.removeView(mToastView);
                        isShow = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, time);
        }
    }
}
