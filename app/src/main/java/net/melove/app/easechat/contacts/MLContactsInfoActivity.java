package net.melove.app.easechat.contacts;

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

import net.melove.app.easechat.R;
import net.melove.app.easechat.communal.base.MLBaseActivity;
import net.melove.app.easechat.application.MLConstants;
import net.melove.app.easechat.communal.util.MLDateUtil;
import net.melove.app.easechat.communal.widget.MLToast;
import net.melove.app.easechat.conversation.MLChatActivity;
import net.melove.app.easechat.database.MLInvitedDao;
import net.melove.app.easechat.database.MLContactsDao;
import net.melove.app.easechat.communal.util.MLCrypto;
import net.melove.app.easechat.communal.util.MLLog;
import net.melove.app.easechat.invited.MLInvitedEntity;


/**
 * Created by lzan13 on 2015/8/29.
 * 联系人信息展示界面，用于显示联系人的一些详细信息，可以显示好友以及陌生人，如果是陌生人就显示添加好友按钮
 */
public class MLContactsInfoActivity extends MLBaseActivity {

    // 当前联系人的username
    private String mChatId;
    // 当前登录用户username
    private String mCurrUsername;

    //
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private FloatingActionButton mFab;

    // 用户信息实体类
    private MLContactsEntity mContactsEntity;


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
        mCurrUsername = EMClient.getInstance().getCurrentUser();
        // 查询本地User对象
        mContactsEntity = MLContactsDao.getInstance().getContact(mChatId);

        mFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_user_info);
        mFab.setOnClickListener(viewListener);

        if (mChatId.equals(mCurrUsername)) {
            mFab.setVisibility(View.INVISIBLE);
        }

        // 根据本地查询到的用户情况来确定是显示 添加好友 还是显示 发送消息
        if (mContactsEntity != null && mContactsEntity.getUserName() != null) {
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
        mToolbar.setNavigationOnClickListener(viewListener);

    }

    /**
     * 界面控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case -1:
                onFinish();
                break;
            case R.id.ml_btn_fab_user_info:
                if (mContactsEntity != null && mContactsEntity.getUserName() != null) {
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
        editText.setHint(R.string.ml_hint_input_not_null);
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
                        if (mChatId.equals(mCurrUsername)) {
                            return;
                        }
                        try {
                            EMClient.getInstance().contactManager().addContact(mChatId, reason);

                            // 创建一条好友申请数据，自己发送好友请求也保存
                            MLInvitedEntity invitedEntity = new MLInvitedEntity();
                            // 根据根据对方的名字，加上当前用户的名字，加申请类型按照一定顺序组合，得到当前申请信息的唯一 ID
                            String invitedId = MLCrypto.cryptoStr2MD5(mCurrUsername + mChatId + MLInvitedEntity.InvitedType.CONTACTS);
                            // 设置此条信息的唯一ID
                            invitedEntity.setInvitedId(invitedId);
                            // 对方的username
                            invitedEntity.setUserName(mChatId);
                            // invitedEntity.setNickName(mContactsEntity.getNickName());
                            // 设置申请理由
                            invitedEntity.setReason(reason);
                            // 设置申请状态为被申请
                            invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.APPLYFOR);
                            // 设置申请信息为联系人申请
                            invitedEntity.setType(MLInvitedEntity.InvitedType.CONTACTS);
                            // 设置申请信息的时间
                            invitedEntity.setTime(MLDateUtil.getCurrentMillisecond());

                            // 这里进行一下筛选，如果已存在则去更新本地内容
                            MLInvitedEntity temp = MLInvitedDao.getInstance().getInvitedEntiry(invitedId);
                            if (temp != null) {
                                MLInvitedDao.getInstance().updateInvited(invitedEntity);
                            } else {
                                MLInvitedDao.getInstance().saveInvited(invitedEntity);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MLToast.rightToast(R.string.ml_toast_add_contacts_success).show();
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
                                    MLToast.errorToast(R.string.ml_toast_add_contacts_failed).show();
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
