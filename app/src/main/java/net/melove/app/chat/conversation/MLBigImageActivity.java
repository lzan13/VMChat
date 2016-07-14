package net.melove.app.chat.conversation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.melove.app.chat.R;
import net.melove.app.chat.communal.base.MLBaseActivity;

/**
 * Created by lzan13 on 2016/4/1.
 * 显示大图界面，
 * TODO 保存，分享图片，缩放查看等
 */
public class MLBigImageActivity extends MLBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
