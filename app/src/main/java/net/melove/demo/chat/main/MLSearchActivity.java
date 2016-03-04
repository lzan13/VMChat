package net.melove.demo.chat.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.base.MLBaseActivity;
import net.melove.demo.chat.contacts.MLUserInfoActivity;

/**
 * Created by lzan13 on 2016/1/12.
 */
public class MLSearchActivity extends MLBaseActivity {

    private Toolbar mToolbar;

    private EditText mSearchView;
    private Button mSearchBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mActivity = this;

        initView();
        initToolbar();
    }

    private void initView() {
        mSearchView = (EditText) findViewById(R.id.ml_edit_search);
        mSearchBtn = (Button) findViewById(R.id.ml_btn_search);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContacts();
            }
        });

    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_signup);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    private void searchContacts() {

        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("正在搜索，请稍候...");
        dialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO 这里使用线程睡眠 1.5s 来模拟去服务器搜索用户，真实开发应该请求网络去查找用户是否存在，
                // 然后在请求的结果中去判断是应该跳转到用户界面，还是提示用户不存在
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                // 模拟搜索完成，跳转到用户信息页
                String str = mSearchView.getText().toString();
                Intent intent = new Intent();
                intent.setClass(mActivity, MLUserInfoActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, str);
                startActivity(intent);
                mActivity.finish();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
