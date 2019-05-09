package com.vmloft.develop.app.chat.router;

import android.content.Context;

import com.vmloft.develop.app.chat.ui.guide.GuideActivity;
import com.vmloft.develop.app.chat.ui.main.MainActivity;
import com.vmloft.develop.app.chat.ui.sign.SignInActivity;
import com.vmloft.develop.library.tools.router.VMRouter;

/**
 * Create by lzan13 on 2019/04/09
 *
 * 项目路由
 */
public class ARouter extends VMRouter {

    /**
     * 跳转到主界面
     */
    public static void goMain(Context context) {
        forward(context, MainActivity.class);
    }

    /**
     * 跳转到引导页
     */
    public static void goGuide(Context context) {
        forward(context, GuideActivity.class);
    }

    /**
     * 跳转到登录注册界面
     */
    public static void goSign(Context context) {
        forward(context, SignInActivity.class);
    }
}
