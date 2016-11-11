package net.melove.app.chat.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.R;
import net.melove.app.chat.module.event.MLUserEvent;
import net.melove.app.chat.module.listener.MLItemCallBack;
import net.melove.app.chat.ui.MLBaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人界面 Fragment
 * 定义创建实例的工厂方法 {@link MLContactsFragment#newInstance}，可使用此方法创建实例
 */
public class MLContactsFragment extends MLBaseFragment {

    // 代替 ListView 用来显示联系人列表
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    // 联系人界面适配器
    private MLContactsAdapter mAdapter;

    private List<MLUserEntity> mContactsList = new ArrayList<>();

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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    /**
     * 初始化联系人列表界面
     */
    private void initView() {
        mActivity = getParentFragment().getActivity();
        loadContactsList();
        mAdapter = new MLContactsAdapter(mActivity, mContactsList);
        /**
         * 为RecyclerView 设置布局管理器，这里使用线性布局
         * RececlerView 默认的布局管理器：
         * LinearLayoutManager          显示垂直滚动列表或水平的项目
         * GridLayoutManager            显示在一个网格项目
         * StaggeredGridLayoutManager   显示在交错网格项目
         * 自定义的布局管理器，需要继承 {@link android.support.v7.widget.RecyclerView.LayoutManager}
         *
         * add/remove items时的动画是默认启用的。
         * 自定义这些动画需要继承{@link android.support.v7.widget.RecyclerView.ItemAnimator}，
         * 并实现{@link RecyclerView#setItemAnimator(RecyclerView.ItemAnimator)}
         */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);

        setItemCallBack();
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击监听都要自己去做，然后通过回调接口实现
     */
    private void setItemCallBack() {
        mAdapter.setItemCallBack(new MLItemCallBack() {
            @Override public void onAction(int action, Object tag) {
                int position = (int) tag;
                switch (action) {
                    case MLConstants.ML_ACTION_APPLY_FOR_CLICK:
                        jumpUserInfo(position);
                        break;
                    case MLConstants.ML_ACTION_APPLY_FOR_AGREE:

                        break;
                }
            }
        });
    }

    private void jumpUserInfo(int position) {
        MLUserEntity userEntity = mContactsList.get(position);
        Intent intent = new Intent();
        intent.setClass(mActivity, MLUserActivity.class);
        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, userEntity.getUserName());
        mActivity.startActivity(intent);
    }

    /**
     * 加载联系人列表
     */
    private void loadContactsList() {
        if (mContactsList == null) {
            mContactsList = new ArrayList<>();
        }
        mContactsList.clear();
        mContactsList.addAll(MLHyphenate.getInstance().getUserList().values());
    }

    /**
     * 刷新邀请信息界面
     */
    private void refresh() {
        loadContactsList();
        if (mAdapter == null) {
            mAdapter = new MLContactsAdapter(mActivity, mContactsList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(MLUserEvent event) {
        refresh();
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override public void onResume() {
        super.onResume();
        // 刷新联系人界面
        refresh();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override public void onStop() {
        super.onStop();
    }
}
