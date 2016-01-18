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

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;

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

import java.util.List;

/**
 * Created by lzan13 on 2015/7/2.
 * 主Activity类，整个程序启动的主界面
 */
public class MLMainActivity extends MLBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MLBaseFragment.OnMLFragmentListener, EMEventListener {

    // 环信事件监听接口
    private EMEventListener mEventListener;
    private MLConnectionListener mConnectionListener;
    private MLContactListener mContactListener;
    private MLGroupListener mGroupListener;

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
        mEventListener = this;
        mMenuType = 0;
        isActivateFab = false;

        mApplyForDao = new MLApplyForDao(mActivity);

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
        EMChatManager.getInstance().addConnectionListener(mConnectionListener);

        // 设置联系人监听，监测联系人申请及联系人变化
        mContactListener = new MLContactListener();
        EMContactManager.getInstance().setContactListener(mContactListener);

        // 设置群组监听，监测群组情况的变化
        mGroupListener = new MLGroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(mGroupListener);

        // 最后要通知sdk，UI 已经初始化完毕，注册了相应的listener, 可以进行消息监听了（必须调用）
        EMChat.getInstance().setAppInited();
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
                intent.putExtra(MLConstants.ML_C_CHAT_ID, editText.getText().toString().trim());
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
        Intent intent = new Intent(mActivity, MLSearchActivity.class);
        startActivity(intent);
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
            case EventConversationListChanged:
                Object obj = event.getData();
                MLLog.i("有新的会话");
                MLToast.makeToast("有新的会话").show();
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
                    if (errorCode == EMError.CONNECTION_CONFLICT) {
                        MLLog.d("account conflict");
                    } else if (errorCode == EMError.USER_REMOVED) {
                        MLLog.d("user removed");
                    } else {
                        MLLog.d("onDisconnected");
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

        /**
         * 添加联系人
         *
         * @param list
         */
        @Override
        public void onContactAdded(List<String> list) {
            MLLog.d("onContactAdded");
        }

        /**
         * 联系人被删除
         *
         * @param list
         */
        @Override
        public void onContactDeleted(List<String> list) {
            MLLog.d("onContactDeleted");
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
    private class MLGroupListener implements EMGroupChangeListener {

        /**
         * 收到加入群组邀请
         *
         * @param s
         * @param s1
         * @param s2
         * @param s3
         */
        @Override
        public void onInvitationReceived(String s, String s1, String s2, String s3) {

        }

        /**
         * 收到加入群组申请
         *
         * @param s
         * @param s1
         * @param s2
         * @param s3
         */
        @Override
        public void onApplicationReceived(String s, String s1, String s2, String s3) {

        }

        /**
         * 接收加群申请
         *
         * @param s
         * @param s1
         * @param s2
         */
        @Override
        public void onApplicationAccept(String s, String s1, String s2) {

        }

        /**
         * 拒绝加群申请
         *
         * @param s
         * @param s1
         * @param s2
         * @param s3
         */
        @Override
        public void onApplicationDeclined(String s, String s1, String s2, String s3) {

        }

        /**
         * 接收群邀请
         *
         * @param s
         * @param s1
         * @param s2
         */
        @Override
        public void onInvitationAccpted(String s, String s1, String s2) {

        }

        /**
         * 拒绝群邀请
         *
         * @param s
         * @param s1
         * @param s2
         */
        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {

        }

        /**
         * 群成员被删除
         *
         * @param s
         * @param s1
         */
        @Override
        public void onUserRemoved(String s, String s1) {

        }

        /**
         * 群解散
         *
         * @param s
         * @param s1
         */
        @Override
        public void onGroupDestroy(String s, String s1) {

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(mDrawerToggle.isDrawerIndicatorEnabled()){

            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除链接监听
        if (mConnectionListener != null) {
            EMChatManager.getInstance().removeConnectionListener(mConnectionListener);
        }
        // 移除群组监听
        if (mGroupListener != null) {
            EMGroupManager.getInstance().removeGroupChangeListener(mGroupListener);
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
        // 定义要监听的消息类型
        EMNotifierEvent.Event[] events = new EMNotifierEvent.Event[]{
                EMNotifierEvent.Event.EventConversationListChanged, // 会话列表改变
                EMNotifierEvent.Event.EventDeliveryAck,     // 已发送回执event注册
                EMNotifierEvent.Event.EventNewCMDMessage,   // 接收透传event注册
                EMNotifierEvent.Event.EventNewMessage,      // 接收新消息event注册
                EMNotifierEvent.Event.EventOfflineMessage,  // 接收离线消息event注册
                EMNotifierEvent.Event.EventReadAck          // 已读回执event注册
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
