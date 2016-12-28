package net.melove.app.chat.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import net.melove.app.chat.app.MLConstants;
import net.melove.app.chat.R;
import net.melove.app.chat.app.MLItemCallBack;
import net.melove.app.chat.app.MLBaseFragment;

import net.melove.app.chat.widget.recycler.MLLinearLayoutManager;
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

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    @Override protected int initLayoutId() {
        return R.layout.fragment_contacts;
    }

    /**
     * 初始化联系人列表界面
     */
    @Override protected void initView() {
        ButterKnife.bind(this, getView());

        mActivity = getActivity();
    }

    /**
     * 加载数据
     */
    @Override protected void initData() {
        loadContactsList();
        mAdapter = new MLContactsAdapter(mActivity, mContactsList);
        mRecyclerView.setLayoutManager(new MLLinearLayoutManager(mActivity));
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
                    case R.id.img_avatar:
                        itemClick(position);
                        break;
                    case MLConstants.ML_ACTION_CLICK:
                        itemClick(position);
                        break;
                    case MLConstants.ML_ACTION_LONG_CLICK:
                        itemLongClick(position);
                        break;
                }
            }
        });
    }

    /**
     * 列表项点击事件
     */
    private void itemClick(int position) {
        MLUserEntity userEntity = mContactsList.get(position);
        Intent intent = new Intent();
        intent.setClass(mActivity, MLUserActivity.class);
        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, userEntity.getUserName());
        mActivity.startActivity(intent);
    }

    /**
     * 列表项长按事件
     */
    private void itemLongClick(int position) {

    }

    /**
     * 加载联系人列表
     */
    private void loadContactsList() {
        if (mContactsList == null) {
            mContactsList = new ArrayList<>();
            mContactsList.addAll(MLUserManager.getInstance().getContactsMap().values());
        } else {
            mContactsList.clear();
            mContactsList.addAll(MLUserManager.getInstance().getContactsMap().values());
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(MLContactsEvent event) {
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
