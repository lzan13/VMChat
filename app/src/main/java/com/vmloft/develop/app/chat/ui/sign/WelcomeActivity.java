package com.vmloft.develop.app.chat.ui.sign;

import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.hyphenate.chat.EMClient;
import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.base.AppActivity;
import com.vmloft.develop.app.chat.ui.main.MainActivity;
import com.vmloft.develop.app.chat.util.AUtil;

/**
 * Created by lzan13 on 2017/4/10.
 * App 安装后首次启动欢迎界面
 */
public class WelcomeActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        activity = this;

        ButterKnife.bind(activity);

        init();
    }

    private void init() {
        if (AUtil.isShowGuide()) {
            initWelcomeView();
        }

        Intent intent = new Intent();
        // 判断是否已经登录成功过
        if (EMClient.getInstance().isLoggedInBefore()) {
            // 加载群组到内存
            EMClient.getInstance().groupManager().loadAllGroups();
            // 加载所有本地会话到内存
            EMClient.getInstance().chatManager().loadAllConversations();

            // 跳转到登录界面
            intent.setClass(this, MainActivity.class);
            onStartActivity(this, intent);
            onFinish();
        } else {
            // 跳转到登录界面
            intent.setClass(this, SignInActivity.class);
            onStartActivity(this, intent);
            onFinish();
        }
    }

    private void initWelcomeView() {
    }

    @OnClick(R.id.btn_go_on)
    void onClick() {

    }
}
