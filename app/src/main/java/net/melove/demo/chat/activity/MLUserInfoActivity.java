package net.melove.demo.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.db.MLUserDao;
import net.melove.demo.chat.entity.MLUserEntity;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/8/29.
 */
public class MLUserInfoActivity extends MLBaseActivity {

    private String mChatId;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private FloatingActionButton mFabAddContacts;
    private FloatingActionButton mFabCreateConverstaion;

    private MLUserDao mUserDao;
    private MLUserEntity mUserEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        init();
        initToolbar();
        initView();
    }

    private void init() {
        mActivity = this;
        mChatId = getIntent().getStringExtra(MLConstants.ML_C_CHAT_ID);
        mUserDao = new MLUserDao(mActivity);
        mUserEntity = mUserDao.getContact(mChatId);
    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ml_widget_collapsing);
        mCollapsingToolbarLayout.setTitle(mActivity.getResources().getString(R.string.ml_hint_username));

        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finishAfterTransition();
            }
        });

    }

    /**
     * 控件的初始化
     */
    private void initView() {
        mFabAddContacts = (FloatingActionButton) findViewById(R.id.ml_btn_fab_create_conversation);
        mFabCreateConverstaion = (FloatingActionButton) findViewById(R.id.ml_btn_fab_create_conversation);
        mFabAddContacts.setOnClickListener(viewListener);
        mFabCreateConverstaion.setOnClickListener(viewListener);

        // 根据本地查询到的用户情况来确定是显示 添加好友 还是显示 发送消息
        if (mUserEntity != null && mUserEntity.getUserName() != null) {
            mFabAddContacts.setVisibility(View.GONE);
            mFabCreateConverstaion.setVisibility(View.VISIBLE);
        } else {
            mFabAddContacts.setVisibility(View.VISIBLE);
            mFabCreateConverstaion.setVisibility(View.GONE);
        }

    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case -1:
                    mActivity.finishAfterTransition();
                    break;
                case R.id.ml_btn_fab_create_conversation:
                    startChat();
                    break;
                case R.id.ml_btn_fab_add_contact:
                    addContact();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 添加好友
     */
    private void addContact() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String reason = "加个好友呗";
                try {
                    EMContactManager.getInstance().addContact(mChatId, reason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MLToast.makeToast("发送好友请求成功，等待对方同意^_^").show();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MLToast.makeToast("发送好友请求失败-_-||，稍后重试").show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 发起聊天
     */
    private void startChat() {
        Intent intent = new Intent();
        intent.setClass(mActivity, MLChatActivity.class);
        intent.putExtra(MLConstants.ML_C_CHAT_ID, mChatId);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
