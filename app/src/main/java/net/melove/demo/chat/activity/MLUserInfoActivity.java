package net.melove.demo.chat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.db.MLUserDao;
import net.melove.demo.chat.entity.MLApplyForEntity;
import net.melove.demo.chat.entity.MLUserEntity;
import net.melove.demo.chat.util.MLCrypto;
import net.melove.demo.chat.util.MLDate;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLToast;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/29.
 */
public class MLUserInfoActivity extends MLBaseActivity {

    private String mChatId;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private FloatingActionButton mFab;

    private MLApplyForDao mApplyForDao;
    private MLUserDao mUserDao;
    private MLUserEntity mUserEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        init();
        initToolbar();
    }

    private void init() {
        mActivity = this;
        mChatId = getIntent().getStringExtra(MLConstants.ML_C_CHAT_ID);
        mApplyForDao = new MLApplyForDao(mActivity);
        mUserDao = new MLUserDao(mActivity);
        mUserEntity = mUserDao.getContact(mChatId);
    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ml_widget_collapsing);
        mCollapsingToolbarLayout.setTitle(mChatId);

        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

    }

    /**
     * 控件的初始化
     */
    private void initView() {
        mFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_user_info);
        mFab.setOnClickListener(viewListener);

        // 根据本地查询到的用户情况来确定是显示 添加好友 还是显示 发送消息
        if (mUserEntity != null && mUserEntity.getUserName() != null) {
            mFab.setImageResource(R.drawable.ic_menu_chat);
        } else {
            mFab.setImageResource(R.drawable.ic_menu_add);
        }

    }

    /**
     * 界面控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_fab_user_info:
                    if (mUserEntity != null && mUserEntity.getUserName() != null) {
                        startChat();
                    } else {
                        addContact();
                    }
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(R.string.ml_add_contact);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.ml_dialog_text_message);
        textView.setText(R.string.ml_dialog_content_add_contact);

        final EditText editText = (EditText) view.findViewById(R.id.ml_dialog_edit_input);
        editText.setHint(R.string.ml_hint_input);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 获取输入的添加好友理由，并除去首尾空格，然后判断，如果为空就设置默认值
                        String reason = editText.getText().toString().trim();
                        if (reason.equals("")) {
                            reason = mActivity.getResources().getString(R.string.ml_add_contact_reason);
                        }
                        try {
                            EMContactManager.getInstance().addContact(mChatId, reason);

                            // 创建一条好友申请数据，自己发送好友请求也保存
                            MLApplyForEntity applyForEntity = new MLApplyForEntity();
                            applyForEntity.setUserName(mChatId);
//                            applyForEntity.setNickName(mUserEntity.getNickName());
                            applyForEntity.setReason(reason);
                            applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.APPLYFOR);
                            applyForEntity.setType(0);
                            applyForEntity.setTime(MLDate.getCurrentMillisecond());
                            applyForEntity.setObjId(MLCrypto.cryptoStr2MD5(applyForEntity.getUserName() + applyForEntity.getType()));

                            // 这里进行一下筛选，如果已存在则去更新本地内容
                            MLApplyForEntity temp = mApplyForDao.getApplyForEntiry(applyForEntity.getObjId());
                            if (temp != null) {
                                mApplyForDao.updateApplyFor(applyForEntity);
                            } else {
                                mApplyForDao.saveApplyFor(applyForEntity);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MLToast.makeToast(R.string.ml_toast_add_contacts_success).show();
                                }
                            });
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                            int errorCode = e.getErrorCode();
                            String errorMsg = e.getMessage();
                            MLLog.e("AddContact: errorCode - %d, errorMsg - %s", errorCode, errorMsg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MLToast.makeToast(R.string.ml_toast_add_contacts_failed).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();
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
        initView();
    }
}
