package net.melove.demo.chat.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.base.MLBaseActivity;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.conversation.MLChatActivity;
import net.melove.demo.chat.database.MLInvitedDao;
import net.melove.demo.chat.database.MLUserDao;
import net.melove.demo.chat.common.util.MLCrypto;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.widget.MLToast;


/**
 * Created by lzan13 on 2015/8/29.
 * 用户信息展示界面，主要用于显示用户信息，可以显示好友以及陌生人，如果是陌生人就显示添加好友按钮
 */
public class MLUserInfoActivity extends MLBaseActivity {

    private String mChatId;

    //
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private FloatingActionButton mFab;

    // 申请与邀请数据库操作类
    private MLInvitedDao mInvitedDao;
    // 用户信息数据库操作类
    private MLUserDao mUserDao;
    // 用户信息实体类
    private MLUserEntity mUserEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        initView();
        initToolbar();
    }

    /**
     * 界面的初始化
     */
    private void initView() {
        mActivity = this;
        mChatId = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        mInvitedDao = new MLInvitedDao(mActivity);
        mUserDao = new MLUserDao(mActivity);
        // 查询本地User对象
        mUserEntity = mUserDao.getContact(mChatId);

        mFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_user_info);
        mFab.setOnClickListener(viewListener);

        // 根据本地查询到的用户情况来确定是显示 添加好友 还是显示 发送消息
        if (mUserEntity != null && mUserEntity.getUserName() != null) {
            mFab.setImageResource(R.mipmap.ic_chat_white_24dp);
        } else {
            mFab.setImageResource(R.mipmap.ic_add_contacts_white_24dp);
        }
    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ml_widget_collapsing);
        // 设置Toolbar标题为用户名称
        mCollapsingToolbarLayout.setTitle(mChatId);

        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

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
        dialog.setTitle(R.string.ml_add_contacts);
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
                        if (TextUtils.isEmpty(reason)) {
                            reason = mActivity.getResources().getString(R.string.ml_add_contact_reason);
                        }
                        try {
                            EMClient.getInstance().contactManager().addContact(mChatId, reason);

                            // 创建一条好友申请数据，自己发送好友请求也保存
                            MLInvitedEntity invitedEntity = new MLInvitedEntity();
                            invitedEntity.setUserName(mChatId);
                            //invitedEntity.setNickName(mUserEntity.getNickName());
                            invitedEntity.setReason(reason);
                            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.APPLYFOR);
                            invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
                            // 设置此信息创建时间
                            invitedEntity.setCreateTime(MLDate.getCurrentMillisecond());
                            // 设置邀请信息的唯一id
                            invitedEntity.setObjId(MLCrypto.cryptoStr2MD5(invitedEntity.getUserName() + invitedEntity.getType()));

                            // 这里进行一下筛选，如果已存在则去更新本地内容
                            MLInvitedEntity temp = mInvitedDao.getInvitedEntiry(invitedEntity.getObjId());
                            if (temp != null) {
                                mInvitedDao.updateInvited(invitedEntity);
                            } else {
                                mInvitedDao.saveInvited(invitedEntity);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MLToast.makeToast(R.string.ml_toast_add_contacts_success).show();
                                }
                            });
                        } catch (HyphenateException e) {
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
        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mChatId);
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
