package net.melove.demo.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;

import com.easemob.EMConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;

import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.fragment.MLBaseFragment;
import net.melove.demo.chat.fragment.MLDrawerFragment;
import net.melove.demo.chat.R;
import net.melove.demo.chat.fragment.MLGroupsFragment;
import net.melove.demo.chat.fragment.MLRoomFragment;
import net.melove.demo.chat.fragment.MLSingleFragment;
import net.melove.demo.chat.info.MLApplyForInfo;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLToast;

import java.util.List;


public class MLMainActivity extends MLBaseActivity implements MLBaseFragment.OnMLFragmentListener {


    private Activity mActivity;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;
    private MLDrawerFragment mMLDrawerFragment;
    private boolean isDrawerOpen;
    private int mMenuType;
    private int mCurrentIndex;

    private MLApplyForDao mApplyForDao;


    /**
     * 存储最后一次显示的title
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initToolbar();
        initDrawerFragment();

        initListener();
    }


    private void init() {
        mActivity = this;
        isDrawerOpen = false;
        mMenuType = 0;
        mCurrentIndex = 0;
        mTitle = getTitle();

        mApplyForDao = new MLApplyForDao(mActivity);
    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.ml_widget_drawer_layout);

        mToolbar.setTitle(R.string.ml_chat);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 设置Toolbar与DrawerLayout 联动的按钮
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.ml_open, R.string.ml_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                isDrawerOpen = false;
                switch (mMenuType) {
                    case 0:
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.setCustomAnimations(R.anim.ml_fade_in, R.anim.ml_fade_out);
                        mFragmentTransaction.replace(R.id.ml_framelayout_container, mCurrentFragment);
                        mFragmentTransaction.commit();
                        break;
                    case 1:

                        break;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initDrawerFragment() {
        mFragmentManager = getSupportFragmentManager();
        // 侧滑菜单Fragment
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mMLDrawerFragment = MLDrawerFragment.newInstance(mDrawerLayout);
        mFragmentTransaction.replace(R.id.ml_framelayout_drawer, mMLDrawerFragment);
        mFragmentTransaction.commit();

        // 主Activity 默认显示 TimeLineFragment
        mCurrentIndex = 0;
        mCurrentFragment = new MLSingleFragment();
        mToolbar.setTitle(mActivity.getResources().getString(R.string.ml_chat));
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.ml_framelayout_container, mCurrentFragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mFragmentTransaction.setCustomAnimations(R.anim.ml_fade_in, R.anim.ml_fade_out);
        mFragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isDrawerOpen) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListener() {
        EMChatManager.getInstance().addConnectionListener(new MLConnectionListener());
        EMChat.getInstance().setAppInited();
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
        int w = a | b;
        switch (w) {
            // 系统级调用
            case 0x00:
                mToolbar.setTitle(s);
                break;
            case 0x01:

                break;
            // 侧滑调用
            case 0x10:
                if (mCurrentIndex != 0) {
                    MLToast.makeToast(mActivity.getResources().getString(R.string.ml_chat)).show();
                    mCurrentIndex = 0;
                    mMenuType = 0;
                    mCurrentFragment = new MLSingleFragment();
                    mToolbar.setTitle(R.string.ml_single_chat);
                }
                break;
            case 0x11:
                if (mCurrentIndex != 1) {
                    MLToast.makeToast(mActivity.getResources().getString(R.string.ml_group)).show();
                    mCurrentIndex = 1;
                    mMenuType = 0;
                    mCurrentFragment = new MLGroupsFragment();
                    mToolbar.setTitle(R.string.ml_group_chat);
                }
                break;
            case 0x12:
                if (mCurrentIndex != 2) {
                    MLToast.makeToast(mActivity.getResources().getString(R.string.ml_room)).show();
                    mCurrentIndex = 2;
                    mMenuType = 0;
                    mCurrentFragment = new MLRoomFragment();
                    mToolbar.setTitle(R.string.ml_room);
                }
                break;
            case 0x13:
                MLToast.makeToast(mActivity.getResources().getString(R.string.ml_settings)).show();
                mMenuType = 1;

                break;

            default:
                MLToast.makeToast(mActivity.getResources().getString(R.string.ml_other)).show();
                break;
        }
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    /**
     * 链接监听
     */
    private class MLConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MLLog.d("onConnected");
                    MLToast.makeToast("onConnected").show();
                }
            });
        }

        @Override
        public void onDisconnected(int i) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MLLog.d("onDisconnected");
                    MLToast.makeToast("onDisconnected").show();
                }
            });
        }
    }

    /**
     * 联系人监听
     */
    private class MLContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> list) {
            MLLog.d("onContactAdded");
            // 添加
        }

        @Override
        public void onContactDeleted(List<String> list) {
            MLLog.d("onContactDeleted");
            // 被删除
        }

        @Override
        public void onContactInvited(String username, String reason) {
            MLLog.d("onContactInvited");
            // 收到申请
            List<MLApplyForInfo> applyForInfos = mApplyForDao.getApplyForList();
            for (MLApplyForInfo applyForInfo : applyForInfos) {
                if (applyForInfo.getGroupId() == null && applyForInfo.getUserName().equals(username)) {
                    mApplyForDao.deleteApplyFor(username);
                }
            }
            MLApplyForInfo applyForInfo = new MLApplyForInfo();
            applyForInfo.setUserName(username);
            applyForInfo.setReason(reason);
            applyForInfo.setStatus(MLApplyForInfo.ApplyForStatus.BEAPPLYFOR);
            applyForInfo.setTime(System.currentTimeMillis());

            mApplyForDao.saveApplyFor(applyForInfo);

        }

        @Override
        public void onContactAgreed(String s) {
            // 同意申请
            MLLog.d("onContactAgreed");
        }

        @Override
        public void onContactRefused(String s) {
            // 拒绝申请
            MLLog.d("onContactRefused");
        }
    }
}
