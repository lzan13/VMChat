package net.melove.app.chat.app;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.chat.EMClient;

import net.melove.app.chat.R;
import net.melove.app.chat.connection.MLConnectionEvent;
import net.melove.app.chat.contacts.MLUserActivity;
import net.melove.app.chat.conversation.MLConversationsFragment;
import net.melove.app.chat.contacts.MLContactsFragment;
import net.melove.app.chat.setting.MLMeFragment;
import net.melove.app.chat.sign.MLSignInActivity;
import net.melove.app.chat.util.MLNetUtil;
import net.melove.app.chat.chat.MLChatActivity;
import net.melove.app.chat.util.MLSPUtil;
import net.melove.app.chat.widget.MLImageView;

/**
 * Created by lzan13 on 2015/7/2.
 * 主Activity类，整个程序启动的主界面
 */
public class MLMainActivity extends MLBaseActivity {

    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.widget_tab_layout) TabLayout mTabLayout;
    @BindView(R.id.layout_connection_error) LinearLayout mConnectionStatusLayout;
    @BindView(R.id.text_connection_error) TextView mConnectionStatusView;
    @BindView(R.id.img_avatar) MLImageView mAvatarView;
    @BindView(R.id.text_username) TextView mUsernameView;

    private String mCurrentUsername;
    // TabLayout 装填的内容
    private String mTabTitles[] = null;
    private Fragment mFragments[];
    private int mCurrentTabIndex;
    private MLConversationsFragment mConversationFragment;
    private MLContactsFragment mContactsFragment;
    private MLMeFragment mMeFragment;
    private MLOtherFragment mOtherFragment;

    // 创建新会话对话框
    private AlertDialog createConversationDialog;
    private AlertDialog.Builder alertDialogBuilder;

    @Override protected void onCreate(Bundle savedInstanceState) {
        // 判断当前是否已经登录
        if (EMClient.getInstance().isLoggedInBefore()) {
            // 加载群组到内存
            EMClient.getInstance().groupManager().loadAllGroups();
            // 加载所有本地会话到内存
            EMClient.getInstance().chatManager().loadAllConversations();
        } else {
            // 跳转到登录界面
            Intent intent = new Intent(this, MLSignInActivity.class);
            onStartActivity(this, intent);
            this.finish();
        }

        // 将主题设置为正常主题
        setTheme(R.style.MLTheme_Default);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
    }

    /**
     * 界面初始化操作
     */
    private void init() {
        mActivity = this;

        mCurrentUsername = (String) MLSPUtil.get(MLConstants.ML_SHARED_USERNAME, "");
        mUsernameView.setText(mCurrentUsername);

        setSupportActionBar(getToolbar());
        mCurrentTabIndex = 0;
        mTabTitles = new String[] {
                getString(R.string.ml_chat), getString(R.string.ml_contacts),
                getString(R.string.ml_me), getString(R.string.ml_test)
        };

        mConversationFragment = MLConversationsFragment.newInstance();
        mContactsFragment = MLContactsFragment.newInstance();
        mMeFragment = MLMeFragment.newInstance();
        mOtherFragment = MLOtherFragment.newInstance();

        mFragments = new Fragment[] {
                mConversationFragment, mContactsFragment, mMeFragment, mOtherFragment
        };
        MLViewPagerAdapter adapter =
                new MLViewPagerAdapter(getSupportFragmentManager(), mFragments, mTabTitles);
        mViewPager.setAdapter(adapter);
        // 设置 ViewPager 缓存个数
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(mCurrentTabIndex);
        // 添加 ViewPager 页面改变监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override public void onPageSelected(int position) {
            }

            @Override public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Fab 按钮控件点击监听
     */
    @OnClick({ R.id.img_avatar, R.id.layout_connection_error }) void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.img_avatar:
                intent = new Intent(mActivity, MLUserActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mCurrentUsername);
                onStartActivity(mActivity, intent, mAvatarView);
                break;
            case R.id.layout_connection_error:
                // 网络有错误，进行网络诊断
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
                break;
        }
    }

    /**
     * 根据输入的 chatId 创建一个新的会话
     */
    private void createNewConversation() {
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(
                mActivity.getResources().getString(R.string.ml_dialog_title_conversation));
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.dialog_text_message);
        textView.setText(R.string.ml_dialog_content_create_conversation);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_input);
        editText.setHint(R.string.ml_hint_input_not_null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.ml_btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                            Snackbar.make(getRootView(), R.string.ml_hint_input_not_null,
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        String currUsername = EMClient.getInstance().getCurrentUser();
                        if (currUsername.equals(editText.getText().toString().trim())) {
                            Snackbar.make(getRootView(), R.string.ml_toast_cant_chat_with_yourself,
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(mActivity, MLChatActivity.class);
                        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID,
                                editText.getText().toString().trim());
                        onStartActivity(mActivity, intent);
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.ml_btn_cancel,
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {

                    }
                });
        createConversationDialog = alertDialogBuilder.create();
        createConversationDialog.show();
    }

    /**
     * 开始搜索用户或者群组，
     */
    private void startSearch() {
        Intent intent = new Intent(mActivity, MLSearchActivity.class);
        onStartActivity(mActivity, intent);
    }

    /**
     * 重载父类实现的 EventBus 订阅方法，实现更具体的逻辑处理
     *
     * @param event 订阅的消息类型
     */
    @Override public void onEventBus(MLConnectionEvent event) {
        checkConnectionStatus();
        super.onEventBus(event);
    }

    /**
     * 检查连接状态
     */
    private void checkConnectionStatus() {
        // 判断当前是否拦截到服务器
        if (EMClient.getInstance().isConnected()) {
            mConnectionStatusLayout.setVisibility(View.GONE);
        } else {
            mConnectionStatusLayout.setVisibility(View.VISIBLE);
            // 判断连接不到服务器的原因是不是因为网络不可用
            if (MLNetUtil.hasNetwork()) {
                mConnectionStatusView.setText(R.string.ml_error_disconnected);
            } else {
                mConnectionStatusView.setText(R.string.ml_error_network_error);
            }
        }
    }

    /**
     * 重载加载菜单布局方法
     *
     * @param menu 菜单对象
     * @return 返回加载结果
     */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 重载菜单项选择方法
     *
     * @param item 被选择的菜单项
     * @return 返回处理结果，是否向下传递
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.action_add_conversation:
                // 创建新绘会话
                createNewConversation();
                break;
            case R.id.action_add_friend:
                startSearch();
                break;
            case R.id.action_add_group:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override public void onBackPressed() {
        //super.onBackPressed();
        onFinish();
    }

    @Override protected void onResume() {
        super.onResume();
        checkConnectionStatus();
        if (!EMClient.getInstance().isLoggedInBefore()) {
            // 跳转到登录界面
            Intent intent = new Intent(this, MLSignInActivity.class);
            onStartActivity(mActivity, intent);
            this.finish();
        }
    }

    @Override protected void onDestroy() {
        // 判断对话框是否显示状态，显示中则销毁，避免 activity 的销毁导致错误
        if (createConversationDialog != null && createConversationDialog.isShowing()) {
            createConversationDialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * 自定义 ViewPager 适配器子类
     */
    class MLViewPagerAdapter extends FragmentPagerAdapter {

        private String mTabsTitle[];
        private Fragment mFragments[];

        public MLViewPagerAdapter(FragmentManager fm, Fragment fragments[], String args[]) {
            super(fm);
            mFragments = fragments;
            mTabsTitle = args;
        }

        @Override public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }

        @Override public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override public int getCount() {
            return mTabsTitle.length;
        }
    }
}
