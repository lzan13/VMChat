package net.melove.demo.chat.common.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.leakcanary.RefWatcher;

import net.melove.demo.chat.application.MLActivityManager;
import net.melove.demo.chat.application.MLApplication;
import net.melove.demo.chat.common.util.MLLog;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLBaseActivity extends AppCompatActivity {

    protected FragmentActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLLog.i("Activity onCreate");

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
     * 自定义返回方法
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onFinish() {
        // 根据不同的系统版本选择不同的 finish 方法
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mActivity.finish();
        } else {
            mActivity.finishAfterTransition();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLLog.i("Activity onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MLLog.i("Activity onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLLog.i("Activity onResume");
        MLActivityManager.getInstance().setCurrActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivity = this;
        MLLog.i("Activity onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLLog.i("Activity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLLog.i("Activity onDestroy");
        RefWatcher refWatcher = MLApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
