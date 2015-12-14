package net.melove.demo.chat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.util.MLSPUtil;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLSigninActivity extends MLBaseActivity {

    private Toolbar mToolbar;

    private ProgressDialog mDialog;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;
    private String mPassword;

    private View mSigninBtn;
    private View mSignupBtn;
    private View mForgetBtn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initToolbar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    private void init() {

        mUsername = (String) MLSPUtil.get(mActivity, "username", "");
        mPassword = (String) MLSPUtil.get(mActivity, "password", "");
        mUsernameView = (EditText) findViewById(R.id.ml_edit_login_username);
        mPasswordView = (EditText) findViewById(R.id.ml_edit_login_password);
        mUsernameView.setText(mUsername);
        mPasswordView.setText(mPassword);

        mSigninBtn = findViewById(R.id.ml_btn_signin);
        mSignupBtn = findViewById(R.id.ml_btn_signup);
        mForgetBtn = findViewById(R.id.ml_btn_forget_password);

        mSigninBtn.setOnClickListener(viewListener);
        mSignupBtn.setOnClickListener(viewListener);
        mForgetBtn.setOnClickListener(viewListener);

    }


    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_signin);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_close);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    /**
     * 界面内控件的点击事件监听器
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_signin:
                    attemptLogin();
                    break;
                case R.id.ml_btn_signup:
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MLSignupActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mToolbar, "toolbar");
                    ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                    break;
                case R.id.ml_btn_forget_password:
                    forgetPassword();
                    break;
            }
        }
    };

    /**
     * 检测登陆，主要先判断是否满足登陆条件
     */
    private void attemptLogin() {

        // 重置错误
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        mUsername = mUsernameView.getText().toString().toLowerCase().trim();
        mPassword = mPasswordView.getText().toString().toLowerCase().trim();

        boolean cancel = false;
        View focusView = null;

        // 检查密码是否为空
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.ml_error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // 检查username是否为空
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.ml_error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            signin();
        }
    }

    /**
     * by lzan13 2015-10-2 18:03:53
     * 登录到环信服务器处理
     */
    private void signin() {
        final Resources res = mActivity.getResources();

        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(res.getString(R.string.ml_signin_begin));
        mDialog.show();

        EMChatManager.getInstance().login(mUsername, mPassword, new EMCallBack() {
            /**
             * by lzan13 2015-10-28 18:05:12
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();

                        // 登录成功，把用户名和密码保存在本地（可以不保存，根据自己的需求）
                        MLSPUtil.put(mActivity, MLConstants.ML_C_USERNAME, mUsername);
                        MLSPUtil.put(mActivity, MLConstants.ML_C_PASSWORD, mPassword);

                        // 加载所有会话到内存
                        EMChatManager.getInstance().loadAllConversations();
                        // 加载所有群组到内存
                        EMGroupManager.getInstance().loadAllGroups();

                        Intent intent = new Intent();
                        intent.setClass(mActivity, MLMainActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mToolbar, "toolbar");
                        ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                        finishAfterTransition();
                    }
                });
            }

            /**
             * by lzan13 2015-10-28 18:05:30
             * 登陆错误的回调
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        MLLog.d("Error: " + i + " " + res.getString(R.string.ml_signin_failed) + s);
                        switch (i) {
                            case EMError.INVALID_PASSWORD_USERNAME:
                                MLToast.makeToast(R.mipmap.ic_emotion_sad_24dp, res.getString(R.string.ml_error_invalid_username_or_password) + "(" + i + ")").show();
                                break;
                            case EMError.UNABLE_CONNECT_TO_SERVER:
                                MLToast.makeToast(R.mipmap.ic_emotion_sad_24dp, res.getString(R.string.ml_error_network_anomaly) + "(" + i + ")").show();
                                break;
                            case EMError.DNS_ERROR:
                                MLToast.makeToast(R.mipmap.ic_emotion_sad_24dp, res.getString(R.string.ml_error_network_dns_error) + "(" + i + ")").show();
                                break;
                            case EMError.CONNECT_TIMER_OUT:
                                MLToast.makeToast(R.mipmap.ic_emotion_sad_24dp, res.getString(R.string.ml_error_connect_time_out) + "(" + i + ")").show();
                                break;
                            default:
                                MLToast.makeToast(R.mipmap.ic_emotion_sad_24dp, res.getString(R.string.ml_error_signin_error) + "(" + i + ")").show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
                MLLog.d("progress: " + i + " " + res.getString(R.string.ml_signin_begin) + s);

            }
        });

    }


    private void forgetPassword() {
        MLToast.makeToast(R.mipmap.ic_emotion_sad_24dp, "暂不支持找回密码").show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


