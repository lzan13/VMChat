package com.vmloft.develop.app.chat.sign;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.AppActivity;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.app.chat.app.MainActivity;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

/**
 * Created by lzan13 on 2017/4/10.
 * App 安装后首次启动欢迎界面
 */
public class WelcomeActivity extends AppActivity {

    private String strVersion = "";

    @Override protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        activity = this;

        ButterKnife.bind(activity);

        init();
    }

    private void init() {
        // 获取 APP 当前运行版本
        String runVersion = (String) VMSPUtil.get(activity, Constants.SHARED_RUN_VERSION, "0.0.0");
        PackageManager packageManager = getPackageManager();
        try {
            // 获取当前 APP 版本
            strVersion = packageManager.getPackageInfo(getPackageName(), 0).versionName;
            // 如果版本不同，则显示引导界面
            if (!runVersion.equals(strVersion)) {
                initWelcomeView();
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

    /**
     * 按钮点击监听
     */
    @OnClick(R.id.btn_go_on) void onClick() {
        VMSPUtil.put(activity, Constants.SHARED_RUN_VERSION, strVersion);
    }
}
