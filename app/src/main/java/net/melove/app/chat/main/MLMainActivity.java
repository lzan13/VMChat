package net.melove.app.chat.main;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.MLEasemobHelper;
import net.melove.app.chat.application.eventbus.MLConnectionEvent;
import net.melove.app.chat.authentication.MLSigninActivity;
import net.melove.app.chat.communal.base.MLBaseActivity;
import net.melove.app.chat.communal.util.MLLog;
import net.melove.app.chat.communal.util.MLSPUtil;
import net.melove.app.chat.conversation.MLChatActivity;
import net.melove.app.chat.invited.MLApplyForActivity;
import net.melove.app.chat.communal.base.MLBaseFragment;
import net.melove.app.chat.communal.widget.MLImageView;
import net.melove.app.chat.communal.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/2.
 * 主Activity类，整个程序启动的主界面
 */
public class MLMainActivity extends MLBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MLBaseFragment.OnMLFragmentListener {

    /**
     * 主界面的一些系统控件
     */
    // 当前界面 Toolbar
    private Toolbar mToolbar;
    // 侧滑菜单外围布局
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    // 侧滑菜单控件
    private NavigationView mNavigationView;

    // 侧滑布局用户头像
    private MLImageView mAvatarView;
    private FloatingActionButton mConnectionFabBtn;

    // Fragment 切换事务处理类
    private FragmentTransaction mFragmentTransaction;
    // 当前 Fragment
    private Fragment mCurrentFragment;

    // 菜单操作类型
    private int mMenuType;

