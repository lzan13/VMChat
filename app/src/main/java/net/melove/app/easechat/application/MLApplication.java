package net.melove.app.easechat.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.melove.app.easechat.communal.base.MLBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/8/10
 * MLApplication类，项目的入口，做一些初始化操作
 */
public class MLApplication extends Application {

    // 全局的上下文对象
    private static Context context;

    private static RefWatcher watcher;


    @Override
    public void onCreate() {
        super.onCreate();

        context = this;


        initEasemob();

        // 初始化 LeakCanary
        watcher = LeakCanary.install(this);

    }

    public static Context getContext() {
        return context;
    }


    public static RefWatcher getRefWatcher() {
        return watcher;
    }

    /**
     * 初始化sdk的一些操作，封装在 MLEasemobHelper 类中
     */
    private void initEasemob() {
        MLEasemobHelper.getInstance().initEasemob(context);
    }


}
