package net.melove.demo.chat.fragment;


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
import com.hyphenate.chat.EMMessage;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLViewPagerAdapter;
import net.melove.demo.chat.util.MLLog;

import java.util.List;

/**
 * 单聊模块儿 Fragment
 */
public class MLHomeFragment extends MLBaseFragment implements EMMessageListener {

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
        mMessageListener = this;
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

    /**
     * 收到新消息
     *
     * @param list 收到的新消息集合
     */
    @Override
    public void onMessageReceived(List<EMMessage> list) {

    }

    /**
     * 收到新的透传消息
     *
     * @param list
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
    @Override
    public void onMessageReadAckReceived(List<EMMessage> list) {

    }

    /**
     * 收到新的发送回执
     *
     * @param list 收到发送回执的消息集合
     */
    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list) {

    }

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object  包含改变的消息
     */
    @Override
    public void onMessageChanged(EMMessage message, Object object) {

    }
}
