package net.melove.demo.chat.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLViewPagerAdapter;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLPagerSlidingTab;

/**
 * 单聊模块儿 Fragment
 */
public class MLChatFragment extends MLBaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private MLPagerSlidingTab mMLPagerSlidingTab;
    private ViewPager mViewPager;
    private Fragment mFragments[];
    private MLContactsFragment mMLContactsFragment;
    private MLConversationsFragment mMLConversationFragment;
    private MLTestFragment mMLTestFragment;
    private String mTabTitles[] = new String[]{"聊天", "通讯录", "测试"};
    private int mCurrentTabIndex;

    private OnMLFragmentListener mListener;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @param param1
     * @param param2
     * @return MLSingleFragment
     */
    public static MLChatFragment newInstance(String param1, String param2) {
        MLChatFragment fragment = new MLChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MLChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initPagerSlidingTab();
        initViewPager();

    }

    /**
     * 初始化PagerSlidingTab
     */
    private void initPagerSlidingTab() {
        mMLPagerSlidingTab = (MLPagerSlidingTab) getView().findViewById(R.id.ml_widget_pageslidingtab);

    }

    /**
     * 初始化ViewPager，将Fragment添加进去
     */
    private void initViewPager() {
        mViewPager = (ViewPager) getView().findViewById(R.id.ml_widget_viewpager);

        mMLContactsFragment = MLContactsFragment.newInstance("", "");
        mMLConversationFragment = MLConversationsFragment.newInstance("", "");
        mMLTestFragment = MLTestFragment.newInstance("", "");

        mFragments = new Fragment[]{mMLConversationFragment, mMLContactsFragment, mMLTestFragment};
        mViewPager.setAdapter(new MLViewPagerAdapter(getChildFragmentManager(), mFragments, mTabTitles));

        mViewPager.setCurrentItem(mCurrentTabIndex);

        mMLPagerSlidingTab.setViewPager(mViewPager);
        mMLPagerSlidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentTabIndex = position;
                // 调用回调方法设置Toolbar Title
                mListener.onFragmentClick(0, 0, mTabTitles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMLFragmentListener) activity;
        } catch (ClassCastException e) {
            MLLog.e("必须实现Fragment的回调接口！");
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
