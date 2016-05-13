package net.melove.app.easechat.application;

import net.melove.app.easechat.communal.base.MLBaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by lzan13 on 2016/4/5.
 * 自定义Activity管理类，记录当前运行栈顶的 Activity
 */
public class MLActivityManager {
    // 当前类实例
    private static MLActivityManager instance;

    private WeakReference<MLBaseActivity> currActivity;

    /**
     * 私有的构造方法
     */
    private MLActivityManager() {}

    /**
     * 获取单例对象，如果当前类实例为空就创建，不为空直接返回
     *
     * @return 返回当前类的实例
     */
    public static MLActivityManager getInstance() {
        if (instance == null) {
            instance = new MLActivityManager();
        }
        return instance;
    }

    /**
     * 设置当前Activity，为当前栈顶Activity 创建一个弱引用
     *
     * @param activity 当前栈顶的 Activity
     */
    public void setCurrActivity(MLBaseActivity activity) {
        currActivity = new WeakReference<MLBaseActivity>(activity);
    }

    /**
     * 获取当前栈顶的 Activity
     *
     * @return 返回当前栈顶的 Activity
     */
    public MLBaseActivity getCurrActivity() {
        MLBaseActivity activity = null;
        // 判断当前 Activity 弱引用指向的 Activity 是否为空
        if (currActivity != null) {
            activity = currActivity.get();
        }
        return activity;
    }

}
