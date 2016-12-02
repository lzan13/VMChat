package net.melove.app.chat.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hyphenate.chat.EMClient;

import net.melove.app.chat.R;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.module.event.MLConnectionEvent;
import net.melove.app.chat.test.MLTestFragment;
import net.melove.app.chat.ui.chat.MLConversationsFragment;
import net.melove.app.chat.ui.contacts.MLContactsFragment;
import net.melove.app.chat.ui.sign.MLSignInActivity;
import net.melove.app.chat.util.MLSPUtil;
import net.melove.app.chat.ui.chat.MLChatActivity;

/**
 * Created by lzan13 on 2015/7/2.
 * 主Activity类，整个程序启动的主界面
 */
public class MLMainActivity extends MLBaseActivity {

    @BindView(R.id.fab_connection) FloatingActionButton mConnectionFabBtn;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.widget_tab_layout) TabLayout mTabLayout;

    // TabLayout 装填的内容
    private String mTabTitles[] = null;
    private Fragment mFragments[];
    private int mCurrentTabIndex;
    private MLContactsFragment mContactsFragment;
    private MLConversationsFragment mConversationFragment;
    private MLTestFragment mTestFragment;

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
            superJump(intent);
            this.finish();
        }
        // 将主题设置为正常主题
        setTheme(R.style.MLTheme_Default);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
        initView();
    }

    /**
     * 界面初始化操作
     */
    private void init() {
        mActivity = this;

        if (EMClient.getInstance().isConnected()) {
            mConnectionFabBtn.setImageResource(R.mipmap.ic_signal_wifi_on_white_24dp);
            mConnectionFabBtn.setVisibility(View.GONE);
        } else {
            mConnectionFabBtn.setImageResource(R.mipmap.ic_signal_wifi_off_white_24dp);
            mConnectionFabBtn.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(getToolbar());
        getSupportActionBar().setTitle(R.string.ml_chat);
    }

    private void initView() {
        mCurrentTabIndex = 0;
        mTabTitles = new String[] {
                mActivity.getResources().getString(R.string.ml_chat),
                mActivity.getResources().getString(R.string.ml_contacts),
                mActivity.getResources().getString(R.string.ml_test)
        };

        mContactsFragment = MLContactsFragment.newInstance();
        mConversationFragment = MLConversationsFragment.newInstance();
        mTestFragment = MLTestFragment.newInstance();

        mFragments = new Fragment[] { mConversationFragment, mContactsFragment, mTestFragment };
        MLViewPagerAdapter adapter =
                new MLViewPagerAdapter(getSupportFragmentManager(), mFragments, mTabTitles);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mCurrentTabIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override public void onPageSelected(int position) {
                getToolbar().setTitle(mTabTitles[position]);
            }

            @Override public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Fab 按钮控件点击监听
     */
    @OnClick({ R.id.fab_connection }) void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_connection:
                Intent intent = null;
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
                        String currUsername =
                                (String) MLSPUtil.get(mActivity, MLConstants.ML_SHARED_USERNAME,
                                        "");
                        if (currUsername.equals(editText.getText().toString().trim())) {
                            Snackbar.make(getRootView(), R.string.ml_toast_cant_chat_with_yourself,
                                    Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(mActivity, MLChatActivity.class);
                        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID,
                                editText.getText().toString().trim());
                        superJump(intent);
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
        superJump(intent);
    }

    /**
     * 重载父类实现的 EventBus 订阅方法，实现更具体的逻辑处理
     *
     * @param event 订阅的消息类型
     */
    @Override public void onEventBus(MLConnectionEvent event) {
        /**
         * 因为3.x的sdk断开服务器连接后会一直重试并发出回调，所以为了防止一直Toast提示用户，
         * 这里取消弹出 Toast 方式，只是显示图标
         */
        if (event.getType() == MLConstants.ML_CONNECTION_CONNECTED) {
            mConnectionFabBtn.setImageResource(R.mipmap.ic_signal_wifi_on_white_24dp);
            mConnectionFabBtn.setVisibility(View.GONE);
        } else {
            mConnectionFabBtn.setImageResource(R.mipmap.ic_signal_wifi_off_white_24dp);
            mConnectionFabBtn.setVisibility(View.VISIBLE);
        }
        super.onEventBus(event);
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
     * 按键监听
     *
     * @param keyCode 按键Code
     * @param event 按键事件
     * @return 返回值表示是否向下继续传递按键事件
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 检测当前是否打开侧滑抽屉菜单，如果打开状态，返回键就关闭，否则结束退出app
            // 结束Activity
            //onFinish();
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override protected void onResume() {
        super.onResume();
        if (!EMClient.getInstance().isLoggedInBefore()) {
            // 跳转到登录界面
            Intent intent = new Intent(this, MLSignInActivity.class);
            superJump(intent);
            this.finish();
        }
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @Override protected void onDestroy() {
        // 判断对话框是否显示状态，显示中则销毁，避免 activity 的销毁导致错误
        if (createConversationDialog != null && createConversationDialog.isShowing()) {
            createConversationDialog.dismiss();
        }
        super.onDestroy();
    }
}
