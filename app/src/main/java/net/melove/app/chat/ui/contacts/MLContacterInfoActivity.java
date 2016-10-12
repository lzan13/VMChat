package net.melove.app.chat.ui.contacts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.application.eventbus.MLApplyForEvent;
import net.melove.app.chat.application.eventbus.MLConnectionEvent;
import net.melove.app.chat.ui.MLBaseActivity;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.ui.widget.MLImageView;
import net.melove.app.chat.ui.widget.MLToast;
import net.melove.app.chat.ui.chat.MLChatActivity;
import net.melove.app.chat.database.MLContactsDao;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.notification.MLNotifier;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by lzan13 on 2015/8/29.
 * 联系人信息展示界面，用于显示联系人的一些详细信息，可以显示好友以及陌生人，如果是陌生人就显示添加好友按钮
 */
public class MLContacterInfoActivity extends MLBaseActivity {

    //
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    // 界面控件
    private Toolbar mToolbar;

    private FloatingActionButton mFab;

    // 当前联系人的username
    private String mChatId;
    // 当前登录用户username
    private String mCurrUsername;
    // 头像
    private MLImageView mAvatarView;
    // 名字
    private TextView mUsernameView;
    // 理由
    private TextView mReasonView;
    // 状态
    private TextView mStatusView;
    // 回复按钮
    private Button mReplyBtn;
    // 同意按钮
    private Button mAgreeBtn;
    // 拒绝按钮
    private Button mRefuseBtn;


    // 用户信息实体类
    private MLContacterEntity mContactsEntity;

    // 弹出对话框
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog addContactsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacter_info);

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

        mFab = (FloatingActionButton) findViewById(R.id.ml_btn_fab_contacter_info);
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
            case R.id.ml_btn_fab_contacter_info:
                if (mContactsEntity != null && mContactsEntity.getUserName() != null) {
                    startChat();
                } else {
                    addContact();
                }
                break;
            case R.id.ml_btn_reply_apply_for:
                break;
            case R.id.ml_btn_agree_apply_for:
                agreeInvited();
                break;
            case R.id.ml_btn_refuse_apply_for:
                refuseInvited();
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
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(R.string.ml_add_contacts);
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.ml_dialog_text_message);
        textView.setText(R.string.ml_dialog_content_add_contact_reason);

        final EditText editText = (EditText) view.findViewById(R.id.ml_dialog_edit_input);
        editText.setHint(R.string.ml_hint_input_not_null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
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

                            // 根据申请者的 username 和当前登录账户 username 拼接出msgId方便后边更新申请信息
                            String msgId = EMClient.getInstance().getCurrentUser() + mChatId;

                            // 首先查找这条申请消息是否为空
                            EMMessage message = EMClient.getInstance().chatManager().getMessage(msgId);
                            if (message != null) {
                                // 申请理由
                                message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
                                // 当前申请的消息状态
                                message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_BE_APPLY_FOR);
                                // 更新消息时间
                                message.setMsgTime(MLDateUtil.getCurrentMillisecond());
                                message.setLocalTime(message.getMsgTime());
                                // 更新消息到本地
                                EMClient.getInstance().chatManager().updateMessage(message);
                            } else {
                                // 创建一条接收的消息，用来保存申请信息
                                message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                EMTextMessageBody body = new EMTextMessageBody("发送好友请求给 " + mChatId);
                                message.addBody(body);
                                // 设置消息扩展，主要是申请信息
                                message.setAttribute(MLConstants.ML_ATTR_APPLY_FOR, true);
                                // 申请者username
                                message.setAttribute(MLConstants.ML_ATTR_USERNAME, mChatId);
                                // 申请理由
                                message.setAttribute(MLConstants.ML_ATTR_REASON, reason);
                                // 当前申请的消息状态
                                message.setAttribute(MLConstants.ML_ATTR_STATUS, MLConstants.ML_STATUS_APPLY_FOR);
                                // 申请与通知类型
                                message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_APPLY_FOR_CONTACTS);
                                // 设置消息发送方
                                message.setFrom(MLConstants.ML_CONVERSATION_ID_APPLY_FOR);
                                // 设置
                                message.setMsgId(msgId);
                                // 将消息保存到本地和内存
                                EMClient.getInstance().chatManager().saveMessage(message);
                            }
                            // 调用发送通知栏提醒方法，提醒用户查看申请通知
                            MLNotifier.getInstance().sendNotificationMessage(message);
                            // 使用 EventBus 发布消息，通知订阅者申请与通知信息有变化
                            MLApplyForEvent event = new MLApplyForEvent();
                            event.setMessage(message);
                            EventBus.getDefault().post(event);
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
        alertDialogBuilder.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        addContactsDialog = alertDialogBuilder.create();
        addContactsDialog.show();
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

        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    EMClient.getInstance().contactManager().acceptInvitation(mInvitedEntity.getUserName());
        //                    mInvitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
        //                    mInvitedEntity.setTime(MLDateUtil.getCurrentMillisecond());
        //                    MLInvitedDao.getInstance().updateInvited(mInvitedEntity);
        //                    // 关闭对话框
        //                    dialog.dismiss();
        //                    // 发送Handler Manager 通知界面更新
        //                    mHandler.sendMessage(mHandler.obtainMessage(0));
        //                } catch (HyphenateException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }).start();


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

    /**
     * 重载父类实现的 EventBus 订阅方法，实现更具体的逻辑处理
     *
     * @param event 订阅的消息类型
     */
    @Override
    public void onEventBus(MLConnectionEvent event) {
        super.onEventBus(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 检测是否有弹出框显示，如果有显示则销毁，避免 activity 的销毁导致错误
        if (addContactsDialog != null && addContactsDialog.isShowing()) {
            addContactsDialog.dismiss();
        }
        super.onDestroy();
    }
}
