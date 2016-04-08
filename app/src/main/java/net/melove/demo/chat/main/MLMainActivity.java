package net.melove.demo.chat.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.authentication.MLSigninActivity;
import net.melove.demo.chat.common.base.MLBaseActivity;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.conversation.MLChatActivity;
import net.melove.demo.chat.database.MLInvitedDao;
import net.melove.demo.chat.invited.MLInvitedFragment;
import net.melove.demo.chat.common.base.MLBaseFragment;
import net.melove.demo.chat.common.widget.MLImageView;
import net.melove.demo.chat.common.widget.MLToast;

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

    // Fragment 切换事务处理类
    private FragmentTransaction mFragmentTransaction;
    // 当前 Fragment
    private Fragment mCurrentFragment;

    // 菜单操作类型
    private int mMenuType;

    // 申请与通知的 Dao
    private MLInvitedDao mInvitedDao;

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
            Intent intent = new Intent();
            intent.setClass(this, MLSigninActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
            this.finish();
        }
        // 将主题设置为正常主题
        setTheme(R.style.MLTheme_Default);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initView();
        initFragment();
    }


    /**
     * 界面属性初始值的初始化
     */
    private void init() {
        mActivity = this;
        mMenuType = 0;
        mInvitedDao = new MLInvitedDao(mActivity);
    }

    /**
     * 控件初始化
     */
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.ml_layout_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.ml_widget_navigation);
        mAvatarView = (MLImageView) mNavigationView.getHeaderView(0).findViewById(R.id.ml_img_nav_avatar);

        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_chat));
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
                    mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_in, R.anim.ml_anim_fade_out);
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
        mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_in, R.anim.ml_anim_fade_out);
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
            mCurrentFragment = MLInvitedFragment.newInstance();
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_conversation));
        View view = mActivity.getLayoutInflater().inflate(R.layout.dialog_communal, null);
        TextView textView = (TextView) view.findViewById(R.id.ml_dialog_text_message);
        textView.setText(R.string.ml_dialog_content_create_conversation);
        final EditText editText = (EditText) view.findViewById(R.id.ml_dialog_edit_input);
        editText.setHint(R.string.ml_hint_input);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    MLToast.errorToast(R.string.ml_hint_input).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(mActivity, MLChatActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, editText.getText().toString().trim());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mToolbar, "toolbar");
                ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
            }
        });
        dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * 开始搜索用户或者群组，
     */
    private void startSearch() {
        Intent intent = new Intent();
        intent.setClass(mActivity, MLSearchActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mToolbar, "toolbar");
        ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
    }

    /**
     * Fragment 的统一回调函数
     *
     * @param a
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
            intent = new Intent();
            intent.setClass(mActivity, MLSigninActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mToolbar, "toolbar");
            ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
            mActivity.finish();
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
            //                Intent intent = new Intent();
            //                intent.setClass(mActivity, MLContactInfoActivity.class);
            //                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity);
            //                ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
