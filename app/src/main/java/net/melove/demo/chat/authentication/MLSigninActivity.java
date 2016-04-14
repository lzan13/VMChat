package net.melove.demo.chat.authentication;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import net.melove.demo.chat.R;
import net.melove.demo.chat.communal.base.MLBaseActivity;
import net.melove.demo.chat.main.MLMainActivity;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.communal.util.MLLog;
import net.melove.demo.chat.communal.util.MLSPUtil;
import net.melove.demo.chat.communal.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 * App 进行登录界面，登录逻辑处理类
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initToolbar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    private void init() {

        mUsername = (String) MLSPUtil.get(mActivity, MLConstants.ML_SHARED_USERNAME, "");
        mPassword = (String) MLSPUtil.get(mActivity, MLConstants.ML_SHARED_PASSWORD, "");
        mUsernameView = (EditText) findViewById(R.id.ml_edit_sign_in_username);
        mPasswordView = (EditText) findViewById(R.id.ml_edit_sign_in_password);
        mUsernameView.setText(mUsername);
        mPasswordView.setText(mPassword);

        mSigninBtn = findViewById(R.id.ml_btn_sign_in);
        mSignupBtn = findViewById(R.id.ml_btn_sign_up);
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

        mToolbar.setTitle(R.string.ml_sign_in);
        // TODO 这里设置Toolbar的图标时如果使用svg图标,在5.x上才能支持，在4.x的设备 svg 图标会显示，但是颜色是黑色不能改变，
        //        mToolbar.setNavigationIcon(R.drawable.ic_menu_close);
        mToolbar.setNavigationIcon(R.mipmap.ic_close_white_24dp);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(viewListener);
    }

    /**
     * 界面内控件的点击事件监听器
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case -1:
                onFinish();
                break;
            case R.id.ml_btn_sign_in:
                attemptLogin();
                break;
            case R.id.ml_btn_sign_up:
                Intent intent = new Intent(mActivity, MLSignupActivity.class);
                superJump(intent);
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
        mDialog.setMessage(res.getString(R.string.ml_sign_in_begin));
        mDialog.show();

        EMClient.getInstance().login(mUsername, mPassword, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        mDialog.dismiss();

                        // 登录成功，把用户名和密码保存在本地（可以不保存，根据自己的需求）
                        MLSPUtil.put(mActivity, MLConstants.ML_SHARED_USERNAME, mUsername);
                        MLSPUtil.put(mActivity, MLConstants.ML_SHARED_PASSWORD, mPassword);

                        // 加载所有会话到内存
                        EMClient.getInstance().chatManager().loadAllConversations();
                        // 加载所有群组到内存
                        EMClient.getInstance().groupManager().loadAllGroups();

                        Intent intent = new Intent(mActivity, MLMainActivity.class);
                        superJump(intent);
                        // 根据不同的系统版本选择不同的 finish 方法
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            mActivity.finish();
                        } else {
                            mActivity.finishAfterTransition();
                        }
                    }
                });
            }

            /**
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
                        MLLog.d("Error: " + i + " " + res.getString(R.string.ml_sign_in_failed) + s);
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                        // 网络异常 2
                        case EMError.NETWORK_ERROR:
                            MLToast.errorToast(res.getString(R.string.ml_error_network_error) + "-" + i).show();
                            break;
                        // 无效的用户名 101
                        case EMError.INVALID_USER_NAME:
                            MLToast.errorToast(res.getString(R.string.ml_error_invalid_user_name) + "-" + i).show();
                            break;
                        // 无效的密码 102
                        case EMError.INVALID_PASSWORD:
                            MLToast.errorToast(res.getString(R.string.ml_error_invalid_password) + "-" + i).show();
                            break;
                        // 用户认证失败，用户名或密码错误 202
                        case EMError.USER_AUTHENTICATION_FAILED:
                            MLToast.errorToast(res.getString(R.string.ml_error_user_authentication_failed) + "-" + i).show();
                            break;
                        // 用户不存在 204
                        case EMError.USER_NOT_FOUND:
                            MLToast.errorToast(res.getString(R.string.ml_error_user_not_found) + "-" + i).show();
                            break;
                        // 无法访问到服务器 300
                        case EMError.SERVER_NOT_REACHABLE:
                            MLToast.errorToast(res.getString(R.string.ml_error_server_not_reachable) + "-" + i).show();
                            break;
                        // 等待服务器响应超时 301
                        case EMError.SERVER_TIMEOUT:
                            MLToast.errorToast(res.getString(R.string.ml_error_server_timeout) + "-" + i).show();
                            break;
                        // 服务器繁忙 302
                        case EMError.SERVER_BUSY:
                            MLToast.errorToast(res.getString(R.string.ml_error_server_busy) + "-" + i).show();
                            break;
                        // 未知 Server 异常 303
                        case EMError.SERVER_UNKNOWN_ERROR:
                            MLToast.errorToast(res.getString(R.string.ml_error_server_unknown_error) + "-" + i).show();
                            break;
                        default:
                            MLToast.errorToast(res.getString(R.string.ml_sign_in_failed) + "-" + i).show();
                            break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {
                MLLog.d("progress: " + i + " " + res.getString(R.string.ml_sign_in_begin) + s);

            }
        });
    }


    private void forgetPassword() {
        MLToast.errorToast("暂不支持找回密码").show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


