package net.melove.demo.chat.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.entity.MLApplyForEntity;
import net.melove.demo.chat.fragment.MLApplyforFragment;
import net.melove.demo.chat.fragment.MLBaseFragment;
import net.melove.demo.chat.fragment.MLHomeFragment;
import net.melove.demo.chat.fragment.MLOtherFragment;
import net.melove.demo.chat.notification.MLNotifier;
import net.melove.demo.chat.util.MLCrypto;
import net.melove.demo.chat.util.MLDate;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLImageView;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lzan13 on 2015/7/2.
 * 主Activity类，整个程序启动的主界面
 */
public class MLMainActivity extends MLBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MLBaseFragment.OnMLFragmentListener {

    // 环信事件监听接口
    private MLConnectionListener mConnectionListener;
    private MLContactListener mContactListener;
    private MLGroupChangeListener mGroupChangeListener;

    // 主界面的一些系统控件
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    // fab按钮
    private boolean isActivateFab;
    // 定义的包含Fab按钮的一个Layout布局，控制其他的Fab按钮的显示和隐藏
    private View mFabMenuLayout;
    private FloatingActionButton mFabCreateConversationBtn;
    private FloatingActionButton mFabAddContactBtn;
    private FloatingActionButton mFabAddGroupBtn;
    private FloatingActionButton mFabBtn;

    // Fragment 切换
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;
    private int mMenuType;


