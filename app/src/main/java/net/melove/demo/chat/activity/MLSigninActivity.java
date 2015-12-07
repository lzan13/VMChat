package net.melove.demo.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.util.MLSPUtil;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLSigninActivity extends MLBaseActivity {

    private Activity mActivity;
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

    private void init() {
        mActivity = this;

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
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.icon_close_white_24dp);
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
                    signin();
                    break;
                case R.id.ml_btn_signup:
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MLSignupActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity,
                            R.anim.ml_anim_slide_right_in, R.anim.ml_anim_slide_left_out);
                    ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                    break;
                case R.id.ml_btn_forget_password:
                    forgetPassword();
                    break;
            }
        }
    };


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
//                        mDialog.setMessage(res.getString(R.string.ml_signin_success) + ";" + res.getString(R.string.ml_load_data) + "…");
                        mDialog.dismiss();

                        MLSPUtil.put(mActivity, MLConstants.ML_C_USERNAME, mUsername);
                        MLSPUtil.put(mActivity, MLConstants.ML_C_PASSWORD, mPassword);

                        Intent intent = new Intent();
                        intent.setClass(mActivity, MLMainActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();
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
                                MLToast.makeToast(R.mipmap.icon_emotion_sad_24dp, res.getString(R.string.ml_invalid_username_or_password) + ":" + i).show();
                                break;
                            case EMError.UNABLE_CONNECT_TO_SERVER:
                                MLToast.makeToast(R.mipmap.icon_emotion_sad_24dp, res.getString(R.string.ml_network_anomaly) + ":" + i).show();
                                break;
                            case EMError.DNS_ERROR:
                                MLToast.makeToast(R.mipmap.icon_emotion_sad_24dp, res.getString(R.string.ml_network_dns_error) + ":" + i).show();
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
        Snackbar.make(mActivity.getWindow().getDecorView(), "暂不支持找回密码", Snackbar.LENGTH_LONG).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


