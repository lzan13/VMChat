package net.melove.demo.chat.communal.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;


import net.melove.demo.chat.application.MLApplication;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2015/4/15.
 */
public class MLDimen {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";


    public MLDimen() {

    }

    public static Point getScreenSize() {
        WindowManager wm = (WindowManager) MLApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize;
    }

    public static Point getImageSize(String str) {
        String wh = str.substring(str.indexOf(".") + 1, str.lastIndexOf("."));
        String w = wh.substring(0, wh.indexOf("."));
        String h = wh.substring(wh.indexOf(".") + 1);
        Point outSize = new Point(Integer.valueOf(w), Integer.valueOf(h));
        return outSize;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Resources res = MLApplication.getContext().getResources();
        int height = res.getIdentifier("status_bar_height", "dimen", "android");
        height = res.getDimensionPixelSize(height);
        MLLog.i("statusBar.h." + height);

        return height;
    }

    /**
     * 获取NavigationBar的高度（在NavigationBar 存在的情况下）
     *
     * @return
     */
    public static int getNavigationBarHeight() {
        Resources res = MLApplication.getContext().getResources();
        int height = 0;
        if (hasNavigationBar()) {
            String key = NAV_BAR_HEIGHT_RES_NAME;
            height = getInternalDimensionSize(res, key);
        }
//        MLLog.i("navigationbar.h." + height);
        return height;
    }


    public static int getSystemBarHeight() {
        Resources res = MLApplication.getContext().getResources();
        int height = res.getIdentifier("system_bar_height", "dimen", "android");
        height = res.getDimensionPixelSize(height);
        MLLog.i("systembar.h." + height);

        return height;
    }

    /**
     * 获取ToolBar高度
     *
     * @return
     */
    public static int getToolbarHeight() {
//        int toolbarHeight = mActivity.getActionBar().getHeight();
        int height = 0;
        if (height != 0) {
            return height;
        }
        TypedValue tv = new TypedValue();
        if (MLApplication.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, MLApplication.getContext().getResources().getDisplayMetrics());
        }
        MLLog.i("toolbar.h." + height);
        return height;
    }


    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断是否有虚拟导航栏NavigationBar，
     *
     * @return
     */
    private static boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = MLApplication.getContext().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            MLLog.e(e.getMessage());
        }
        return hasNavigationBar;
    }

    /**
     * 将dp类尺寸转换为px尺寸
     *
     * @param id
     * @return
     */
    public static int dp2px(int id) {
        Resources res = MLApplication.getContext().getResources();
        int result = res.getDimensionPixelSize(id);
        return result;
    }

    public static int dip2px(int dip) {
        Resources res = MLApplication.getContext().getResources();
        float density = res.getDisplayMetrics().density;
        return (int) (dip * density * 0.5f);
    }
}
