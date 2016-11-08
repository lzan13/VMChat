package net.melove.app.chat.ui.contacts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.R;
import net.melove.app.chat.module.event.MLApplyForEvent;
import net.melove.app.chat.ui.MLBaseActivity;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.ui.widget.MLImageView;
import net.melove.app.chat.ui.widget.MLToast;
import net.melove.app.chat.ui.chat.MLChatActivity;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.module.notification.MLNotifier;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lzan13 on 2015/8/29.
 * 用户信息展示界面，用于显示用户详细信息，可以显示好友以及陌生人，如果是陌生人就显示添加好友按钮
 */
public class MLUserActivity extends MLBaseActivity {

    @BindView(R.id.ml_widget_collapsing) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.ml_fab_add_or_chat) FloatingActionButton mAddOrChatFab;
    @BindView(R.id.ml_img_avatar) MLImageView mAvatarView;
    @BindView(R.id.ml_text_call_username) TextView mUsernameView;
    @BindView(R.id.ml_text_reason) TextView mReasonView;
    @BindView(R.id.ml_btn_agree) Button mAgreeBtn;
    @BindView(R.id.ml_btn_reject) Button mRejectBtn;

    // 当前联系人的username
    private String mChatId;
    // 当前登录用户username
    private String mCurrUsername;

    // 用户信息实体类
    private MLUserEntity mUserEntity;

    // 弹出对话框
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog addFriendDialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mActivity = this;

        ButterKnife.bind(mActivity);

        initView();
    }

    /**
     * 界面的初始化
     */
    private void initView() {
        mCurrUsername = EMClient.getInstance().getCurrentUser();
        mChatId = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        // 根据 Username 获取User对象
        mUserEntity = MLHyphenate.getInstance().getUser(mChatId);

        if (mChatId.equals(mCurrUsername)) {
            mAddOrChatFab.setVisibility(View.INVISIBLE);
        }

        // 设置Toolbar标题为用户名称
        mCollapsingToolbarLayout.setTitle(mChatId);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onFinish();
            }
        });

        // 根据本地查询到的用户情况来确定是显示 添加好友 还是显示 发送消息
        if (mUserEntity != null && mUserEntity.getUserName() != null) {
            mAddOrChatFab.setImageResource(R.mipmap.ic_chat_white_24dp);
        } else {
            mAddOrChatFab.setImageResource(R.mipmap.ic_add_white_24dp);
        }
    }

    /**
     * 界面控件点击监听
     */
    @OnClick({ R.id.ml_btn_agree, R.id.ml_btn_reject, R.id.ml_fab_add_or_chat }) void onClick(
            View v) {
        switch (v.getId()) {
            case R.id.ml_fab_add_or_chat:
                if (mUserEntity != null && mUserEntity.getUserName() != null) {
                    startChat();
                } else {
                    addContact();
                }
                break;
            case R.id.ml_btn_agree:
                agreeInvited();
                break;
            case R.id.ml_btn_reject:
                refuseInvited();
                break;
            default:
                break;
        }
    }

    /**
     * 添加好友
     */
    private void addContact() {
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.ml_add_friend);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.ml_dialog_text_message);
        textView.setText(R.string.ml_dialog_content_add_friend_reason);

        final EditText editText = (EditText) view.findViewById(R.id.ml_dialog_edit_input);
        editText.setHint(R.string.ml_hint_input_not_null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.ml_btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override public void run() {
                                // 获取输入的添加好友理由，并除去首尾空格，然后判断，如果为空就设置默认值
                                String reason = editText.getText().toString().trim();
                                if (TextUtils.isEmpty(reason)) {
                                    reason = mActivity.getResources()
                                            .getString(R.string.ml_add_friend_reason);
                                }
                                if (mChatId.equals(mCurrUsername)) {
                                    return;
                                }
                                try {
                                    EMClient.getInstance()
                                            .contactManager()
                                            .addContact(mChatId, reason);
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    int errorCode = e.getErrorCode();
                                    String errorMsg = e.getMessage();
                                    MLLog.e("AddContact: errorCode - %d, errorMsg - %s", errorCode,
                                            errorMsg);
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            MLToast.errorToast(R.string.ml_toast_add_friend_failed)
                                                    .show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.ml_btn_cancel,
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {

                    }
                });
        addFriendDialog = alertDialogBuilder.create();
        addFriendDialog.show();
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

    /**
     * 同意好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void agreeInvited() {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation("");
                    // 关闭对话框
                    dialog.dismiss();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 拒绝好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void refuseInvited() {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    EMClient.getInstance().contactManager().declineInvitation(mInvitedEntity.getUserName());
        //                    // 修改当前申请消息的状态
        //                    mInvitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
        //                    mInvitedEntity.setTime(MLDateUtil.getCurrentMillisecond());
        //                    MLInvitedDao.getInstance().updateInvited(mInvitedEntity);
        //                    dialog.dismiss();
        //                    mHandler.sendMessage(mHandler.obtainMessage(0));
        //                } catch (HyphenateException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }).start();
    }

    @Override protected void onResume() {
        super.onResume();
        initView();
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @Override protected void onDestroy() {
        // 检测是否有弹出框显示，如果有显示则销毁，避免 activity 的销毁导致错误
        if (addFriendDialog != null && addFriendDialog.isShowing()) {
            addFriendDialog.dismiss();
        }
        super.onDestroy();
    }
}
