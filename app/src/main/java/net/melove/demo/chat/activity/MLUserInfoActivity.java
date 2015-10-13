package net.melove.demo.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.db.MLUserDao;
import net.melove.demo.chat.info.MLUserInfo;
import net.melove.demo.chat.widget.MLImageView;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/8/29.
 */
public class MLUserInfoActivity extends MLBaseActivity {

    private Activity mActivity;

    private Toolbar mToolbar;

    private MLUserDao mUserDao;
    private MLUserInfo mUserInfo;

    private String mUsername;
    private MLImageView mAvatarView;
    private TextView mUsernameView;
    private TextView mSignatureView;
    private TextView mTagView;
    private Button mAddFriendBtn;
    private Button mStartChatBtn;
    private Button mVideoChatBtn;


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
        mUserDao = new MLUserDao(mActivity);
    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_info_detailed);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.icon_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(viewListener);

    }

    private void initView() {
        mAvatarView = (MLImageView) findViewById(R.id.ml_img_user_info_avatar);
        mUsernameView = (TextView) findViewById(R.id.ml_text_user_info_username);
        mSignatureView = (TextView) findViewById(R.id.ml_text_user_info_signature);
        mTagView = (TextView) findViewById(R.id.ml_text_user_info_tag);
        mAddFriendBtn = (Button) findViewById(R.id.ml_btn_user_info_add);
        mStartChatBtn = (Button) findViewById(R.id.ml_btn_user_info_chat_start);
        mVideoChatBtn = (Button) findViewById(R.id.ml_btn_user_info_chat_video);

        mAddFriendBtn.setOnClickListener(viewListener);
        mStartChatBtn.setOnClickListener(viewListener);
        mVideoChatBtn.setOnClickListener(viewListener);

        mAvatarView.setImageResource(R.mipmap.icon_avatar_01);
        mUsername = getIntent().getStringExtra("username");
        mUsernameView.setText(mUsername);

        mUserInfo = mUserDao.getContact(mUsername);
        if (mUserInfo != null) {
            mAddFriendBtn.setVisibility(View.GONE);
            mStartChatBtn.setVisibility(View.VISIBLE);
            mVideoChatBtn.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case -1:
                    mActivity.finish();
                    break;
                case R.id.ml_btn_user_info_add:
                    addContact();
                    break;
                case R.id.ml_btn_user_info_chat_start:
                    startChat();
                    break;
                case R.id.ml_btn_user_info_chat_video:
                    MLToast.makeToast("暂时还未实现视频聊天").show();
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
                    EMContactManager.getInstance().addContact(mUsername, reason);
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
                            MLToast.makeToast("发送好友请求失败-_-||").show();
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
        intent.putExtra("username", mUsername);
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
