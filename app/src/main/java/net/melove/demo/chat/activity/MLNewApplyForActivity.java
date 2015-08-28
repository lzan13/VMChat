package net.melove.demo.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLApplyForAdapter;
import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.info.MLApplyForInfo;

import java.util.List;

/**
 * Created by lzan13 on 2015/3/28.
 */
public class MLNewApplyForActivity extends MLBaseActivity {

    private Activity mActivity;

    private Toolbar mToolbar;

    private ListView mListView;

    private MLApplyForDao mApplyForDao;
    private List<MLApplyForInfo> mList;
    private MLApplyForAdapter mApplyForAdapter;

    public MLNewApplyForActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for);
        init();
        initToolbar();
        initContactList();
    }


    private void init() {
        mActivity = this;
        mApplyForDao = new MLApplyForDao(mActivity);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(R.string.ml_apply_for);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        mToolbar.setNavigationIcon(R.mipmap.icon_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    private void initContactList() {
        mListView = (ListView) findViewById(R.id.ml_list_applyfor);
        View headView = LayoutInflater.from(mActivity).inflate(R.layout.item_apply_for_head, null);
        mListView.addHeaderView(headView);
        mList = mApplyForDao.getApplyForList();
        mApplyForAdapter = new MLApplyForAdapter(mActivity, mList);
        mListView.setAdapter(mApplyForAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_apply_for, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
