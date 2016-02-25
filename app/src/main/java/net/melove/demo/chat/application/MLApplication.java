package net.melove.demo.chat.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by lzan13 on 2015/7/6.
 */
public class MLApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        initEasemob();


    }

    public static Context getContext() {
        return context;
    }

    /**
     * 初始化sdk的一些操作，封装在 MLEasemobHelper 类中
     */
    private void initEasemob() {
        MLEasemobHelper.getInstance().initEasemob(context);
    }
}
