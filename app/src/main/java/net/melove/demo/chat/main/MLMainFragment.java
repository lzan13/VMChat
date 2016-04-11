package net.melove.demo.chat.main;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.base.MLBaseFragment;
import net.melove.demo.chat.contacts.MLContactsFragment;
import net.melove.demo.chat.conversation.MLConversationsFragment;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.test.MLTestFragment;

/**
 * 单聊模块儿 Fragment
 */
public class MLMainFragment extends MLBaseFragment {

    private EMMessageListener mMessageListener;

    // 和ViewPager配合使用
    private TabLayout mTabLayout;
    // TabLayout 装填的内容
    private String mTabTitles[] = null;
    private ViewPager mViewPager;
    private Fragment mFragments[];
    private MLContactsFragment mMLContactsFragment;
    private MLConversationsFragment mMLConversationFragment;
    private MLTestFragment mMLTestFragment;
    private int mCurrentTabIndex;

    private OnMLFragmentListener mListener;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLMainFragment
     */
    public static MLMainFragment newInstance() {
        MLMainFragment fragment = new MLMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLMainFragment() {
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
                mActivity.getResources().getString(R.string.ml_chat),
                mActivity.getResources().getString(R.string.ml_contacts),
                mActivity.getResources().getString(R.string.ml_test)
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        // 取消消息的监听事件，为了防止多个界面同时监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
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
