package com.vmloft.develop.app.chat.setting;

import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.app.chat.app.AppActivity;
import com.vmloft.develop.app.chat.R;

/**
 * Created by lzan13 on 2016/10/31.
 * 应用设置界面
 */
public class SettingsActivity extends AppActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(activity);

        initView();
    }

    /**
     * UI 界面初始化
     */
    private void initView() {
        setSupportActionBar(getToolbar());
        getToolbar().setTitle(R.string.settings);
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onFinish();
            }
        });
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.layout_container, new SettingsPreference())
                .commit();

        // 更新推送昵称
        EMClient.getInstance().pushManager().updatePushNickname("");
    }
}