    // 申请已通知的 Dao
    private MLApplyForDao mApplyForDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initView();
        initFragment();
        initListener();
    }


    /**
     * 界面属性初始值的初始化
     */
    private void init() {
        mActivity = this;
        mMenuType = 0;
        isActivateFab = false;

        mApplyForDao = new MLApplyForDao(mActivity);
        android.R.layout.simple_list_item_2
    }

    /**
     * 控件初始化
     */
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.ml_layout_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.ml_widget_navigation);
        MLImageView imageView = (MLImageView) mNavigationView.getHeaderView(0).findViewById(R.id.ml_img_nav_avatar);

        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_chat));
        setSupportActionBar(mToolbar);

        // 定义的包含Fab按钮的一个Layout布局，控制其他的Fab按钮的显示和隐藏
        mFabMenuLayout = findViewById(R.id.ml_btn_fab_menu_layout);
        mFabCreateConversationBtn = (FloatingActionButton) findViewById(R.id.ml_btn_fab_create_conversation);
        mFabAddContactBtn = (FloatingActionButton) findViewById(R.id.ml_btn_fab_add_contact);
        mFabAddGroupBtn = (FloatingActionButton) findViewById(R.id.ml_btn_fab_add_group);
        mFabBtn = (FloatingActionButton) findViewById(R.id.ml_btn_fab);

        findViewById(R.id.ml_btn_fab_menu_layout).setOnClickListener(viewListener);
        findViewById(R.id.ml_text_fab_create_conversation).setOnClickListener(viewListener);
        findViewById(R.id.ml_text_fab_add_contact).setOnClickListener(viewListener);
        findViewById(R.id.ml_text_fab_add_group).setOnClickListener(viewListener);
        mFabCreateConversationBtn.setOnClickListener(viewListener);
        mFabAddContactBtn.setOnClickListener(viewListener);
        mFabAddGroupBtn.setOnClickListener(viewListener);
        mFabBtn.setOnClickListener(viewListener);

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
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    /**
     * lzan13   2015-8-28
     * 初始化界面显示的 Fragment
     */
    private void initFragment() {
        // 主Activity 默认显示第一个Fragment
        mCurrentFragment = MLHomeFragment.newInstance();
        mToolbar.setTitle(R.string.ml_chat);
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.ml_framelayout_container, mCurrentFragment);
        mFragmentTransaction.setCustomAnimations(R.anim.ml_anim_fade_in, R.anim.ml_anim_fade_out);
        mFragmentTransaction.commit();
    }

    /**
     * 初始化SDK的一些监听
     */
    private void initListener() {
        mConnectionListener = new MLConnectionListener();
        // 设置链接监听，监测连接服务器情况
        EMClient.getInstance().addConnectionListener(mConnectionListener);

        // 设置联系人监听，监测联系人申请及联系人变化
        mContactListener = new MLContactListener();
        EMClient.getInstance().contactManager().setContactListener(mContactListener);

        // 设置群组监听，监测群组情况的变化
        mGroupChangeListener = new MLGroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(mGroupChangeListener);

        // 最后要通知sdk，UI 已经初始化完毕，注册了相应的listener, 可以进行消息监听了（必须调用）
//        EMClient.getInstance().chatManager().setAppInited();
    }

    /**
     * Fab 按钮控件点击监听
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_fab:
                    fabChange();
                    break;
                case R.id.ml_text_fab_create_conversation:
                case R.id.ml_btn_fab_create_conversation:
                    fabChange();
                    createNewConversation();
                    break;
                case R.id.ml_text_fab_add_contact:
                case R.id.ml_btn_fab_add_contact:
                    fabChange();
                    startSearch();
                    break;
                case R.id.ml_text_fab_add_group:
                case R.id.ml_btn_fab_add_group:
                    fabChange();
//                    startSearch();
                    break;
                case R.id.ml_btn_fab_menu_layout:
                    fabChange();
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
                mCurrentFragment = new MLHomeFragment();
                mToolbar.setTitle(R.string.ml_chat);
                break;
            case R.id.ml_nav_group:
                mMenuType = 0;
                mCurrentFragment = new MLOtherFragment();
                mToolbar.setTitle(R.string.ml_group);
                break;
            case R.id.ml_nav_room:
                mMenuType = 1;
                mToolbar.setTitle(R.string.ml_room);

                break;
            case R.id.ml_nav_notification:
                mMenuType = 0;
                mCurrentFragment = new MLApplyforFragment();
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
     * 悬浮按钮的变化
     */
    private void fabChange() {
        if (isActivateFab) {
            isActivateFab = false;
            Animator animator = AnimatorInflater.loadAnimator(mActivity, R.animator.ml_animator_fab_rotate_left);
            animator.setTarget(mFabBtn);
            animator.start();
            Animator animator2 = AnimatorInflater.loadAnimator(mActivity, R.animator.ml_animator_fab_alpha_out);
            animator2.setTarget(mFabMenuLayout);
            animator2.start();
            animator2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mFabMenuLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            isActivateFab = true;
            mFabMenuLayout.setVisibility(View.VISIBLE);
            Animator animator = AnimatorInflater.loadAnimator(mActivity, R.animator.ml_animator_fab_rotate_right);
            animator.setTarget(mFabBtn);
            animator.start();
            Animator animator2 = AnimatorInflater.loadAnimator(mActivity, R.animator.ml_animator_fab_alpha_in);
            animator2.setTarget(mFabMenuLayout);
            animator2.start();
        }
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
                Intent intent = new Intent();
                intent.setClass(mActivity, MLChatActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, editText.getText().toString().trim());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity);
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
        Intent intent = new Intent(mActivity, MLSearchActivity.class);
        startActivity(intent);
    }

    /**
     * lzan13    2015-8-25
     * 链接监听，监听与服务器连接状况
     */
    private class MLConnectionListener implements EMConnectionListener {

        /**
         * 链接聊天服务器成功
         */
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MLLog.d("onConnected");
                }
            });
        }

        /**
         * 链接聊天服务器失败
         *
         * @param errorCode
         */
        @Override
        public void onDisconnected(final int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        MLLog.d("监听到被踢，多次初始化也有可能出现这个错误");
                    } else if (errorCode == EMError.USER_REMOVED) {
                        MLLog.d("账户被后台移除");
                    } else {
                        if (NetUtils.hasNetwork(mActivity)) {

                        } else {
                            MLLog.d("网络不可用");
                        }
                    }
                }
            });
        }
    }

    /**
     * ---------------------------------- Contact Listener -------------------------------
     * 联系人监听，用来监听联系人的请求与变化等
     */
    private class MLContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String s) {

        }

        @Override
        public void onContactDeleted(String s) {

        }

        /**
         * 收到对方联系人申请
         *
         * @param username
         * @param reason
         */
        @Override
        public void onContactInvited(String username, String reason) {
            MLLog.d("onContactInvited");

            // 创建一条好友申请数据
            MLApplyForEntity applyForEntity = new MLApplyForEntity();
            applyForEntity.setUserName(username);
//            applyForEntity.setNickName(mUserEntity.getNickName());
            applyForEntity.setReason(reason);
            applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEAPPLYFOR);
            applyForEntity.setType(0);
            applyForEntity.setTime(MLDate.getCurrentMillisecond());
            applyForEntity.setObjId(MLCrypto.cryptoStr2MD5(applyForEntity.getUserName() + applyForEntity.getType()));

            /**
             * 这里先读取本地的申请与通知信息
             * 如果相同则直接 return，不进行操作
             * 只有当新的好友请求发过来时才进行保存，并发送通知
             */
            // 这里进行一下筛选，如果已存在则去更新本地内容
            MLApplyForEntity temp = mApplyForDao.getApplyForEntiry(applyForEntity.getObjId());
            if (temp != null) {
                if (temp.getReason().equals(applyForEntity.getReason())) {
                    // 这里判断当前保存的信息如果和新的一模一样不进行操作
                    return;
                }
                mApplyForDao.updateApplyFor(applyForEntity);
            } else {
                mApplyForDao.saveApplyFor(applyForEntity);
            }
            // 调用发送通知栏提醒方法，提醒用户查看申请通知
            MLNotifier.getInstance(mActivity).sendApplyForNotification(applyForEntity);
        }

        /**
         * 对方同意了联系人申请
         *
         * @param username 收到处理的对方的username
         */
        @Override
        public void onContactAgreed(String username) {
            MLLog.d("onContactAgreed %", username);

            // 这里进行一下筛选，如果已存在则去更新本地内容
            MLApplyForEntity temp = mApplyForDao.getApplyForEntiry(MLCrypto.cryptoStr2MD5(username + 0));
            if (temp != null) {
                temp.setStatus(MLApplyForEntity.ApplyForStatus.BEAGREED);
                mApplyForDao.updateApplyFor(temp);
            } else {
                // 创建一条好友申请数据
                MLApplyForEntity applyForEntity = new MLApplyForEntity();
                applyForEntity.setUserName(username);
//                applyForEntity.setNickName(mUserEntity.getNickName());
                applyForEntity.setReason(mActivity.getResources().getString(R.string.ml_add_contact_reason));
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEAGREED);
                applyForEntity.setType(0);
                applyForEntity.setTime(MLDate.getCurrentMillisecond());
                applyForEntity.setObjId(MLCrypto.cryptoStr2MD5(applyForEntity.getUserName() + applyForEntity.getType()));
                mApplyForDao.saveApplyFor(applyForEntity);
            }
            // 调用发送通知栏提醒方法，提醒用户查看申请通知
            MLNotifier.getInstance(mActivity).sendApplyForNotification(temp);
        }

        /**
         * 对方拒绝了联系人申请
         *
         * @param username 收到处理的对方的username
         */
        @Override
        public void onContactRefused(String username) {
            MLLog.d("onContactRefused");
            // 这里进行一下筛选，如果已存在则去更新本地内容
            MLApplyForEntity temp = mApplyForDao.getApplyForEntiry(MLCrypto.cryptoStr2MD5(username + 0));
            if (temp != null) {
                temp.setStatus(MLApplyForEntity.ApplyForStatus.BEREFUSED);
                mApplyForDao.updateApplyFor(temp);
            } else {
                // 创建一条好友申请数据
                MLApplyForEntity applyForEntity = new MLApplyForEntity();
                applyForEntity.setUserName(username);
//                applyForEntity.setNickName(mUserEntity.getNickName());
                applyForEntity.setReason(mActivity.getResources().getString(R.string.ml_add_contact_reason));
                applyForEntity.setStatus(MLApplyForEntity.ApplyForStatus.BEREFUSED);
                applyForEntity.setType(0);
                applyForEntity.setTime(MLDate.getCurrentMillisecond());
                applyForEntity.setObjId(MLCrypto.cryptoStr2MD5(applyForEntity.getUserName() + applyForEntity.getType()));
                mApplyForDao.saveApplyFor(applyForEntity);
            }
            // 调用发送通知栏提醒方法，提醒用户查看申请通知
            MLNotifier.getInstance(mActivity).sendApplyForNotification(temp);
        }
    }

    /**
     * ------------------------------------- Group Listener -------------------------------------
     * 群组变化监听，用来监听群组请求，以及其他群组情况
     */
    private class MLGroupChangeListener implements EMGroupChangeListener {

        /**
         * 收到其他用户邀请加入群组
         *
         * @param groupId   要加入的群的id
         * @param groupName 要加入的群的名称
         * @param inviter   邀请者
         * @param reason    邀请理由
         */
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

        }

        /**
         * 用户申请加入群组
         *
         * @param groupId   要加入的群的id
         * @param groupName 要加入的群的名称
         * @param applyer   申请人的username
         * @param reason    申请加入的reason
         */
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

        }

        @Override
        public void onApplicationAccept(String s, String s1, String s2) {

        }

        @Override
        public void onApplicationDeclined(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onInvitationAccpted(String s, String s1, String s2) {

        }

        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {

        }

        @Override
        public void onUserRemoved(String s, String s1) {

        }

        @Override
        public void onGroupDestroy(String s, String s1) {

        }


        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }
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
            // 0x0x 表示系统级调用
            case 0x00:
                // 设置当前 Toolbar title内容
                mToolbar.setTitle(s);
                break;
            case 0x01:
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
                Intent intent = new Intent();
                intent.setClass(mActivity, MLUserInfoActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, mToolbar, "toolbar");
                ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            mDrawerLayout.closeDrawer(GravityCompat.START);
            mActivity.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除链接监听
        if (mConnectionListener != null) {
            EMClient.getInstance().chatManager().removeConversationListener(mConnectionListener);
        }
        // 移除群组监听
        if (mGroupChangeListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupChangeListener);
        }
        // 移除联系人监听
        EMContactManager.getInstance().removeContactListener();

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
