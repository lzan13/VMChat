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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
    private ListView mListView;
    private MLInvitedAdapter mInvitedAdapter;

    // 应用内广播管理器，为了完全这里使用局域广播
    private LocalBroadcastManager mLocalBroadcastManager;
    // 会话界面监听会话变化的广播接收器
    private BroadcastReceiver mBroadcastReceiver;

    public MLInvitedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invited, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initView();
    }

    private void init() {
        mActivity = getActivity();
        mInvitedDao = new MLInvitedDao(mActivity);
    }


    /**
     * 初始化界面控件等
     */
    private void initView() {

        mInvitedList = mInvitedDao.getInvitedList();
        mInvitedAdapter = new MLInvitedAdapter(mActivity, mInvitedList);
        // 初始化ListView
        mListView = (ListView) getView().findViewById(R.id.ml_listview_invited);
        mListView.setAdapter(mInvitedAdapter);

        // 设置申请列表项的点击事件
        setItemClickListener();
        // 设置申请与邀请列表项长按事件监听
        setItemLongClickListener();
        // 设置当前界面数据为空的状态
        mListView.setEmptyView(getView().findViewById(R.id.ml_layout_empty));
    }

    /**
     * 设置列表项点击事件监听
     */
    private void setItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MLToast.makeToast("item " + position).show();
            }
        });
    }

    /**
     * 设置列表项的长按监听
     */
    private void setItemLongClickListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_apply_for));
                dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_content_add_contact));
                dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    /**
     * @return 返回本地申请与邀请的信息
     */
    private List<MLInvitedEntity> loadInvitedList() {
        List<MLInvitedEntity> list = mInvitedDao.getInvitedList();
        return list;
    }


    /**
     * 刷新申请请求列表
     */
    private void refreshInvited() {

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
