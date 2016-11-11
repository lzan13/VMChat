package net.melove.app.chat.ui.main;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import net.melove.app.chat.R;
import net.melove.app.chat.ui.MLBaseFragment;
import net.melove.app.chat.ui.contacts.MLContactsFragment;
import net.melove.app.chat.ui.chat.MLConversationsFragment;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.test.MLTestFragment;

/**
 * 单聊模块儿 Fragment
 */
public class MLMainFragment extends MLBaseFragment {

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

        initView();

    }

    private void initView() {
        mCurrentTabIndex = 0;
        mTabTitles = new String[]{
                mActivity.getResources().getString(R.string.ml_chat),
                mActivity.getResources().getString(R.string.ml_contacts),
                mActivity.getResources().getString(R.string.ml_test)
        };

        mTabLayout = (TabLayout) getView().findViewById(R.id.widget_tab_layout);
        mViewPager = (ViewPager) getView().findViewById(R.id.view_viewpager);

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
