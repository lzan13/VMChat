package net.melove.demo.chat.contacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.database.MLInvitedDao;
import net.melove.demo.chat.common.widget.MLToast;
import net.melove.demo.chat.common.base.MLBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/8/28.
 */
public class MLInvitedFragment extends MLBaseFragment {

    // 申请信息数据库操作类
    private MLInvitedDao mInvitedDao;
    // 申请信息集合
    private List<MLInvitedEntity> mInvitedList = new ArrayList<MLInvitedEntity>();
    //    private ListView mListView;
    private RecyclerView mRecyclerView;
    private MLInvitedAdapter mInvitedAdapter;

    // 应用内广播管理器，为了完全这里使用局域广播
    private LocalBroadcastManager mLocalBroadcastManager;
    // 会话界面监听会话变化的广播接收器
    private BroadcastReceiver mBroadcastReceiver;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLInvitedFragment
     */
    public static MLInvitedFragment newInstance() {
        MLInvitedFragment fragment = new MLInvitedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLInvitedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invited, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initListView();
    }

    /**
     * 初始化界面控件等
     */
    private void initView() {
        mActivity = getActivity();
        mInvitedDao = new MLInvitedDao(mActivity);

    }

    /**
     * 初始化邀请信息列表
     */
    private void initListView() {
        // 获取数据源
        mInvitedList = mInvitedDao.getInvitedList();
        // 实例化适配器
        mInvitedAdapter = new MLInvitedAdapter(mActivity, mInvitedList);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.ml_recyclerview_invited);

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

        // 设置适配器
        mRecyclerView.setAdapter(mInvitedAdapter);

        // 通过自定义接口来实现RecyclerView item的点击和长按事件
        setItemClickListener();
    }

    /**
     * 刷新邀请信息列表
     */
    private void refreshInvited() {
        mInvitedList.clear();
        // 这里清空之后要使用addAll的方式填充数据，不能直接 = ，否则Adapter的数据源将改变
        mInvitedList.addAll(mInvitedDao.getInvitedList());
        if (mInvitedAdapter != null) {
            mInvitedAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击事件都需要去自己实现，这里通过回调接口实现
     */
    private void setItemClickListener() {
        mInvitedAdapter.setOnItemClickListener(new MLInvitedAdapter.MLOnItemClickListener() {
            /**
             * 实现 RecyclerView item点击事件
             * @param view 触发点击事件的view
             * @param position 触发点击事件的位置
             */
            @Override
            public void onItemClick(View view, int position) {
                MLToast.makeToast("item " + position).show();
            }

            /**
             * 实现 RecyclerView item 长按事件
             * @param view 触发长按事件的 item
             * @param position 触发长按事件的position
             */
            @Override
            public void onItemLongClick(View view, int position) {
                final int index = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_invited));
                dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_content_delete_invited));
                dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mInvitedDao.deleteInvited(mInvitedList.get(index).getObjId());
                        refreshInvited();
                    }
                });
                dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * 注册广播接收器，用来监听全局监听监听到新消息之后发送的广播
     */
    private void registerBroadcastReceiver() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MLConstants.ML_ACTION_MESSAGE);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshInvited();
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
        refreshInvited();
        // 注册广播监听
        registerBroadcastReceiver();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
        // 取消广播监听的注册
        unregisterBroadcastReceiver();
    }
}
