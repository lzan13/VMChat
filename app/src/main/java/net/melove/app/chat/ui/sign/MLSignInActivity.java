package net.melove.app.chat.ui.sign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import net.melove.app.chat.R;
import net.melove.app.chat.ui.MLBaseActivity;
import net.melove.app.chat.ui.MLMainActivity;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.ui.contacts.MLUserManager;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.util.MLSPUtil;

/**
 * Created by lzan13 on 2015/7/4.
 * App 进行登录界面，登录逻辑处理类
 */
public class MLSignInActivity extends MLBaseActivity {

    // loading 等待对话框
    private ProgressDialog mDialog;

    // 输入框
    @BindView(R.id.edit_username) EditText mUsernameView;
    @BindView(R.id.edit_password) EditText mPasswordView;
    private String mUsername;
    private String mPassword;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mActivity = this;

        ButterKnife.bind(mActivity);

        initView();
    }

    /**
     * 界面UI初始化方法，一般是为了先通过 findViewById 实例化控件
     */
    private void initView() {
        setSupportActionBar(getToolbar());
        getToolbar().setTitle(R.string.ml_sign_in);
        // TODO 这里设置Toolbar的图标时如果使用svg图标,在5.x上才能支持，在4.x的设备 svg 图标会显示，但是颜色是黑色不能改变，
        // mToolbar.setNavigationIcon(R.drawable.ic_menu_close);
        getToolbar().setNavigationIcon(R.mipmap.ic_close_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onFinish();
            }
        });
    }

    /**
     * 界面内控件的点击事件监听器
     */
    @OnClick({ R.id.btn_sign_in, R.id.btn_sign_up, R.id.btn_forget_password }) void onClick(
            View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                attemptLogin();
                break;
            case R.id.btn_sign_up:
                Intent intent = new Intent(mActivity, MLSignUpActivity.class);
                superJump(intent);
                break;
            case R.id.btn_forget_password:
                forgetPassword();
                break;
        }
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
            // 输入框获取焦点
            focusView.requestFocus();
        } else {
            signIn();
        }
    }

    /**
     * 登录到环信服务器处理
     */
    private void signIn() {
        final Resources res = mActivity.getResources();

        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(res.getString(R.string.ml_sign_in_begin));
        mDialog.show();

        EMClient.getInstance().login(mUsername, mPassword, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override public void onSuccess() {
                // 登录成功同步联系人到本地
                MLUserManager.getInstance().syncContactsFromServer();

                // 登录成功，把用户名保存在本地（可以不保存，根据自己的需求）
                MLSPUtil.put(MLConstants.ML_SHARED_USERNAME, mUsername);
                // 加载所有会话到内存
                EMClient.getInstance().chatManager().loadAllConversations();
                // 加载所有群组到内存
                EMClient.getInstance().groupManager().loadAllGroups();

                // 关闭登录进度弹出框
                mDialog.dismiss();

                // 登录成功跳转到主界面
                Intent intent = new Intent(mActivity, MLMainActivity.class);
                superJump(intent);

                // 根据不同的系统版本选择不同的 finish 方法
                onFinish();
            }

            /**
             * 登陆错误的回调
             *
             * @param i
             * @param s
             */
            @Override public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        mDialog.dismiss();
                        MLLog.d("Error: "
                                + i
                                + " "
                                + res.getString(R.string.ml_sign_in_failed)
                                + s);
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        String error = "";
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                error = res.getString(R.string.ml_error_network_error);
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                error = res.getString(R.string.ml_error_invalid_user_name);
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                error = res.getString(R.string.ml_error_invalid_password);
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                error = res.getString(R.string.ml_error_user_authentication_failed);
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                error = res.getString(R.string.ml_error_user_not_found);
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                error = res.getString(R.string.ml_error_server_not_reachable);
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                error = res.getString(R.string.ml_error_server_timeout);
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                error = res.getString(R.string.ml_error_server_busy);
                                break;
                            // 未知 Server 异常 303
                            case EMError.SERVER_UNKNOWN_ERROR:
                                error = res.getString(R.string.ml_error_server_unknown_error);
                                break;
                            default:
                                error = res.getString(R.string.ml_sign_in_failed);
                                break;
                        }
                        Snackbar.make(getRootView(), error + "-" + i + "-" + s,
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onProgress(int i, String s) {
                MLLog.d("progress: " + i + " " + res.getString(R.string.ml_sign_in_begin) + s);
            }
        });
    }

    /**
     * 找回密码触发方法
     */
    private void forgetPassword() {
        Snackbar.make(getRootView(), "暂不支持找回密码", Snackbar.LENGTH_SHORT).show();
    }

    @Override protected void onResume() {
        super.onResume();
        // 读取最后一次登录的账户 Username
        mUsername = (String) MLSPUtil.get(MLConstants.ML_SHARED_USERNAME, "");
        mUsernameView.setText(mUsername);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }
}
 
