package net.melove.demo.chat.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by lzan13 on 2015/7/6.
 */
public class MLApplication extends Application {

    private static Context context;
    private MLEasemobHelper mMLSDKHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        initEasemob();

    }

    public static Context getContext() {
        return context;
    }

    private void initEasemob() {
        MLEasemobHelper.getInstance().onInit(context);
    }
}
