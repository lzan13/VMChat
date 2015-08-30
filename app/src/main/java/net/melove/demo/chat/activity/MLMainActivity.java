package net.melove.demo.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;

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

/**
 * Created by lzan13 on 2015/7/2.
 */
public class MLMainActivity extends MLBaseActivity implements MLBaseFragment.OnMLFragmentListener, EMEventListener {


    private Activity mActivity;
    private EMEventListener mEventListener;

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
        mEventListener = this;
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
                        mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_in, R.anim.ml_anim_fade_out);
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

    /**
     * lzan13   2015-8-28
     * 初始化侧滑菜单
     */
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
        mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_in, R.anim.ml_anim_fade_out);
        mFragmentTransaction.commit();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        // 设置添加链接监听，监测连接服务器情况
        EMChatManager.getInstance().addConnectionListener(new MLConnectionListener());
        // 设置添加联系人监听，监测联系人申请及联系人变化
        EMContactManager.getInstance().setContactListener(new MLContactListener());

        // 最后要通知sdk，UI 已经初始化完毕，注册了相应的listener, 可以进行消息监听了（必须调用）
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
                    MLToast.makeToast(mActivity.getResources().getString(R.string.ml_single_chat)).show();
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
     * 实现EMEventListener接口，消息的监听方法，用来监听发来的消息
     *
     * @param event
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                MLLog.d("new message!");
                break;
            case EventNewCMDMessage:
                MLLog.d("new cmd message!");
                break;
            case EventOfflineMessage:

                break;
            default:

                break;
        }
    }

    /**
     * lzan13    2015-8-25
     * 链接监听，监听与服务器连接状况
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
        public void onDisconnected(final int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (errorCode == EMError.CONNECTION_CONFLICT) {
                        MLLog.d("account conflict");
                    } else if (errorCode == EMError.USER_REMOVED) {
                        MLLog.d("user removed");
                    } else {
                        MLLog.d("onDisconnected");
                        MLToast.makeToast("onDisconnected").show();
                    }
                }
            });
        }
    }

    /**
     * lzan13   2015-8-26 16:32
     * 联系人监听，用来监听联系人的请求与变化等
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent();
                intent.setClass(mActivity, MLNewApplyForActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity,
                        R.anim.ml_anim_slide_right_in, R.anim.ml_anim_slide_left_out);
                ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                break;
        }
        return super.onOptionsItemSelected(item);
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
        // 定义要监听的消息类型
        EMNotifierEvent.Event[] events = new EMNotifierEvent.Event[]{
                EMNotifierEvent.Event.EventDeliveryAck,     //已发送回执event注册
                EMNotifierEvent.Event.EventNewCMDMessage,   //接收透传event注册
                EMNotifierEvent.Event.EventNewMessage,      //接收新消息event注册
                EMNotifierEvent.Event.EventOfflineMessage,  //接收离线消息event注册
                EMNotifierEvent.Event.EventReadAck          //已读回执event注册
        };
        // 注册消息监听
        EMChatManager.getInstance().registerEventListener(mEventListener, events);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消消息的监听事件，为了防止多个界面同时监听
        EMChatManager.getInstance().unregisterEventListener(mEventListener);
    }
}
