package com.vmloft.develop.app.chat.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.chat.EMClient;

import java.util.Arrays;
import java.util.List;

/**
 * Class MLEMCheck
 * <p/>
 * Created by lzan13 on 2015/12/23 18:51.
 * 主要检查在环信 SDK集成过程中的一些基本配置是否正确
 * 主要做了一下几项检查：
 * 权限的配置（permission）
 * AppApplication 的 name 配置
 * Appkey 的配置
 * Service 的配置
 */
public class MLEMCheck {
    private final String TAG = "em_check";

    private static MLEMCheck instance;

    private MLEMCheck() {

    }

    /**
     * 获取单例类的实例
     *
     * @return
     */
    public static MLEMCheck getInstance() {
        if (instance == null) {
            instance = new MLEMCheck();
        }
        return instance;
    }

    /**
     * 初始化方法
     * 自动调用所有测试方法
     *
     * @param context
     */
    public void init(Context context) {
        checkPermission(context);
        checkApplication(context);
        checkMetaData(context);
        checkService(context);
    }

    /**
     * 获取PackageInfo对象
     *
     * @param context
     * @return
     */
    private PackageInfo getPackageInfo(Context context, int p) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), p);
            return pi;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查权限配置情况
     *
     * @param context
     */
    public void checkPermission(Context context) {
        /**
         * 这里定义Demo默认请求的一些权限，用来检查开发时是否有漏掉配置
         * 这些权限也不是圈闭必须，如果不需要某些功能是可以去掉的，根据自己需求以及理解配置
         */
        String[] pArray = {
                "android.permission.VIBRATE",
                "android.permission.INTERNET",
                "android.permission.RECORD_AUDIO",
                "android.permission.CAMERA",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.WAKE_LOCK",
                "android.permission.MODIFY_AUDIO_SETTINGS",
                "android.permission.READ_PHONE_STATE",
                "android.permission.RECEIVE_BOOT_COMPLETED",
                "android.permission.GET_ACCOUNTS",
                "android.permission.USE_CREDENTIALS",
                "android.permission.MANAGE_ACCOUNTS",
                "android.permission.AUTHENTICATE_ACCOUNTS",
                "com.android.launcher.permission.READ_SETTINGS",
                "android.permission.BROADCAST_STICKY",
                "android.permission.WRITE_SETTINGS",
                "android.permission.READ_PROFILE",
                "android.permission.READ_CONTACTS",
                "android.permission.READ_EXTERNAL_STORAGE"
        };
        logStart();
        logE("检查 Permission 配置情况");
        logLine();
        PackageInfo pi = getPackageInfo(context, PackageManager.GET_PERMISSIONS);
        // 获取权限列表
        String[] permissions = pi.requestedPermissions;
        List<String> pLists = Arrays.asList(permissions);
        logE("Demo 中请求了 %d 个权限，当前项目请求了 %d 个权限", pArray.length, permissions.length);
        logLine();
        // 输出 Demo 配置的权限，并判断当前项目是否有配置
        for (int i = 0; i < pArray.length; i++) {
            if (pLists.contains(pArray[i])) {
                logE("YES    %s", pArray[i]);
            } else {
                logE("NO     %s", pArray[i]);
            }
        }
        logEnd();
    }

    /**
     * 对application的配置检查
     */
    public void checkApplication(Context context) {
        logStart();
        logE("检查 AppApplication 配置情况");
        logLine();
        PackageInfo pi = getPackageInfo(context, PackageManager.GET_UNINSTALLED_PACKAGES);
        ApplicationInfo ai = pi.applicationInfo;
        String className = ai.className;
        logE("AppApplication className %s", className);
        logLine();
        if (className != null) {
            logE("AppApplication 已配置，可以在 AppApplication 的 onCreate 方法里进行 SDK 的初始化");
        } else {
            logE("没有配置 AppApplication，如果有在 onCreate 做 SDK 的初始化操作会无效，请检查！");
        }
        logEnd();
    }


    /**
     * 检查 MetaData 的配置情况 主要是检查是否配置了appkey
     *
     * @param context
     */
    public void checkMetaData(Context context) {
        boolean isAppkey = false;
        String appkey = "";
        logStart();
        logE("检查 appkey 配置情况");
        logLine();
        PackageInfo pi = getPackageInfo(context, PackageManager.GET_META_DATA);
        ApplicationInfo ai = pi.applicationInfo;
        try {
            Bundle bundle = ai.metaData;
            appkey = bundle.getString("EASEMOB_APPKEY", null);
            if (appkey != null && !appkey.equals("")) {
                isAppkey = true;
            }
        } catch (NullPointerException e) {
            logLine();
            logE("没有查询到 MetaData 配置，继续检测是否在代码中设置了 Appkey");
            logE("Exception %s", e.getMessage());
//            e.printStackTrace();
        }
        if (EMClient.getInstance().getOptions().getAppKey() != null && !EMClient.getInstance().getOptions().getAppKey().equals("")) {
            isAppkey = true;
            appkey = EMClient.getInstance().getOptions().getAppKey();
        }
        if (isAppkey) {
            logE("appkey 已配置 - %s", appkey);
        } else {
            logE("appkey 没有配置，请配置环信 SDK 初始化所需的 appkey");
        }
        logEnd();
    }

    /**
     * 检查 Service 配置情况 主要是检查是否有配置 EMChatService
     *
     * @param context
     */
    public void checkService(Context context) {
        boolean isService = false;
        logStart();
        logE("检查 Service 配置情况");
        logLine();
        PackageInfo pi = getPackageInfo(context, PackageManager.GET_SERVICES);
        try {
            ServiceInfo[] services = pi.services;
            logE("配置了 %d 个服务", services.length);
            logLine();
            for (int i = 0; i < services.length; i++) {
                ServiceInfo si = services[i];
                logE("ServiceInfo %s", si.toString());
                if (si.name.equals("com.easemob.chat.EMChatService")) {
                    isService = true;
                }
            }
            logLine();
            if (isService) {
                logE("EMChatService 已配置");
            } else {
                logE("EMChatService 没有配置，请在配置文件配置环信的 EMChatService");
            }
        } catch (NullPointerException e) {
            logLine();
            logE("没有查询到配置 Service 信息，请配置环信的 EMChatSerice");
            logE("Exception %s", e.getMessage());
            //            e.printStackTrace();
        }
        logEnd();
    }


    /**
     * -----------------------------------------------------------------------------
     * 关于log输出的简单封装一下
     */
    private void logStart() {
        Log.e(TAG, "======================================= start ===============================");
    }

    private void logLine() {
        Log.e(TAG, "|----------------------------------------------------------------------------");
    }

    private void logE(String msg) {
        Log.e(TAG, "|   " + msg);
    }

    private void logE(String msg, Object... args) {
        Log.e(TAG, "|   " + String.format(msg, args));
    }

    private void logEnd() {
        Log.e(TAG, "======================================== end ================================");
    }

}