    // 创建新会话对话框
    private AlertDialog createConversationDialog;
    private AlertDialog.Builder alertDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 判断当前是否已经登录
        if (MLEasemobHelper.getInstance().isLogined()) {
            // 获取当前系统时间毫秒数
            long start = System.currentTimeMillis();
            // 加载群组到内存
            EMClient.getInstance().groupManager().loadAllGroups();
            // 加载所有本地会话到内存
            EMClient.getInstance().chatManager().loadAllConversations();
            // 获取加载回话使用的时间差 毫秒表示
            long costTime = System.currentTimeMillis() - start;
            MLLog.d("Load groups and load conversations cost time %d" + costTime);
        } else {
            // 跳转到登录界面
            Intent intent = new Intent(this, MLSigninActivity.class);
            superJump(intent);
            this.finish();
        }
        // 将主题设置为正常主题
        setTheme(R.style.MLTheme_Default);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFragment();
    }


    /**
     * 控件初始化以及界面属性初始值的初始化
     */
    private void initView() {
        mActivity = this;
        mMenuType = 0;

        mRootView = findViewById(R.id.ml_layout_coordinator);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.ml_layout_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.ml_widget_navigation);
        // 侧滑用户头像
        mAvatarView = (MLImageView) mNavigationView.getHeaderView(0).findViewById(R.id.ml_img_nav_avatar);
        mAvatarView.setOnClickListener(viewListener);

        // 网络连接提示按钮
        mConnectionFabBtn = (FloatingActionButton) findViewById(R.id.ml_btn_fab_connection);
        mConnectionFabBtn.setOnClickListener(viewListener);
        if (MLEasemobHelper.getInstance().isConnection()) {
            mConnectionFabBtn.setImageResource(R.mipmap.ic_signal_wifi_on_white_24dp);
            mConnectionFabBtn.setVisibility(View.GONE);
        } else {
            mConnectionFabBtn.setImageResource(R.mipmap.ic_signal_wifi_off_white_24dp);
            mConnectionFabBtn.setVisibility(View.VISIBLE);
        }

        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(mActivity.getResources().getString(R.string.ml_chat));
        setSupportActionBar(mToolbar);

        initDrawer();
    }


    /**
     * 初始化侧滑抽屉，实现抽屉的监听
     */
    private void initDrawer() {
        // 设置侧滑导航的监听
        mNavigationView.setNavigationItemSelectedListener(this);

        // 设置Toolbar与DrawerLayout 联动的按钮，并重写DrawerToggle 的几个方法，主要是为了实现抽屉关闭后再加界面
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.ml_drawer_open, R.string.ml_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                switch (mMenuType) {
                case 0:
                    mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_enter, R.anim.ml_anim_fade_exit);
                    mFragmentTransaction.replace(R.id.ml_framelayout_container, mCurrentFragment);
                    mFragmentTransaction.commit();
                    break;
                case 1:

                    break;
                default:
                    break;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    /**
     * lzan13   2015-8-28
     * 初始化界面显示的 Fragment
     */
    private void initFragment() {
        // 主Activity 默认显示第一个Fragment
        mCurrentFragment = MLMainFragment.newInstance();
        mToolbar.setTitle(R.string.ml_chat);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.ml_framelayout_container, mCurrentFragment);
        mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_enter, R.anim.ml_anim_fade_exit);
        mFragmentTransaction.commit();
    }

    /**
     * Fab 按钮控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.ml_img_nav_avatar:

                break;
            case R.id.ml_btn_fab_connection:
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
                break;
            }
        }
    };

    /**
     * 侧滑导航的点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        case R.id.ml_nav_home:
            mMenuType = 0;
            mCurrentFragment = MLMainFragment.newInstance();
            mToolbar.setTitle(R.string.ml_chat);
            break;
        case R.id.ml_nav_group:
            mMenuType = 0;
            mCurrentFragment = MLOtherFragment.newInstance();
            mToolbar.setTitle(R.string.ml_group);
            break;
        case R.id.ml_nav_room:
            mMenuType = 1;
            mToolbar.setTitle(R.string.ml_room);

            break;
        case R.id.ml_nav_notification:
            mMenuType = 0;
            mCurrentFragment = MLApplyForActivity.newInstance();
            mToolbar.setTitle(R.string.ml_apply_for);
            break;
        case R.id.ml_nav_help:
            mMenuType = 1;

            break;
        case R.id.ml_nav_setting:
            mMenuType = 1;

            break;
        default:
            mMenuType = 1;
            break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 根据输入的 chatId 创建一个新的会话
     */
    private void createNewConversation() {
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_conversation));
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.ml_dialog_text_message);
        textView.setText(R.string.ml_dialog_content_create_conversation);
        final EditText editText = (EditText) view.findViewById(R.id.ml_dialog_edit_input);
        editText.setHint(R.string.ml_hint_input_not_null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    MLToast.errorToast(R.string.ml_hint_input_not_null).show();
                    return;
                }
                String currUsername = (String) MLSPUtil.get(mActivity, MLConstants.ML_SHARED_USERNAME, "");
                if (currUsername.equals(editText.getText().toString().trim())) {
                    MLToast.errorToast(R.string.ml_toast_chat_cant_yourself).show();
                    return;
                }
                Intent intent = new Intent(mActivity, MLChatActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, editText.getText().toString().trim());
                superJump(intent);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
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
        Intent intent = new Intent(mActivity, MLSearchActivity.class);
        superJump(intent);
    }

    /**
     * Fragment 的统一回调函数
     *
     * @param a action命令
     * @param b
     * @param s
     */
    @Override
    public void onFragmentClick(int a, int b, String s) {
        Intent intent = null;
        int w = a | b;
        switch (w) {
        // 0x0x 表示系统级调用
        case 0x00:
            // 设置当前 Toolbar title内容
            mToolbar.setTitle(s);
            break;
        case 0x01:
            // 退出登录，跳转到登录界面
            intent = new Intent(mActivity, MLSigninActivity.class);
            superJump(intent);
            onFinish();
            break;
        // 0x1x 表示其他调用
        case 0x10:

            break;
        case 0x11:

            break;
        case 0x12:

            break;
        case 0x13:

            break;
        // 0x2x 暂时表示Test
        case 0x20:

            break;
        default:
            MLToast.makeToast(mActivity.getResources().getString(R.string.ml_test)).show();
            break;
        }
        //        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public View getToolbar() {
        return mToolbar;
    }


    /**
     * 重载父类实现的 EventBus 订阅方法，实现更具体的逻辑处理
     *
     * @param event 订阅的消息类型
     */
    @Override
    public void onEventBus(MLConnectionEvent event) {
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
        case R.id.ml_action_search:

            break;
        case R.id.ml_action_add_conversation:
            // 创建新绘会话
            createNewConversation();
            break;
        case R.id.ml_action_add_contacts:
            startSearch();
            break;
        case R.id.ml_action_add_group:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * 按键监听
     *
     * @param keyCode 按键Code
     * @param event   按键事件
     * @return 返回值表示是否向下继续传递按键事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 检测当前是否打开侧滑抽屉菜单，如果打开状态，返回键就关闭，否则结束退出app
            if (mNavigationView.isShown()) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                // 结束Activity
                onFinish();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MLEasemobHelper.getInstance().isLogined()) {
            // 跳转到登录界面
            Intent intent = new Intent(this, MLSigninActivity.class);
            superJump(intent);
            this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 判断对话框是否显示状态，显示中则销毁，避免 activity 的销毁导致错误
        if (createConversationDialog != null && createConversationDialog.isShowing()) {
            createConversationDialog.dismiss();
        }
        super.onDestroy();
    }
}
