package net.melove.demo.chat;

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

    }

    public static Context getContext() {
        return context;
    }
}
