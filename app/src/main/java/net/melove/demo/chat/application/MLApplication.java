package net.melove.demo.chat.application;

import android.app.Application;
import android.content.Context;

import net.melove.demo.chat.sdkutils.MLSDKHelper;

/**
 * Created by lzan13 on 2015/7/6.
 */
public class MLApplication extends Application {

    private static Context context;
    private MLSDKHelper mMLSDKHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        initSDK();

    }

    public static Context getContext() {
        return context;
    }

    private void initSDK() {
        mMLSDKHelper = MLSDKHelper.getInstance();
        mMLSDKHelper.onInit(context);
    }
}
