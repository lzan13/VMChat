package net.melove.app.chat.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.app.MLBaseActivity;
import net.melove.app.chat.app.MLConstants;
import net.melove.app.chat.chat.MLChatActivity;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.widget.MLImageView;

/**
 * Created by lzan13 on 2015/8/29.
 * 用户信息展示界面，用于显示用户详细信息，可以显示好友以及陌生人，如果是陌生人就显示添加好友按钮
 */
public class MLUserActivity extends MLBaseActivity {

    @BindView(R.id.widget_collapsing) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.fab_add_or_chat) FloatingActionButton mAddOrChatFab;
    @BindView(R.id.widget_appbar) AppBarLayout mAppbarLayout;
    @BindView(R.id.img_avatar) MLImageView mAvatarView;
    @BindView(R.id.img_cover) ImageView mCoverView;
    @BindView(R.id.layout_head_container) View mHeadContainer;

    private boolean isShowHeadContainer = true;

    // 当前登录用户username
    private String mCurrUsername;
    // 当前联系人的username
    private String mChatId;
    // 申请与通知消息 id
    private String mApplyMsgId;

    // 用户信息实体类
    private MLUserEntity mUserEntity;

    // 弹出对话框
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog addFriendDialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
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
        mApplyMsgId = getIntent().getStringExtra(MLConstants.ML_EXTRA_MSG_ID);

        // 根据 Username 获取User对象
        mUserEntity = MLUserManager.getInstance().getUser(mChatId);

        if (mChatId.equals(mCurrUsername)) {
            mAddOrChatFab.setVisibility(View.INVISIBLE);
        }

        mAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = mAppbarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                MLLog.i("maxScroll: %d, verticalOffset: %d, percentage: %f", maxScroll,
                        verticalOffset, percentage);
                handleHeader(percentage);
            }
        });

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
    @OnClick({ R.id.fab_add_or_chat }) void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_or_chat:
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

    /**
     * 添加好友
     */
    private void addContact() {
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.ml_add_friend);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.dialog_text_message);
        textView.setText(R.string.ml_dialog_content_add_friend_reason);

        final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_input);
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
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            Snackbar.make(getRootView(),
                                                    R.string.ml_toast_add_friend_success,
                                                    Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    int errorCode = e.getErrorCode();
                                    String errorMsg = e.getMessage();
                                    MLLog.e("AddContact: errorCode - %d, errorMsg - %s", errorCode,
                                            errorMsg);
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            Snackbar.make(getRootView(),
                                                    R.string.ml_toast_add_friend_failed,
                                                    Snackbar.LENGTH_LONG).show();
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
     * 控制 Header
     *
     * @param percentage 百分比
     */
    private void handleHeader(float percentage) {
        if (percentage >= 0.5) {
            if (isShowHeadContainer) {
                startAlphaAnimation(mHeadContainer, View.INVISIBLE);
                isShowHeadContainer = false;
            }
        } else {
            if (!isShowHeadContainer) {
                startAlphaAnimation(mHeadContainer, View.VISIBLE);
                isShowHeadContainer = true;
            }
        }
    }

    /**
     * 设置渐变的动画
     *
     * @param v 控件
     * @param visibility 显示隐藏
     */
    public void startAlphaAnimation(View v, int visibility) {
        // 动画持续时间
        int duration = getResources().getInteger(R.integer.ml_time_alpha_change);
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override protected void onDestroy() {
        // 检测是否有弹出框显示，如果有显示则销毁，避免 activity 的销毁导致错误
        if (addFriendDialog != null && addFriendDialog.isShowing()) {
            addFriendDialog.dismiss();
        }
        super.onDestroy();
    }
}
