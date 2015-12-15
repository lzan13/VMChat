package net.melove.demo.chat.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLViewPagerAdapter;
import net.melove.demo.chat.util.MLLog;

/**
 * 单聊模块儿 Fragment
 */
public class MLHomeFragment extends MLBaseFragment {

    private Activity mActivity;


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Fragment mFragments[];
    private MLContactsFragment mMLContactsFragment;
    private MLConversationsFragment mMLConversationFragment;
    private MLTestFragment mMLTestFragment;
    private String mTabTitles[] = null;
    private int mCurrentTabIndex;

    private OnMLFragmentListener mListener;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLSingleFragment
     */
    public static MLHomeFragment newInstance() {
        MLHomeFragment fragment = new MLHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        init();
        initView();

    }

    private void init() {
        mCurrentTabIndex = 0;
        mTabTitles = new String[]{
                mActivity.getResources().getString(R.string.ml_title_chat),
                mActivity.getResources().getString(R.string.ml_title_contacts),
                mActivity.getResources().getString(R.string.ml_title_test)
        };
    }

    private void initView() {
        mTabLayout = (TabLayout) getView().findViewById(R.id.ml_widget_tablayout);
        mViewPager = (ViewPager) getView().findViewById(R.id.ml_view_viewpager);

        mMLContactsFragment = MLContactsFragment.newInstance();
        mMLConversationFragment = MLConversationsFragment.newInstance();
        mMLTestFragment = MLTestFragment.newInstance();

        mFragments = new Fragment[]{mMLConversationFragment, mMLContactsFragment, mMLTestFragment};
        MLViewPagerAdapter adapter = new MLViewPagerAdapter(getChildFragmentManager(), mFragments, mTabTitles);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mCurrentTabIndex);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mListener.onFragmentClick(0x00, 0x00, mTabTitles[tab.getPosition()]);
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnMLFragmentListener) context;
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
