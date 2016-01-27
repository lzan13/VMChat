package net.melove.demo.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.util.MLSPUtil;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLSignupActivity extends MLBaseActivity {

    private Toolbar mToolbar;

    private ProgressDialog mDialog;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;
    private String mPassword;

    private View mSignupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
        initToolbar();

    }

    private void init() {
        mActivity = this;

        mUsernameView = (EditText) findViewById(R.id.ml_edit_signup_username);
        mPasswordView = (EditText) findViewById(R.id.ml_edit_signup_password);

        mSignupBtn = findViewById(R.id.ml_btn_signup);
        mSignupBtn.setOnClickListener(viewListener);

    }


    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_signup);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_signup:
                    attemptSignup();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 检测登陆，主要先判断是否满足登陆条件
     */
    private void attemptSignup() {

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
            signup();
        }
    }

    /**
     * 注册方法
     */
    private void signup() {
        final Resources res = mActivity.getResources();
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(res.getString(R.string.ml_signup_begin));
        mDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().createAccountOnServer(mUsername, mPassword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mActivity.isFinishing()) {
                                mDialog.dismiss();
                            }
                            MLSPUtil.put(mActivity, MLConstants.ML_SHARED_USERNAME, mUsername);
                            MLToast.successToast(res.getString(R.string.ml_signup_success)).show();
                            signupToBmob();
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mActivity.isFinishing()) {
                                mDialog.dismiss();
                            }
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NONETWORK_ERROR) {
                                MLToast.errorToast(res.getString(R.string.ml_error_network_anomaly)).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                MLToast.errorToast(res.getString(R.string.ml_error_user_already_exits)).show();
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                MLToast.errorToast(res.getString(R.string.ml_error_signup_failed_unauthorized)).show();
                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                MLToast.errorToast(res.getString(R.string.ml_error_illegal_username)).show();
                            } else {
                                MLToast.errorToast(res.getString(R.string.ml_signup_failed)).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 注册用户信息到 Bmob 后端服务
     */
    private void signupToBmob(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
