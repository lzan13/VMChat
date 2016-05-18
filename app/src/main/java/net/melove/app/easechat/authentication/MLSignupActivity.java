package net.melove.app.easechat.authentication;

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

import net.melove.app.easechat.R;
import net.melove.app.easechat.communal.base.MLBaseActivity;
import net.melove.app.easechat.application.MLConstants;
import net.melove.app.easechat.communal.util.MLLog;
import net.melove.app.easechat.communal.util.MLSPUtil;
import net.melove.app.easechat.communal.widget.MLToast;

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
        setContentView(R.layout.activity_sign_up);

        initView();
        initToolbar();

    }

    /**
     * 界面ＵＩ初始化方法，一般是为了先通过 findViewById 实例化控件
     */
    private void initView() {
        mActivity = this;
        mRootView = findViewById(R.id.ml_layout_coordinator);

        // 实例化控件
        mUsernameView = (EditText) findViewById(R.id.ml_edit_sign_up_username);
        mPasswordView = (EditText) findViewById(R.id.ml_edit_sign_up_password);

        mSignupBtn = findViewById(R.id.ml_btn_sign_up);
        mSignupBtn.setOnClickListener(viewListener);

    }


    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_sign_up);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(viewListener);
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case -1:
                onFinish();
                break;
            case R.id.ml_btn_sign_up:
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
        mDialog.setMessage(res.getString(R.string.ml_sign_up_begin));
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
                            MLToast.rightToast(R.string.ml_sign_up_success).show();
                            // 注册成功，返回登录界面
                            onFinish();
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
                            String error = "";
                            switch (errorCode) {
                            // 网络错误
                            case EMError.NETWORK_ERROR:
                                error = res.getString(R.string.ml_error_network_error) + "-" + errorCode;
                                break;
                            // 用户已存在
                            case EMError.USER_ALREADY_EXIST:
                                error = res.getString(R.string.ml_error_user_already_exits) + "-" + errorCode;
                                break;
                            // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                            case EMError.USER_ILLEGAL_ARGUMENT:
                                error = res.getString(R.string.ml_error_user_illegal_argument) + "-" + errorCode;
                                break;
                            // 服务器未知错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                error = res.getString(R.string.ml_error_server_unknown_error) + "-" + errorCode;
                                break;
                            case EMError.USER_REG_FAILED:
                                error = res.getString(R.string.ml_error_user_reg_failed) + "-" + errorCode;
                                break;
                            default:
                                error = res.getString(R.string.ml_sign_up_failed) + "-" + errorCode;
                                break;
                            }
                            MLToast.errorToast(error).show();
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
