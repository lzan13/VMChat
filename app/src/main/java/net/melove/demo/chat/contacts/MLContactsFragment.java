package net.melove.demo.chat.contacts;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.database.MLContactDao;
import net.melove.demo.chat.communal.base.MLBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 单聊联系人界面 Fragment
 * 继承自自定义的MLBaseFramgnet类，为了减少代码量，在MLBaseFrament类中定义接口回调
 * 包含此Fragment的活动窗口必须实现{@link MLBaseFragment.OnMLFragmentListener}接口,
 * 定义创建实例的工厂方法 {@link MLContactsFragment#newInstance}，可使用此方法创建实例
 */
public class MLContactsFragment extends MLBaseFragment {

    private ListView mListView;
    private View mHeadView;
    private MLContactsAdapter mAdapter;

    // 用户信息操作类
    private MLContactDao mUserDao;
    private List<MLContactEntity> mUserList = new ArrayList<MLContactEntity>();

    // 应用内广播管理器，为了完全这里使用局域广播
    private LocalBroadcastManager mLocalBroadcastManager;
    // 会话界面监听会话变化的广播接收器
    private BroadcastReceiver mBroadcastReceiver;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLSingleContactsFragment
     */
    public static MLContactsFragment newInstance() {
        MLContactsFragment fragment = new MLContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

    }

    /**
     * 初始化联系人列表界面
     */
    private void initView() {
        mActivity = getParentFragment().getActivity();
    }

    /**
     * 初始化联系人界面ListView
     */
    private void initListView() {
        mListView = (ListView) getView().findViewById(R.id.ml_list_contacts);
        mUserDao = new MLContactDao(mActivity);

        // 加载所有联系人
        loadUserList();

        // 实例化联系人列表的适配器
        mAdapter = new MLContactsAdapter(mActivity, mUserList);

        mListView.setAdapter(mAdapter);

    }

    /**
     * 刷新邀请信息界面
     */
    private void refreshContacts() {
        mUserList.clear();
        mUserList.addAll(loadUserList());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载联系人列表，TODO 后期需要实现排序
     *
     * @return 返回加载的联系人集合
     */
    private List<MLContactEntity> loadUserList() {
        mUserDao = new MLContactDao(mActivity);
        // TODO 这里暂时只进行了简单的获取用户列表，后期需要实现排序
        List<MLContactEntity> list = mUserDao.getContactList();
        return list;
    }

    /**
     *
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    };

    /**
     * 注册广播接收器，用来监听全局监听监听到新消息之后发送的广播
     */
    private void registerBroadcastReceiver() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MLConstants.ML_ACTION_CONTACT);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshContacts();
            }
        };
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 取消注册消息变化的广播监听
     */
    private void unregisterBroadcastReceiver() {
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override
    public void onResume() {
        super.onResume();
        // 刷新联系人界面
        refreshContacts();
        // 注册广播监听
        registerBroadcastReceiver();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
        unregisterBroadcastReceiver();
    }
}
