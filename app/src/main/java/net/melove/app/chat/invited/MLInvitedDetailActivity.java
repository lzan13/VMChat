package net.melove.app.chat.invited;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.eventbus.MLApplyForEvent;
import net.melove.app.chat.communal.base.MLBaseActivity;
import net.melove.app.chat.communal.util.MLDateUtil;
import net.melove.app.chat.communal.widget.MLImageView;
import net.melove.app.chat.conversation.MLChatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lzan13 on 2016/4/26.
 * 申请与请求详细界面，展示申请与请求信息，并处理
 */
public class MLInvitedDetailActivity extends MLBaseActivity {

    // 界面控件
    private Toolbar mToolbar;

    // 申请信息实体类
    private MLInvitedEntity mInvitedEntity;

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


    // 自定义Handler类，用来处理非UI界面刷新UI的工作
    private MLHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_detail);

        initView();
        initToolbar();
    }

    private void initView() {
        mActivity = this;
        mHandler = new MLHandler();

        Intent intent = getIntent();
        /**
         * 调用{@link MLInvitedDao#getInvitedEntiry(String)} 获取指定的申请与邀请信息
         */
//        mInvitedEntity = MLInvitedDao.getInstance().getInvitedEntiry(intent.getStringExtra(MLConstants.ML_EXTRA_INVITED_ID));
//
//        mAvatarView = (MLImageView) findViewById(R.id.ml_img_invited_avatar);
//        mUsernameView = (TextView) findViewById(R.id.ml_text_invited_username);
//        mReasonView = (TextView) findViewById(R.id.ml_text_invited_reason);
//        mStatusView = (TextView) findViewById(R.id.ml_text_invited_status);
//        mReplyBtn = (Button) findViewById(R.id.ml_btn_invited_reply);
//        mAgreeBtn = (Button) findViewById(R.id.ml_btn_invited_agree);
//        mRefuseBtn = (Button) findViewById(R.id.ml_btn_invited_refuse);
//
//
//        mReplyBtn.setOnClickListener(viewListener);
//        mAgreeBtn.setOnClickListener(viewListener);
//        mRefuseBtn.setOnClickListener(viewListener);

        refreshInvited();
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(mInvitedEntity.getUserName());
        setSupportActionBar(mToolbar);
        // 设置toolbar图标
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        // 设置Toolbar图标点击事件，Toolbar上图标的id是 -1
        mToolbar.setNavigationOnClickListener(viewListener);
    }

    /**
     * 刷新界面，
     */
    private void refreshInvited() {
        mUsernameView.setText(mInvitedEntity.getUserName());
        mReasonView.setText(mInvitedEntity.getReason());
        // 判断当前的申请与通知的状态，显示不同的提醒文字
        if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.AGREED) {
            mAgreeBtn.setVisibility(View.GONE);
            mRefuseBtn.setVisibility(View.GONE);
            mReplyBtn.setText(R.string.ml_btn_start_chat);
            mStatusView.setText(R.string.ml_agreed);
            mStatusView.setVisibility(View.VISIBLE);
        } else if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.REFUSED) {
            mAgreeBtn.setVisibility(View.GONE);
            mRefuseBtn.setVisibility(View.GONE);
            mReplyBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.ml_refused);
            mStatusView.setVisibility(View.VISIBLE);
        } else if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAGREED) {
            mAgreeBtn.setVisibility(View.GONE);
            mRefuseBtn.setVisibility(View.GONE);
            mReplyBtn.setText(R.string.ml_btn_start_chat);
            mStatusView.setText(R.string.ml_be_agreed);
            mStatusView.setVisibility(View.VISIBLE);
        } else if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEREFUSED) {
            mAgreeBtn.setVisibility(View.GONE);
            mRefuseBtn.setVisibility(View.GONE);
            mReplyBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.ml_be_refused);
            mStatusView.setVisibility(View.VISIBLE);
        } else if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.APPLYFOR) {
            mAgreeBtn.setVisibility(View.GONE);
            mRefuseBtn.setVisibility(View.GONE);
            mReplyBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.ml_waiting_respond);
            mStatusView.setVisibility(View.VISIBLE);
        } else if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.BEAPPLYFOR) {
            // 被申请
            mAgreeBtn.setVisibility(View.VISIBLE);
            mRefuseBtn.setVisibility(View.VISIBLE);
            mReplyBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.ml_waiting_dispose);
            mStatusView.setVisibility(View.VISIBLE);
        } else if (mInvitedEntity.getStatus() == MLInvitedEntity.InvitedStatus.GROUPAPPLYFOR) {
            // 群组申请与邀请
            mAgreeBtn.setVisibility(View.VISIBLE);
            mRefuseBtn.setVisibility(View.VISIBLE);
            mReplyBtn.setVisibility(View.VISIBLE);
            mStatusView.setText(R.string.ml_waiting_dispose);
            mStatusView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 给控件设置的点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case -1:
                onFinish();
                break;
            case R.id.ml_btn_invited_reply:
                repleyIntiver();
                break;
            case R.id.ml_btn_invited_agree:
                agreeInvited();
                break;
            case R.id.ml_btn_invited_refuse:
                refuseInvited();
                break;
            }
        }
    };

    /**
     * 回复邀请者，其实直接发起会话了
     */
    private void repleyIntiver() {
        Intent intent = new Intent(mActivity, MLChatActivity.class);
        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mInvitedEntity.getUserName());
        superJump(intent);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLApplyForEvent event) {
        refreshInvited();
    }

    /**
     * 自定义Handler，用来处理界面的刷新
     */
    class MLHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
            case 0:
                // 刷新界面
                refreshInvited();
                break;
            }
        }
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
