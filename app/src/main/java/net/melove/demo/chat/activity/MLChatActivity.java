package net.melove.demo.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.melove.demo.chat.R;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/10/12 15:00.
 */
public class MLChatActivity extends MLBaseActivity {

    private Activity mActivity;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        initToolbar();
    }

    private void init() {
        mActivity = this;
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_info_detailed);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.icon_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(viewListener);
    }

    private void initView() {

    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
