package net.melove.demo.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.util.MLSPUtil;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLSignupActivity extends MLBaseActivity {


    private Activity mActivity;
    private Toolbar mToolbar;

    private ProgressDialog mDialog;

    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private EditText mConfirmPasswordEdit;
    private String mUsername;
    private String mPassword;
    private String mConfirmPassword;

    private View mSignupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mActivity = this;
        init();
        initToolbar();

    }

    private void init() {
        mActivity = this;

        mUsernameEdit = (EditText) findViewById(R.id.ml_edit_username);
        mPasswordEdit = (EditText) findViewById(R.id.ml_edit_password);
        mConfirmPasswordEdit = (EditText) findViewById(R.id.ml_edit_confirm_password);

        mSignupBtn = findViewById(R.id.ml_signup);
        mSignupBtn.setOnClickListener(viewListener);

    }


    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_signup);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.icon_arrow_back_white_24dp);
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
                case R.id.ml_signup:
                    signup();
                    break;
                default:
                    break;
            }
        }
    };

    private void signup() {
        final Resources res = mActivity.getResources();
        mUsername = mUsernameEdit.getText().toString().toLowerCase().trim();
        mPassword = mPasswordEdit.getText().toString().toLowerCase().trim();
        mConfirmPassword = mConfirmPasswordEdit.getText().toString().toLowerCase().trim();
        if (TextUtils.isEmpty(mUsername)) {
            MLToast.makeToast(res.getString(R.string.ml_username_cannot_to_empty)).show();
            mUsernameEdit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mPassword)) {
            MLToast.makeToast(res.getString(R.string.ml_password_cannot_to_empty)).show();
            mPasswordEdit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mConfirmPassword)) {
            MLToast.makeToast(res.getString(R.string.ml_cannot_to_empty)).show();
            mConfirmPasswordEdit.requestFocus();
            return;
        }
        if (!mPassword.equals(mConfirmPassword)) {
            MLToast.makeToast(res.getString(R.string.ml_please_confirm_password)).show();
            mConfirmPasswordEdit.requestFocus();
            return;
        }
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
                            MLSPUtil.put(mActivity, "username", mUsername);
                            MLSPUtil.put(mActivity, "password", "");
                            MLToast.makeToast(R.mipmap.icon_emotion_smile_24dp, res.getString(R.string.ml_signup_success)).show();
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
                                MLToast.makeToast(res.getString(R.string.ml_network_anomaly)).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                MLToast.makeToast(res.getString(R.string.ml_user_already_exits)).show();
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                MLToast.makeToast(res.getString(R.string.ml_signup_failed_unauthorized)).show();
                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                MLToast.makeToast(res.getString(R.string.ml_illegal_username)).show();
                            } else {
                                MLToast.makeToast(res.getString(R.string.ml_signup_failed)).show();
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
