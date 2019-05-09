package com.vmloft.develop.app.chat.ui.main;

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

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.base.AppActivity;
import com.vmloft.develop.app.chat.base.OtherFragment;
import com.vmloft.develop.app.chat.base.SearchActivity;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.connection.ConnectionEvent;
import com.vmloft.develop.app.chat.router.ARouter;
import com.vmloft.develop.app.chat.ui.chat.ChatActivity;
import com.vmloft.develop.app.chat.ui.contacts.ContactsFragment;
import com.vmloft.develop.app.chat.ui.conversation.ConversationsFragment;
import com.vmloft.develop.app.chat.ui.sign.SignInActivity;
import com.vmloft.develop.app.chat.ui.contacts.UserActivity;
import com.vmloft.develop.app.chat.ui.main.me.MeFragment;

import com.vmloft.develop.app.chat.util.AUtil;
import com.vmloft.develop.library.tools.widget.VMImageView;
import com.vmloft.develop.library.tools.utils.VMNetwork;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

/**
 * Created by lzan13 on 2015/7/2.
 * 主Activity类，整个程序启动的主界面
 */
public class MainActivity extends AppActivity {

    // 使用注解方式获取控件
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.widget_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.layout_connection_error)
    LinearLayout connectionStatusLayout;
    @BindView(R.id.text_connection_error)
    TextView connectionStatusView;
    @BindView(R.id.img_avatar)
    VMImageView avatarView;
    @BindView(R.id.text_username)
    TextView usernameView;

    // 当前登录账户 Username
    private String currentUsername;
    // TabLayout 装填的内容
    private String tabTitles[] = null;
    private Fragment fragments[];
    private int currentTabIndex;
    private ConversationsFragment conversationFragment;
    private ContactsFragment contactsFragment;
    private MeFragment meFragment;
    private OtherFragment otherFragment;

    // 创建新会话对话框
    private AlertDialog createConversationDialog;
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断是否需要跳转到引导界面
        if (AUtil.isShowGuide()) {
            ARouter.goGuide(activity);
            return;
        }
        if (!AUtil.isSign()) {
            ARouter.goSign(activity);
            return;
        }

        setContentView(R.layout.activity_main);

        setWindowEnterTransition();

        ButterKnife.bind(this);

        init();
    }

    /**
     * 界面初始化操作
     */
    private void init() {
        activity = this;

        currentUsername = (String) VMSPUtil.get(activity, AConstants.SHARED_USERNAME, "");
        usernameView.setText(currentUsername);

        setSupportActionBar(getToolbar());
        currentTabIndex = 0;
        tabTitles = new String[]{
                getString(R.string.chat), getString(R.string.contacts), getString(R.string.me), getString(R.string.test)
        };

        conversationFragment = ConversationsFragment.newInstance();
        contactsFragment = ContactsFragment.newInstance();
        meFragment = MeFragment.newInstance();
        otherFragment = OtherFragment.newInstance();

        fragments = new Fragment[]{
                conversationFragment, contactsFragment, meFragment, otherFragment
        };
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, tabTitles);
        viewPager.setAdapter(adapter);
        // 设置 ViewPager 缓存个数
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(currentTabIndex);
        // 添加 ViewPager 页面改变监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Fab 按钮控件点击监听
     */
    @OnClick({R.id.img_avatar, R.id.layout_connection_error})
    void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.img_avatar:
                intent = new Intent(activity, UserActivity.class);
                intent.putExtra(AConstants.EXTRA_CHAT_ID, currentUsername);
                onStartActivity(activity, intent);
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
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                onStartActivity(activity, intent);
                break;
        }
    }

    /**
     * 根据输入的 chatId 创建一个新的会话
     */
    private void createNewConversation() {
        alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(activity.getResources().getString(R.string.dialog_title_conversation));
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.dialog_text_message);
        textView.setText(R.string.dialog_content_create_conversation);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_input);
        editText.setHint(R.string.hint_input_not_null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    Snackbar.make(getRootView(), R.string.hint_input_not_null, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String currUsername = EMClient.getInstance().getCurrentUser();
                if (currUsername.equals(editText.getText().toString().trim())) {
                    Snackbar.make(getRootView(), R.string.toast_cant_chat_with_yourself, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra(AConstants.EXTRA_CHAT_ID, editText.getText().toString().trim());
                onStartActivity(activity, intent);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        createConversationDialog = alertDialogBuilder.create();
        createConversationDialog.show();
    }

    /**
     * 开始搜索用户或者群组，
     */
    private void startSearch() {
        Intent intent = new Intent(activity, SearchActivity.class);
        onStartActivity(activity, intent);
    }

    /**
     * 重载父类实现的 EventBus 订阅方法，实现更具体的逻辑处理
     *
     * @param event 订阅的消息类型
     */
    @Override
    public void onEventBus(ConnectionEvent event) {
        checkConnectionStatus();
        super.onEventBus(event);
    }

    /**
     * 检查连接状态
     */
    private void checkConnectionStatus() {
        // 判断当前是否拦截到服务器
        if (EMClient.getInstance().isConnected()) {
            connectionStatusLayout.setVisibility(View.GONE);
        } else {
            connectionStatusLayout.setVisibility(View.VISIBLE);
            // 判断连接不到服务器的原因是不是因为网络不可用
            if (VMNetwork.hasNetwork(activity)) {
                connectionStatusView.setText(R.string.error_disconnected);
            } else {
                connectionStatusView.setText(R.string.error_network_error);
            }
        }
    }

    /**
     * 重载加载菜单布局方法
     *
     * @param menu 菜单对象
     * @return 返回加载结果
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 重载菜单项选择方法
     *
     * @param item 被选择的菜单项
     * @return 返回处理结果，是否向下传递
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        onFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionStatus();
        if (!EMClient.getInstance().isLoggedInBefore()) {
            // 跳转到登录界面
            Intent intent = new Intent(this, SignInActivity.class);
            onStartActivity(activity, intent);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        // 判断对话框是否显示状态，显示中则销毁，避免 activity 的销毁导致错误
        if (createConversationDialog != null && createConversationDialog.isShowing()) {
            createConversationDialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * 设置界面进入过度效果
     */
    private void setWindowEnterTransition() {
        Transition transition = TransitionInflater.from(activity).inflateTransition(R.transition.transition_fade_enter);
        // This view will not be affected by enter transition animation
        getWindow().setEnterTransition(transition);
    }

    /**
     * 自定义 ViewPager 适配器子类
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private String mTabsTitle[];
        private Fragment mFragments[];

        public ViewPagerAdapter(FragmentManager fm, Fragment fragments[], String args[]) {
            super(fm);
            mFragments = fragments;
            mTabsTitle = args;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mTabsTitle.length;
        }
    }
}
