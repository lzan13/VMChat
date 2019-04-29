package com.vmloft.develop.app.chat.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.connection.ConnectionEvent;
import com.vmloft.develop.library.tools.base.VMActivity;
import com.vmloft.develop.library.tools.utils.VMLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lzan13 on 2015/7/4.
 * Activity 的基类，做一些子类公共的工作
 */
public class AppActivity extends VMActivity {

    // 根布局
    private View rootView;

    // Toolbar
    private Toolbar toolbar;

    protected AlertDialog.Builder dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
        toolbar = null;
        rootView = null;

        VMLog.i("%s onDestroy", className);
    }

    /**
     * 父类封装 Toolbar
     *
     * @return 返回公用的 Toolbar 控件
     */
    public Toolbar getToolbar() {
        if (toolbar == null) {
            toolbar = findViewById(R.id.widget_toolbar);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(ConnectionEvent event) {
        switch (event.getType()) {
            case AConstants.CONNECTION_USER_LOGIN_OTHER_DIVERS:
                onConflictDialog();
                break;
            case AConstants.CONNECTION_USER_REMOVED:
                onRemovedDialog();
                break;
            case AConstants.CONNECTION_CONNECTED:
                break;
            case AConstants.CONNECTION_DISCONNECTED:

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
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(activity, MainActivity.class);
                onStartActivity(activity, intent);
            }
        });
        // 弹出框确认按钮
        dialog.setPositiveButton(R.string.btn_i_know, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确认按钮，退出app
                List<AppActivity> lists = IMHelper.getInstance().getActivityList();
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
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(activity, MainActivity.class);
                onStartActivity(activity, intent);
            }
        });
        // 弹出框确认按钮
        dialog.setPositiveButton(R.string.btn_i_know, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确认按钮，退出app
                List<AppActivity> lists = IMHelper.getInstance().getActivityList();
                for (AppActivity activity : lists) {
                    activity.onFinish();
                }
            }
        });
        dialog.show();
    }
}
