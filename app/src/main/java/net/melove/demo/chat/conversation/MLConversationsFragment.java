package net.melove.demo.chat.conversation;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.base.MLBaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 会话列表界面Fragment
 */
public class MLConversationsFragment extends MLBaseFragment {

    // 保存会话对象的集合
    private List<EMConversation> mConversationList = new ArrayList<EMConversation>();

    private String[] mMenus = null;
    // 用来显示会话列表的控件
    private ListView mListView;
    private MLConversationAdapter mAdapter;

    // 应用内广播管理器，为了完全这里使用局域广播
    private LocalBroadcastManager mLocalBroadcastManager;
    // 会话界面监听会话变化的广播接收器
    private BroadcastReceiver mBroadcastReceiver;

    /**
     * 创建实例对象的工厂方法
     *
     * @return
     */
    public static MLConversationsFragment newInstance() {
        MLConversationsFragment fragment = new MLConversationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 构造方法
     */
    public MLConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 因为当前 Fragment 的父容器也是 Fragment，因此获取当前的 Activity 需要通过父容器来获得
        mActivity = getParentFragment().getActivity();

        initView();
    }

    /**
     * 初始化会话列表界面
     */
    private void initView() {
        // 加载会话到list集合
        loadConversationList();
        // 实例化会话列表的 Adapter 对象
        mAdapter = new MLConversationAdapter(mActivity, mConversationList);
        // 初始化会话列表的 ListView 控件
        mListView = (ListView) getView().findViewById(R.id.ml_listview_conversation);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(0);

        // 设置列表项点击监听
        setItemClickListener();
        // 设置列表项长按监听
        setItemLongClickListener();
    }

    /**
     * 刷新会话列表，重新加载会话列表到list集合，然后刷新adapter
     */
    public void refreshConversation() {
        mConversationList.clear();
        loadConversationList();
        if (mAdapter != null) {
            mAdapter.refreshList();
        }
    }

    /**
     * 加载会话对象到 List 集合，并根据最后一条消息时间进行排序
     */
    public void loadConversationList() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<EMConversation> list = new ArrayList<EMConversation>();
        synchronized (conversations) {
            for (EMConversation temp : conversations.values()) {
                list.add(temp);
            }
        }
        MLLog.i("sort - 1 - %d", MLDate.getCurrentMillisecond());
        // 使用Collectons的sort()方法 对会话列表进行排序
        Collections.sort(list, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation lhs, EMConversation rhs) {
                /**
                 * 根据会话扩展中的时间进行排序
                 * 通过{@link MLConversationExtUtils#getConversationLastTime(EMConversation)} 获取时间
                 */
                if (MLConversationExtUtils.getConversationLastTime(lhs)
                        > MLConversationExtUtils.getConversationLastTime(rhs)) {
                    return -1;
                } else if (MLConversationExtUtils.getConversationLastTime(lhs)
                        < MLConversationExtUtils.getConversationLastTime(rhs)) {
                    return 1;
                }
                return 0;
            }
        });

        // 将列表排序之后，要重新将置顶的item设置到顶部
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (MLConversationExtUtils.getConversationTop(list.get(i))) {
                mConversationList.add(count, list.get(i));
                count++;
            } else {
                mConversationList.add(list.get(i));
            }
        }
        MLLog.i("sort - 2 - %d", MLDate.getCurrentMillisecond());
    }

    /**
     * ListView 控件点击监听
     */
    private void setItemClickListener() {
        // ListView 的点击监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(mActivity, MLChatActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mConversationList.get(position).getUserName());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity);
                ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
            }
        });
    }

    /**
     * ListView 列表项的长按监听
     */
    private void setItemLongClickListener() {

        // ListView 的长按监听
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final EMConversation conversation = mConversationList.get(position);
                final boolean isTop = MLConversationExtUtils.getConversationTop(conversation);
                // 根据当前会话不同的状态来显示不同的长按菜单
                if (isTop) {
                    mMenus = new String[]{
                            mActivity.getResources().getString(R.string.ml_menu_conversation_cancel_top),
                            mActivity.getResources().getString(R.string.ml_menu_conversation_clear),
                            mActivity.getResources().getString(R.string.ml_menu_conversation_delete)
                    };
                } else {
                    mMenus = new String[]{
                            mActivity.getResources().getString(R.string.ml_menu_conversation_top),
                            mActivity.getResources().getString(R.string.ml_menu_conversation_clear),
                            mActivity.getResources().getString(R.string.ml_menu_conversation_delete)
                    };
                }
                // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.ml_dialog_title_conversation)
                        .setItems(mMenus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // 根据当前状态设置会话是否置顶
                                        if (isTop) {
                                            MLConversationExtUtils.setConversationTop(conversation, false);
                                        } else {
                                            MLConversationExtUtils.setConversationTop(conversation, true);
                                        }
                                        refreshConversation();
                                        break;
                                    case 1:
                                        // 清空当前会话的消息，同时删除了内存中和数据库中的数据
                                        mConversationList.get(position).clearAllMessages();
                                        refreshConversation();
                                        break;
                                    case 2:
                                        // 删除当前会话，第二个参数表示是否删除此会话的消息
                                        EMClient.getInstance().chatManager().deleteConversation(conversation.getUserName(), false);
                                        refreshConversation();
                                        break;
                                }
                            }
                        }).show();
                return true;
            }
        });
    }

    /**
     * 注册广播接收器，用来监听全局监听监听到新消息之后发送的广播，然后刷新界面
     */
    private void registerBroadcastReceiver() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MLConstants.ML_ACTION_MESSAGE);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshConversation();
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
        // 刷新会话界面
        refreshConversation();
        // 注册广播监听
        registerBroadcastReceiver();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
        // 注册广播监听
        unregisterBroadcastReceiver();
    }
}
