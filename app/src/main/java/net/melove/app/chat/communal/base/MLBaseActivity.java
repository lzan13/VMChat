package net.melove.app.chat.communal.base;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLApplication;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.MLEasemob;
import net.melove.app.chat.application.eventbus.MLConnectionEvent;
import net.melove.app.chat.communal.util.MLLog;
import net.melove.app.chat.main.MLMainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by lzan13 on 2015/7/4.
 * Activity 的基类，做一些子类公共的工作
 */
public class MLBaseActivity extends AppCompatActivity {

    protected String className = this.getClass().getSimpleName();

    // 当前布局RootView
    protected View mRootView;

    // 当前界面的上下文菜单对象
    protected MLBaseActivity mActivity;

    protected AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLLog.i("%s onCreate", className);
        mActivity = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 公用的 Activity 跳转方法
     * 基类定义并实现的方法，为了以后方便扩展
     *
     * @param intent 跳转的意图
     */
    public void superJump(Intent intent) {
        startActivity(intent);
        /**
         * 5.0以上的跳转方法
         * ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
         * ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
         */
    }


    /**
     * 使用 EventBus 的订阅模式实现链接状态变化的监听，这里 EventBus 3.x 使用注解的方式确定方法调用的线程
     *
     * @param event 订阅的消息类型
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLConnectionEvent event) {
        switch (event.getType()) {
        case MLConstants.ML_CONNECTION_USER_LOGIN_OTHER_DIVERS:
            onConflictDialog();
            break;
        case MLConstants.ML_CONNECTION_USER_REMOVED:
            onRemovedDialog();
            break;
        case MLConstants.ML_CONNECTION_CONNECTED:
            break;
        case MLConstants.ML_CONNECTION_DISCONNECTED:

            break;
        }
    }

    /**
     * 弹出账户被其他设备登录的对话框
     */
    private void onConflictDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mActivity);
        }
        // 设置不可取消
        dialog.setCancelable(false);
        // 弹出框图标
        dialog.setIcon(R.mipmap.ic_warning_amber_24dp);
        // 弹出框标题
        dialog.setTitle(R.string.ml_dialog_title_conflict);
        // 弹出框提示信息
        dialog.setMessage(R.string.ml_dialog_message_conflict);
        // 弹出框自定义操作按钮
        dialog.setNeutralButton(R.string.ml_sign_in_restart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(mActivity, MLMainActivity.class);
                startActivity(intent);
            }
        });
        // 弹出框确认按钮
        dialog.setPositiveButton(R.string.ml_btn_i_know, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确认按钮，退出app
                List<MLBaseActivity> lists = MLEasemob.getInstance().getActivityList();
                for (MLBaseActivity activity : lists) {
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
            dialog = new AlertDialog.Builder(mActivity);
        }
        // 设置不可取消
        dialog.setCancelable(false);
        // 弹出框图标
        dialog.setIcon(R.mipmap.ic_warning_amber_18dp);
        // 弹出框标题
        dialog.setTitle(R.string.ml_dialog_title_conflict);
        // 弹出框提示信息
        dialog.setMessage(R.string.ml_dialog_message_conflict);
        // 弹出框自定义操作按钮
        dialog.setNeutralButton(R.string.ml_sign_in_restart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(mActivity, MLMainActivity.class);
                startActivity(intent);
            }
        });
        // 弹出框确认按钮
        dialog.setPositiveButton(R.string.ml_btn_i_know, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确认按钮，退出app
                List<MLBaseActivity> lists = MLEasemob.getInstance().getActivityList();
                for (MLBaseActivity activity : lists) {
                    activity.onFinish();
                }
            }
        });
        dialog.show();
    }


    /**
     * 自定义返回方法
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onFinish() {

        mActivity.finish();

        // 根据不同的系统版本选择不同的 finish 方法
        //        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
        //            mActivity.finish();
        //        } else {
        //            mActivity.finishAfterTransition();
        //        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        MLLog.i("%s onRestart", className);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MLLog.i("%s onStart", className);
        // 将 activity 添加到集合中去
        MLEasemob.getInstance().addActivity(mActivity);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLLog.i("%s onResume", className);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLLog.i("%s onPause", className);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLLog.i("%s onStop", className);
        // 将 activity 从集合中移除
        MLEasemob.getInstance().removeActivity(mActivity);
        // 取消订阅者的注册
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLLog.i("%s onDestroy", className);
        // 用来检测内存泄漏
        RefWatcher refWatcher = MLApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
