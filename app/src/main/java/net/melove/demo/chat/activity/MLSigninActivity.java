package net.melove.demo.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.melove.demo.chat.R;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/4.
 */
public class MLSigninActivity extends MLBaseActivity {

    private Activity mActivity;
    private Toolbar mToolbar;

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
        init();
        initToolbar();

    }

    private void init() {
        mActivity = this;

        mUsernameEdit = (EditText) findViewById(R.id.ml_edit_username);
        mPasswordEdit = (EditText) findViewById(R.id.ml_edit_password);

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
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(
                            mActivity, R.anim.ml_fade_in, R.anim.ml_fade_out);
                    ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());

                    break;
                case R.id.ml_forget_password:

                    break;
            }
        }
    };


    private void signin() {
        Resources res = mActivity.getResources();
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
    }


    private void forgetPassword() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


