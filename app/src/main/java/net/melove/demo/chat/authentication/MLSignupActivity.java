package net.melove.demo.chat.authentication;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.base.MLBaseActivity;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.util.MLSPUtil;
import net.melove.demo.chat.common.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 * 注册逻辑处理类
 */
public class MLSignupActivity extends MLBaseActivity {
    // Toolbar 控件
    private Toolbar mToolbar;
    // 调用注册按钮时显示的进度对话框
    private ProgressDialog mDialog;

    // 用户名和密码输入框
    private EditText mUsernameView;
    private EditText mPasswordView;
    // 用户名和密码
    private String mUsername;
    private String mPassword;

    // 注册按钮
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
        // 实例化控件
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
        // 注册是耗时过程，所以要显示一个dialog来提示下用户
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(res.getString(R.string.ml_signup_begin));
        mDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mUsername, mPassword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mActivity.isFinishing()) {
                                mDialog.dismiss();
                            }
                            // 注册成功保存用户名到本地
                            MLSPUtil.put(mActivity, MLConstants.ML_SHARED_USERNAME, mUsername);
                            MLToast.rightToast(res.getString(R.string.ml_signup_success)).show();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mActivity.isFinishing()) {
                                mDialog.dismiss();
                            }
                            int errorCode = e.getErrorCode();
                            MLLog.d("MLSignupActivity - signup - errorCode:%d, errorMsg:%s", errorCode, e.getMessage());
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    MLToast.errorToast(res.getString(R.string.ml_error_network_error) + "-" + errorCode).show();
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    MLToast.errorToast(res.getString(R.string.ml_error_user_already_exits) + "-" + errorCode).show();
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    MLToast.errorToast(res.getString(R.string.ml_error_user_illegal_argument) + "-" + errorCode).show();
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    MLToast.errorToast(res.getString(R.string.ml_error_server_unknown_error) + "-" + errorCode).show();
                                    break;
                                case EMError.USER_REG_FAILED:
                                    MLToast.errorToast(res.getString(R.string.ml_error_user_reg_failed) + "-" + errorCode).show();
                                    break;
                                default:
                                    MLToast.errorToast(res.getString(R.string.ml_signup_failed) + "-" + errorCode).show();
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
