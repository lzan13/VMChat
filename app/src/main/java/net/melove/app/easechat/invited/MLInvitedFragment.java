package net.melove.app.easechat.invited;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.easechat.R;
import net.melove.app.easechat.application.MLConstants;
import net.melove.app.easechat.application.eventbus.MLInvitedEvent;
import net.melove.app.easechat.application.eventbus.MLMessageEvent;
import net.melove.app.easechat.communal.util.MLDate;
import net.melove.app.easechat.database.MLInvitedDao;
import net.melove.app.easechat.communal.base.MLBaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzan13 on 2015/8/28.
 * 好友申请通知界面
 */
public class MLInvitedFragment extends MLBaseFragment {

    // 申请信息集合
    private List<MLInvitedEntity> mInvitedList = new ArrayList<MLInvitedEntity>();
    //    private ListView mListView;
    private RecyclerView mRecyclerView;
    private MLInvitedAdapter mInvitedAdapter;

    // 自定义Handler类，用来处理非UI界面刷新UI的工作
    private MLHandler mHandler;

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
        mHandler = new MLHandler();

        mActivity = getActivity();

    }

    /**
     * 初始化邀请信息列表
     */
    private void initListView() {
        /**
         * 通过调用{@link MLInvitedDao#getInvitedList()} 方法获取申请信息列表数据源
         * {@link MLInvitedDao} 是一个单例类
         */
        mInvitedList = MLInvitedDao.getInstance().getInvitedList();
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
        /**
         * 更新数据源的数据
         * 这里清空之后要使用 addAll() 的方式填充数据，不能直接 = ，否则Adapter的数据源将改变
         */
        mInvitedList.addAll(MLInvitedDao.getInstance().getInvitedList());
        if (mInvitedAdapter != null) {
            mInvitedAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击监听都要自己去做，然后通过回调接口实现
     */
    private void setItemClickListener() {
        mInvitedAdapter.setOnItemClickListener(new MLInvitedAdapter.MLOnItemClickListener() {
            /**
             * Item 点击及长按事件的处理
             * 这里Item的点击及长按监听都在 {@link MLInvitedAdapter} 实现，然后通过回调的方式，
             * 把操作的 Action 传递过来
             *
             * @param position 需要操作的Item的位置
             * @param action   长按菜单需要处理的动作，
             */
            @Override
            public void onItemAction(int position, int action) {
                switch (action) {
                case MLConstants.ML_ACTION_INVITED_CLICK:
                    checkInvitedDetail(position);
                    break;
                case MLConstants.ML_ACTION_INVITED_AGREE:
                    agreeInvited(position);
                    break;
                case MLConstants.ML_ACTION_INVITED_REFUSE:
                    refuseInvited(position);
                    break;
                case MLConstants.ML_ACTION_INVITED_DELETE:
                    deleteInvited(position);
                    break;
                }
            }
        });
    }

    /**
     * 查看当前申请信息的详情，并在详情界面做一些处理
     *
     * @param position 当前选中的 Invited 位置
     */
    private void checkInvitedDetail(int position) {
        MLInvitedEntity entity = mInvitedList.get(position);
        Intent intent = new Intent();
        intent.setClass(mActivity, MLInvitedDetailActivity.class);
        intent.putExtra(MLConstants.ML_EXTRA_INVITED_ID, entity.getInvitedId());
        mActivity.startActivity(intent);
    }

    /**
     * 同意好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void agreeInvited(int position) {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();

        final MLInvitedEntity invitedEntity = mInvitedList.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(invitedEntity.getUserName());
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
                    invitedEntity.setTime(MLDate.getCurrentMillisecond());
                    // 更新当前的申请信息
                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
                    dialog.dismiss();
                    mHandler.sendMessage(mHandler.obtainMessage(0));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    /**
     * 拒绝好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void refuseInvited(int positon) {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();
        final MLInvitedEntity invitedEntity = mInvitedList.get(positon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(invitedEntity.getUserName());
                    // 修改当前申请消息的状态
                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
                    invitedEntity.setTime(MLDate.getCurrentMillisecond());
                    // 更新当前的申请信息
                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
                    dialog.dismiss();
                    mHandler.sendMessage(mHandler.obtainMessage(0));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteInvited(int position) {
        final int index = position;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_invited));
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_content_delete_invited));
        dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MLInvitedDao.getInstance().deleteInvited(mInvitedList.get(index).getInvitedId());
                mHandler.sendMessage(mHandler.obtainMessage(0));
            }
        });
        dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLInvitedEvent event) {
        refreshInvited();
    }

    /**
     * 自定义Handler，用来处理界面的刷新
     */
    class MLHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
            case 0:
                // 刷新界面
                refreshInvited();
                break;
            }
        }
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshInvited();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
    }
}
