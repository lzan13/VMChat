package net.melove.demo.chat.test;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;

import com.easemob.chat.EMChatConfig;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/12/23 18:51.
 * 主要检查在环信 SDK集成过程中的一些基本配置是否正确
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

    public void init(Context context) {
        MLEMCheck check = new MLEMCheck();

        check.checkPermission(context);
        check.checkApplication(context);
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
        logStart();
        logE("检查 Permission 配置情况");
        logLine();
        PackageInfo pi = getPackageInfo(context, PackageManager.GET_PERMISSIONS);
        // 获取权限列表
        String[] permissions = pi.requestedPermissions;
        logE("总共请求了 %d 个权限", permissions.length);
        logLine();
        // 循环输出下权限内容
        for (int i = 0; i < permissions.length; i++) {
            logE(permissions[i]);
        }
        logEnd();
    }

    /**
     * 对application的配置检查
     */
    public void checkApplication(Context context) {
        logStart();
        logE("检查 Application 配置情况");
        logLine();
        PackageInfo pi = getPackageInfo(context, PackageManager.GET_UNINSTALLED_PACKAGES);
        ApplicationInfo ai = pi.applicationInfo;
        String name = ai.name;
        String className = ai.className;

        logE("application name %s, className %s", name, className);

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
            if (appkey != null) {
                isAppkey = true;
            }
        } catch (NullPointerException e) {
            logLine();
            logE("没有查询到 MetaData 配置，继续检测是否在代码中设置了 Appkey");
            logE("Exception %s", e.getMessage());
//            e.printStackTrace();
        }
        if (EMChatConfig.getInstance().APPKEY != null) {
            isAppkey = true;
            appkey = EMChatConfig.getInstance().APPKEY;
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
