package net.melove.demo.chat.common.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.melove.demo.chat.common.util.MLLog;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLBaseActivity extends AppCompatActivity {

    protected FragmentActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLLog.d("onCreate");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLLog.d("onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MLLog.d("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLLog.d("onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivity = this;
        MLLog.d("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLLog.d("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLLog.d("onDestroy");
    }
}
