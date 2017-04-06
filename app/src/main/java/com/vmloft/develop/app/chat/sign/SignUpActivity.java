package com.vmloft.develop.app.chat.sign;

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

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.AppActivity;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.app.chat.network.NetworkManager;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzan13 on 2015/7/4.
 * 注册逻辑处理类
 */
public class SignUpActivity extends AppActivity {

    // 调用注册按钮时显示的进度对话框
    private ProgressDialog progressDialog;

    // 输入框
    @BindView(R.id.edit_username) EditText usernameView;
    @BindView(R.id.edit_password) EditText passwordView;
    // 用户名和密码
    private String username;
    private String password;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        activity = this;

        ButterKnife.bind(activity);

        initView();
    }

    /**
     * 界面ＵＩ初始化方法，一般是为了先通过 findViewById 实例化控件
     */
    private void initView() {
        setSupportActionBar(getToolbar());
        getToolbar().setTitle(R.string.sign_up);
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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
        usernameView.setError(null);
        passwordView.setError(null);

        username = usernameView.getText().toString().toLowerCase().trim();
        password = passwordView.getText().toString().toLowerCase().trim();

        boolean cancel = false;
        View focusView = null;

        // 检查密码是否为空
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        // 检查username是否为空
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            restSignUp();
        }
    }

    /**
     * rest 注册方法，这个主要是自己的服务器调用环信 rest 接口，app 请求自己的服务器进行注册
     */
    private void restSignUp() {
        final Resources res = activity.getResources();
        // 注册是耗时过程，所以要显示一个dialog来提示下用户
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.sign_up_begin));
        progressDialog.show();
        new Thread(new Runnable() {
            @Override public void run() {
                if (!activity.isFinishing()) {
                    progressDialog.dismiss();
                }
                try {
                    JSONObject object =
                            NetworkManager.getInstance().createUser(username, password);
                    int errorCode = object.optInt("code");
                    String errorMsg = object.optString("msg");
                    if (errorCode != 0) {
                        Snackbar.make(getRootView(), getString(R.string.sign_up_failed)
                                + " - "
                                + errorCode
                                + " - "
                                + errorMsg, Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            // 注册成功保存用户名到本地
                            VMSPUtil.put(activity, Constants.SHARED_USERNAME, username);
                            Snackbar.make(getRootView(), R.string.sign_up_success,
                                    Snackbar.LENGTH_SHORT).show();
                            // 注册成功，返回登录界面
                            onFinish();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * sdk 注册方法
     */
    private void signUp() {
        // 注册是耗时过程，所以要显示一个dialog来提示下用户
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.sign_up_begin));
        progressDialog.show();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMClient.getInstance().createAccount(username, password);
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!activity.isFinishing()) {
                                progressDialog.dismiss();
                            }
                            // 注册成功保存用户名到本地
                            VMSPUtil.put(activity, Constants.SHARED_USERNAME, username);
                            Snackbar.make(getRootView(), R.string.sign_up_success,
                                    Snackbar.LENGTH_SHORT).show();
                            // 注册成功，返回登录界面
                            onFinish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (!activity.isFinishing()) {
                                progressDialog.dismiss();
                            }
                            int errorCode = e.getErrorCode();
                            VMLog.d("SignUpActivity - signUp - errorCode:%d, errorMsg:%s",
                                    errorCode, e.getMessage());
                            String error = "";
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    error = getString(R.string.error_network_error);
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    error = getString(R.string.error_user_already_exits);
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    error = getString(R.string.error_user_illegal_argument);
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    error = getString(R.string.error_server_unknown_error);
                                    break;
                                case EMError.USER_REG_FAILED:
                                    error = getString(R.string.error_user_reg_failed);
                                    break;
                                default:
                                    error = getString(R.string.sign_up_failed);
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
