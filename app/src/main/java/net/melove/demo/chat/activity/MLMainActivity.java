package net.melove.demo.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;

import net.melove.demo.chat.adapter.MLViewPagerAdapter;
import net.melove.demo.chat.fragment.MLBaseFragment;
import net.melove.demo.chat.fragment.MLContactsFragment;
import net.melove.demo.chat.fragment.MLConversationFragment;
import net.melove.demo.chat.fragment.MLDrawerFragment;
import net.melove.demo.chat.R;
import net.melove.demo.chat.fragment.MLSettingFragment;
import net.melove.demo.chat.fragment.MLTestFragment;
import net.melove.demo.chat.widget.MLPagerSlidingTab;


public class MLMainActivity extends MLBaseActivity implements MLBaseFragment.OnMLFragmentListener {


    private Activity mActivity;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private MLPagerSlidingTab mMLPagerSlidingTab;
    private ViewPager mViewPager;
    private Fragment mFragments[];
    private MLContactsFragment mMLContactsFragment;
    private MLConversationFragment mMLConversationFragment;
    private MLSettingFragment mMLSettingFragment;
    private MLTestFragment mMLTestFragment;
    private String mTabTitles[] = new String[]{"聊天", "通讯录", "设置", "测试"};
    private int mCurrentTabIndex;

    private MLDrawerFragment mMLDrawerFragment;
    private boolean isDrawerOpen;


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
        initPagerSlidingTab();
        initViewPager();

    }


    private void init() {
        mActivity = this;
        isDrawerOpen = false;
        mTitle = getTitle();
        mCurrentTabIndex = 0;

    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.ml_widget_drawer_layout);

        mToolbar.setTitle(R.string.ml_title_chat);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 设置Toolbar与DrawerLayout 联动的按钮
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.ml_drawer_open, R.string.ml_drawer_close) {
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
        // 侧滑菜单Fragment
        FragmentTransaction ftd = getSupportFragmentManager().beginTransaction();
        mMLDrawerFragment = MLDrawerFragment.newInstance(mDrawerLayout, "", "");
        ftd.replace(R.id.ml_framelayout_drawer, mMLDrawerFragment);
        ftd.commit();
    }

    /**
     * 初始化PagerSlidingTab
     */
    private void initPagerSlidingTab() {
        mMLPagerSlidingTab = (MLPagerSlidingTab) findViewById(R.id.ml_widget_pageslidingtab);

//        mMLPagerSlidingTab.setSelectedTextColor(mActivity.getResources().getColor(R.color.ml_primary));
    }

    /**
     * 初始化ViewPager，将Fragment添加进去
     */
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.ml_widget_viewpager);

        mMLContactsFragment = MLContactsFragment.newInstance("", "");
        mMLConversationFragment = MLConversationFragment.newInstance("", "");
        mMLSettingFragment = MLSettingFragment.newInstance("", "");
        mMLTestFragment = MLTestFragment.newInstance("", "");

        mFragments = new Fragment[]{mMLContactsFragment, mMLConversationFragment, mMLSettingFragment, mMLTestFragment};
        mViewPager.setAdapter(new MLViewPagerAdapter(getSupportFragmentManager(), mFragments, mTabTitles));

        mViewPager.setCurrentItem(mCurrentTabIndex);

        mMLPagerSlidingTab.setViewPager(mViewPager);
        mMLPagerSlidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentTabIndex = position;
                mToolbar.setTitle(mTabTitles[position]);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isDrawerOpen) {
            getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public void onPotion(int i) {
        switch (i) {
            case 0:

                break;
        }
    }
}
