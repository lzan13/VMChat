package net.melove.demo.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLApplyForAdapter;
import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.info.MLApplyForEntity;
import net.melove.demo.chat.widget.MLToast;

import java.util.List;

/**
 * Created by lzan13 on 2015/3/28.
 */
public class MLNewApplyForActivity extends MLBaseActivity {

    private Activity mActivity;

    private Toolbar mToolbar;

    private ListView mListView;
    private EditText mEditText;
    private Button mBtnSearch;

    private MLApplyForDao mApplyForDao;
    private List<MLApplyForEntity> mList;
    private MLApplyForAdapter mApplyForAdapter;

    public MLNewApplyForActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for);
        init();
        initToolbar();
        initView();
    }


    private void init() {
        mActivity = this;
        mApplyForDao = new MLApplyForDao(mActivity);
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(R.string.ml_apply_for);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        mToolbar.setNavigationIcon(R.drawable.ic_menu_arrow);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setNavigationOnClickListener(viewListener);
    }

    private void initView() {

        // 初始化ListView
        mListView = (ListView) findViewById(R.id.ml_list_applyfor);
        mListView.setOnItemClickListener(itemListener);

        mList = mApplyForDao.getApplyForList();
        mApplyForAdapter = new MLApplyForAdapter(mActivity, mList);
        mListView.setAdapter(mApplyForAdapter);
    }

    /**
     * by lzan13 2015-11-2 11:25:04
     * 列表项点击事件
     */
    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MLToast.makeToast("item " + position).show();
        }
    };


    /**
     * 搜索联系人
     */
    private void searchContact() {
        String username = mEditText.getText().toString().trim();

        if (username.isEmpty()) {
            MLToast.makeToast(mActivity.getResources().getString(R.string.ml_error_field_required)).show();
            mEditText.requestFocus();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mActivity, MLUserInfoActivity.class);
        intent.putExtra("username", username);

        mActivity.startActivity(intent);
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
