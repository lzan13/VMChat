package net.melove.demo.chat.invited;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.communal.base.MLBaseActivity;
import net.melove.demo.chat.communal.util.MLDate;
import net.melove.demo.chat.communal.widget.MLImageView;
import net.melove.demo.chat.conversation.MLChatActivity;
import net.melove.demo.chat.database.MLInvitedDao;

/**
 * Created by lzan13 on 2016/4/26.
 * 申请与请求详细界面，展示申请与请求信息，并处理
 */
public class MLInvitedDetailActivity extends MLBaseActivity {

    // 申请信息实体类
    private MLInvitedEntity mInvitedEntity;

    // 头像
    private MLImageView mAvatarView;
    // 名字
    private TextView mUsernameView;
    // 理由
    private TextView mReasonView;
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
    }

    private void initView() {
        mActivity = this;
        Intent intent = getIntent();
        /**
         * 调用{@link MLInvitedDao#getInvitedEntiry(String)} 获取指定的邀请信息
         */
        mInvitedEntity = MLInvitedDao.getInstance().getInvitedEntiry(intent.getStringExtra(MLConstants.ML_EXTRA_INVITED_ID));

        mAvatarView = (MLImageView) findViewById(R.id.ml_img_invited_avatar);
        mUsernameView = (TextView) findViewById(R.id.ml_text_invited_username);
        mReasonView = (TextView) findViewById(R.id.ml_text_invited_reason);
        mReplyBtn = (Button) findViewById(R.id.ml_btn_invited_reply);
        mAgreeBtn = (Button) findViewById(R.id.ml_btn_invited_agree);
        mRefuseBtn = (Button) findViewById(R.id.ml_btn_invited_refuse);


        mReplyBtn.setOnClickListener(viewListener);
        mAgreeBtn.setOnClickListener(viewListener);
        mRefuseBtn.setOnClickListener(viewListener);

    }

    /**
     * 给控件设置的点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
     * 回复邀请者
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(mInvitedEntity.getUserName());
                    mInvitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
                    mInvitedEntity.setTime(MLDate.getCurrentMillisecond());
                    MLInvitedDao.getInstance().updateInvited(mInvitedEntity);
                    dialog.dismiss();
                    mHandler.sendMessage(mHandler.obtainMessage(0));
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(mInvitedEntity.getUserName());
                    // 修改当前申请消息的状态
                    mInvitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
                    mInvitedEntity.setTime(MLDate.getCurrentMillisecond());
                    MLInvitedDao.getInstance().updateInvited(mInvitedEntity);
                    dialog.dismiss();
                    mHandler.sendMessage(mHandler.obtainMessage(0));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

                break;
            }
        }
    }
}
