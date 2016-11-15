package net.melove.app.chat.ui.sign;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.ui.MLBaseActivity;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.util.MLSPUtil;

/**
 * Created by lzan13 on 2015/7/4.
 * 注册逻辑处理类
 */
public class MLSignUpActivity extends MLBaseActivity {

    // 调用注册按钮时显示的进度对话框
    private ProgressDialog mDialog;

    // 输入框
    @BindView(R.id.edit_username) EditText mUsernameView;
    @BindView(R.id.edit_password) EditText mPasswordView;
    // 用户名和密码
    private String mUsername;
    private String mPassword;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mActivity = this;

        ButterKnife.bind(mActivity);

        initView();
    }

    /**
     * 界面ＵＩ初始化方法，一般是为了先通过 findViewById 实例化控件
     */
    private void initView() {
        setSupportActionBar(getToolbar());
        getToolbar().setTitle(R.string.ml_sign_up);
        getToolbar().setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onFinish();
            }
        });
    }

    @OnClick(R.id.btn_sign_up) void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                onFinish();
                break;
            case R.id.btn_sign_up:
                attemptSignUp();
                break;
        }
    }

    /**
     * 检测登陆，主要先判断是否满足登陆条件
     */
    private void attemptSignUp() {

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
            signUp();
        }
    }

    /**
     * 注册方法
     */
    private void signUp() {
        final Resources res = mActivity.getResources();
        // 注册是耗时过程，所以要显示一个dialog来提示下用户
        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(res.getString(R.string.ml_sign_up_begin));
        mDialog.show();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMClient.getInstance().createAccount(mUsername, mPassword);
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!mActivity.isFinishing()) {
                                mDialog.dismiss();
                            }
                            // 注册成功保存用户名到本地
                            MLSPUtil.put(mActivity, MLConstants.ML_SHARED_USERNAME, mUsername);
                            Snackbar.make(getRootView(), R.string.ml_sign_up_success,
                                    Snackbar.LENGTH_SHORT).show();
                            // 注册成功，返回登录界面
                            onFinish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!mActivity.isFinishing()) {
                                mDialog.dismiss();
                            }
                            int errorCode = e.getErrorCode();
                            MLLog.d("MLSignUpActivity - signUp - errorCode:%d, errorMsg:%s",
                                    errorCode, e.getMessage());
                            String error = "";
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    error = res.getString(R.string.ml_error_network_error);
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    error = res.getString(R.string.ml_error_user_already_exits);
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    error = res.getString(R.string.ml_error_user_illegal_argument);
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    error = res.getString(R.string.ml_error_server_unknown_error);
                                    break;
                                case EMError.USER_REG_FAILED:
                                    error = res.getString(R.string.ml_error_user_reg_failed);
                                    break;
                                default:
                                    error = res.getString(R.string.ml_sign_up_failed);
                                    break;
                            }
                            Snackbar.make(getRootView(),
                                    error + "-" + errorCode + "-" + e.getMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override protected void onResume() {
        super.onResume();
    }
}
