package com.vmloft.develop.app.chat.sign;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.base.AppActivity;
import com.vmloft.develop.app.chat.base.MainActivity;
import com.vmloft.develop.app.chat.contacts.UserManager;
import com.vmloft.develop.app.chat.network.NetworkManager;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2015/7/4.
 * App 进行登录界面，登录逻辑处理类
 */
public class SignInActivity extends AppActivity {

    // loading 等待对话框
    private ProgressDialog progressDialog;

    // 输入框
    @BindView(R.id.edit_username) EditText usernameEditView;
    @BindView(R.id.edit_password) EditText passwordEditView;
    @BindView(R.id.btn_sign_in) Button signInBtn;

    private String username;
    private String password;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(activity);

        initView();
    }

    /**
     * 界面UI初始化方法，一般是为了先通过 findViewById 实例化控件
     */
    private void initView() {
        setSupportActionBar(getToolbar());
        getToolbar().setTitle(R.string.sign_in);
        // TODO 这里设置Toolbar的图标时如果使用svg图标,在5.x上才能支持，在4.x的设备 svg 图标会显示，但是颜色是黑色不能改变，
        // mToolbar.setNavigationIcon(R.drawable.ic_menu_close);
        getToolbar().setNavigationIcon(R.drawable.ic_close_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onFinish();
            }
        });

        // 读取最后一次登录的账户 Username
        username = (String) VMSPUtil.get(activity, AConstants.SHARED_USERNAME, "");
        usernameEditView.setText(username);

        usernameEditView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyInputBox();
            }

            @Override public void afterTextChanged(Editable s) {
            }
        });
        passwordEditView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyInputBox();
            }

            @Override public void afterTextChanged(Editable s) {
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
                restAuth();
                break;
            case R.id.btn_sign_up:
                Intent intent = new Intent(activity, SignUpActivity.class);
                onStartActivity(activity, intent);
                break;
            case R.id.btn_forget_password:
                forgetPassword();
                break;
        }
    }

    /**
     * 校验输入框
     */
    private void verifyInputBox() {

        // 将用户名转为消息并修剪
        username = usernameEditView.getText().toString().toLowerCase().trim();
        password = passwordEditView.getText().toString().trim();

        // 检查输入框是否为空是否为空
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            signInBtn.setEnabled(false);
            signInBtn.setAlpha(0.5f);
        } else {
            signInBtn.setEnabled(true);
            signInBtn.setAlpha(1.0f);
        }
    }

    /**
     * rest 认证
     */
    private void restAuth() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.sign_in_begin));
        progressDialog.show();

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    JSONObject object = NetworkManager.getInstance().authToken(username, password);
                    final int errorCode = object.optInt("code");
                    final String errorMsg = object.optString("msg");
                    if (errorCode != 0) {
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                // 关闭登录进度弹出框
                                progressDialog.dismiss();
                                Snackbar.make(getRootView(), getString(R.string.sign_in_failed)
                                        + " - "
                                        + errorCode
                                        + " - "
                                        + errorMsg, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    // 解析账户 token 保存
                    String token = object.optJSONObject("data").optString("access_token");
                    VMSPUtil.put(activity, AConstants.USER_ACCESS_TOKEN, token);
                    // rest 认证成功后调用 sdk 登录方法
                    signIn();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 调用 sdk 登录
     */
    private void signIn() {
        EMClient.getInstance().login(username, password, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override public void onSuccess() {
                // 登录成功，把用户名保存在本地（可以不保存，根据自己的需求）
                VMSPUtil.put(activity, AConstants.SHARED_USERNAME, username);
                // 登录成功同步联系人到本地
                UserManager.getInstance().syncContactsFromServer();
                // 加载所有会话到内存
                EMClient.getInstance().chatManager().loadAllConversations();
                // 加载所有群组到内存
                EMClient.getInstance().groupManager().loadAllGroups();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        // 关闭登录进度弹出框
                        progressDialog.dismiss();
                        // 登录成功跳转到主界面
                        Intent intent = new Intent(activity, MainActivity.class);
                        onStartActivity(activity, intent);
                        // 根据不同的系统版本选择不同的 finish 方法
                        onFinish();
                    }
                });
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
                        progressDialog.dismiss();
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        String error = "";
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                error = getString(R.string.error_network_error);
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                error = getString(R.string.error_invalid_user_name);
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                error = getString(R.string.error_invalid_password);
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                error = getString(R.string.error_user_authentication_failed);
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                error = getString(R.string.error_user_not_found);
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                error = getString(R.string.error_server_not_reachable);
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                error = getString(R.string.error_server_timeout);
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                error = getString(R.string.error_server_busy);
                                break;
                            // 未知 Server 异常 303
                            case EMError.SERVER_UNKNOWN_ERROR:
                                error = getString(R.string.error_server_unknown_error);
                                break;
                            default:
                                error = getString(R.string.sign_in_failed);
                                break;
                        }
                        Snackbar.make(getRootView(), error + " - " + i + " - " + s,
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onProgress(int i, String s) {
                VMLog.d("progress: " + i + " " + getString(R.string.sign_in_begin) + s);
            }
        });
    }

    /**
     * 找回密码触发方法
     */
    private void forgetPassword() {
        Snackbar.make(getRootView(), "暂不支持找回密码", Snackbar.LENGTH_SHORT).show();
    }

    @Override protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
 
