package com.vmloft.develop.app.chat.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;
import com.vmloft.develop.library.tools.VMBaseActivity;
import java.util.List;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.connection.ConnectionEvent;
import com.vmloft.develop.library.tools.utils.VMLog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lzan13 on 2015/7/4.
 * Activity 的基类，做一些子类公共的工作
 */
public class AppActivity extends VMBaseActivity {

    protected String className = this.getClass().getSimpleName();

    // 当前界面的上下文菜单对象
    protected AppActivity activity;

    // 根布局
    private View rootView;

    // Toolbar
    private Toolbar toolbar;

    protected AlertDialog.Builder dialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VMLog.i("%s onCreate", className);
        activity = this;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 父类封装 Toolbar
     *
     * @return 返回公用的 Toolbar 控件
     */
    public Toolbar getToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        }
        toolbar.setTitle("");
        return toolbar;
    }

    public View getRootView() {
        if (rootView == null) {
            rootView = findViewById(R.id.layout_coordinator);
        }
        return rootView;
    }

    /**
     * 使用 EventBus 的订阅模式实现链接状态变化的监听，这里 EventBus 3.x 使用注解的方式确定方法调用的线程
     *
     * @param event 订阅的消息类型
     */
    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(ConnectionEvent event) {
        switch (event.getType()) {
            case Constants.CONNECTION_USER_LOGIN_OTHER_DIVERS:
                onConflictDialog();
                break;
            case Constants.CONNECTION_USER_REMOVED:
                onRemovedDialog();
                break;
            case Constants.CONNECTION_CONNECTED:
                break;
            case Constants.CONNECTION_DISCONNECTED:

                break;
        }
    }

    /**
     * 弹出账户被其他设备登录的对话框
     */
    private void onConflictDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity);
        }
        // 设置不可取消
        dialog.setCancelable(false);
        // 弹出框图标
        dialog.setIcon(R.drawable.ic_warning_amber_24dp);
        // 弹出框标题
        dialog.setTitle(R.string.dialog_title_conflict);
        // 弹出框提示信息
        dialog.setMessage(R.string.dialog_message_conflict);
        // 弹出框自定义操作按钮
        dialog.setNeutralButton(R.string.sign_in_restart, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(activity, MainActivity.class);
                startActivity(intent);
            }
        });
        // 弹出框确认按钮
        dialog.setPositiveButton(R.string.btn_i_know, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                // 确认按钮，退出app
                List<AppActivity> lists = Hyphenate.getInstance().getActivityList();
                for (AppActivity activity : lists) {
                    activity.onFinish();
                }
            }
        });
        dialog.show();
    }

    /**
     * 弹出账户被服务器移除对话框，这个可能是账户被禁用，或者强制下线
     */
    private void onRemovedDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity);
        }
        // 设置不可取消
        dialog.setCancelable(false);
        // 弹出框图标
        dialog.setIcon(R.drawable.ic_warning_amber_18dp);
        // 弹出框标题
        dialog.setTitle(R.string.dialog_title_remove);
        // 弹出框提示信息
        dialog.setMessage(R.string.dialog_message_remove);
        // 弹出框自定义操作按钮
        dialog.setNeutralButton(R.string.sign_in_restart, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(activity, MainActivity.class);
                startActivity(intent);
            }
        });
        // 弹出框确认按钮
        dialog.setPositiveButton(R.string.btn_i_know, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                // 确认按钮，退出app
                List<AppActivity> lists = Hyphenate.getInstance().getActivityList();
                for (AppActivity activity : lists) {
                    activity.onFinish();
                }
            }
        });
        dialog.show();
    }

    /**
     * 封装公共的 Activity 跳转方法
     * 基类定义并实现的方法，为了以后方便扩展
     *
     * @param activity 当前 Activity 对象
     * @param intent 界面跳转 Intent 实例对象
     */
    public void onStartActivity(Activity activity, Intent intent) {
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
    }

    /**
     * 带有共享元素的 Activity 跳转方法
     *
     * @param activity 当前活动 Activity
     * @param intent 跳转意图
     * @param sharedElement 共享元素
     */
    public void onStartActivity(Activity activity, Intent intent,
            View sharedElement) {
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElement,
                        sharedElement.getTransitionName());
        ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
    }

    /**
     * 带有多个共享元素的 Activity 跳转方法
     *
     * @param activity 当前活动 Activity
     * @param intent 跳转意图
     * @param pairs 共享元素集合
     */
    public void onStartActivity(Activity activity, Intent intent, Pair<View, String>... pairs) {
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
        ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
    }

    /**
     * 自定义返回方法
     */
    protected void onFinish() {
        // 根据不同的系统版本选择不同的 finish 方法
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            activity.finish();
        } else {
            ActivityCompat.finishAfterTransition(activity);
        }
    }

    @Override protected void onRestart() {
        super.onRestart();
        VMLog.i("%s onRestart", className);
    }

    @Override protected void onStart() {
        super.onStart();
        VMLog.i("%s onStart", className);
        // 将 activity 添加到集合中去
        Hyphenate.getInstance().addActivity(activity);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override protected void onResume() {
        super.onResume();
        VMLog.i("%s onResume", className);
    }

    @Override protected void onPause() {
        super.onPause();
        VMLog.i("%s onPause", className);
    }

    @Override protected void onStop() {
        super.onStop();
        VMLog.i("%s onStop", className);
        // 将 activity 从集合中移除
        Hyphenate.getInstance().removeActivity(activity);
        // 取消订阅者的注册
        EventBus.getDefault().unregister(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        activity = null;
        toolbar = null;
        rootView = null;

        VMLog.i("%s onDestroy", className);
        // 用来检测内存泄漏
        RefWatcher refWatcher = AppApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
