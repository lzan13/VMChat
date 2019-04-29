package com.vmloft.develop.app.chat.util;

import android.app.Activity;
import android.view.View;

import com.vmloft.develop.app.chat.common.ASPManager;
import com.vmloft.develop.library.tools.utils.VMStr;
import com.vmloft.develop.library.tools.utils.VMSystem;

/**
 * Create by lzan13 on 2019/04/08
 *
 * 项目工具类
 */
public class AUtil {

    /**
     * 判断启动时是否需要展示引导界面
     */
    public static boolean isShowGuide() {
        // 上次运行保存的版本号
        long runVersion = ASPManager.getInstance().getRunVersion();
        // 程序当前版本
        long version = VMSystem.getVersionCode();
        if (version > runVersion) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否已经登录
     */
    public static boolean isSign() {
        String account = ASPManager.getInstance().getCurrAccount();
        if (VMStr.isEmpty(account)) {
            return false;
        }
        return true;
    }

    /**
     * 设置 Guide 状态
     */
    public static void setGuideState() {
        // 保存新的版本
        ASPManager.getInstance().putRunVersion(VMSystem.getVersionCode());
    }

    /**
     * 楼主写的很好，但是有个问题。代码在设置黑色状态栏文字时是直接设置flag，会清除掉之前设好的flag，
     * 实际情况中一般是只想单独加上状态栏文字颜色的flag，而其他之前设好的flag也能保持。所以我觉得这么设置会更好：
     */
    public static void setDarkStatusBar(Activity activity, boolean isDark) {
        if (isDark) {
            // 1、设置状态栏文字深色，同时保留之前的 flag
            int originFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow().getDecorView().setSystemUiVisibility(originFlag | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //2、清除状态栏文字深色，同时保留之前的flag
            int originFlag = activity.getWindow().getDecorView().getSystemUiVisibility();
            //使用异或清除SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.getWindow().getDecorView().setSystemUiVisibility(originFlag ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
