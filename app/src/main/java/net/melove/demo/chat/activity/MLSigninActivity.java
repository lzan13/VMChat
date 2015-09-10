package net.melove.demo.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easemob.EMCallBack;
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

    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
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

    private void init() {
        mActivity = this;

        mUsername = (String) MLSPUtil.get(mActivity, "username", "");
        mPassword = (String) MLSPUtil.get(mActivity, "password", "");
        mUsernameEdit = (EditText) findViewById(R.id.ml_edit_username);
        mPasswordEdit = (EditText) findViewById(R.id.ml_edit_password);
        mUsernameEdit.setText(mUsername);
        mPasswordEdit.setText(mPassword);

        mSigninBtn = findViewById(R.id.ml_signin);
        mSignupBtn = findViewById(R.id.ml_signup);
        mForgetBtn = findViewById(R.id.ml_forget_password);

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

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_signin:
                    signin();
                    break;
                case R.id.ml_signup:
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MLSignupActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity,
                            R.anim.ml_anim_slide_right_in, R.anim.ml_anim_slide_left_out);
                    ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                    break;
                case R.id.ml_forget_password:

                    break;
            }
        }
    };


    private void signin() {
        final Resources res = mActivity.getResources();
        mUsername = mUsernameEdit.getText().toString().toLowerCase().trim();
        mPassword = mPasswordEdit.getText().toString().toLowerCase().trim();
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

        mDialog = new ProgressDialog(mActivity);
        mDialog.setMessage(res.getString(R.string.ml_signin_begin));
        mDialog.show();

        EMChatManager.getInstance().login(mUsername, mPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        MLToast.makeToast(R.mipmap.icon_emotion_smile_24dp, res.getString(R.string.ml_signin_success)).show();
                        MLLog.d(res.getString(R.string.ml_signin_success));

                        MLSPUtil.put(mActivity, MLConstants.ML_C_USERNAME, mUsername);
                        MLSPUtil.put(mActivity, MLConstants.ML_C_PASSWORD, mPassword);

                        Intent intent = new Intent();
                        intent.setClass(mActivity, MLMainActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        MLToast.makeToast("Error: " + i + res.getString(R.string.ml_signin_failed)).show();
                        MLLog.d("Error: " + i + " " + res.getString(R.string.ml_signin_failed) + s);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


