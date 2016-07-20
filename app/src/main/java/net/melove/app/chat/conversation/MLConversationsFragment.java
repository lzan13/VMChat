package net.melove.app.chat.conversation;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.eventbus.MLMessageEvent;
import net.melove.app.chat.communal.base.MLBaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private List<EMConversation> mConversations = new ArrayList<EMConversation>();

    private String[] mMenus = null;
    // 代替 ListView 用来显示会话列表的控件
    private RecyclerView mRecyclerView;
    private MLConversationAdapter mConversationAdapter;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog conversationMenuDialog;


    /**
     * 创建实例对象的工厂方法
     *
     * @return 返回一个新的实例
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
        mConversationAdapter = new MLConversationAdapter(mActivity, mConversations);
        // 初始化会话列表的 ListView 控件
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.ml_recyclerview_conversation);
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
        mRecyclerView.setAdapter(mConversationAdapter);

        // 通过自定义接口来实现RecyclerView item的点击和长按事件
        setItemClickListener();
    }

    /**
     * 刷新会话列表，重新加载会话列表到list集合，然后刷新adapter
     */
    public void refreshConversation() {
        if (mConversationAdapter != null) {
            mConversations.clear();
            loadConversationList();
            mConversationAdapter.refreshList();
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
        // 使用Collectons的sort()方法 对会话列表进行排序
        Collections.sort(list, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation lhs, EMConversation rhs) {
                /**
                 * 根据会话扩展中的时间进行排序
                 * 通过{@link MLConversationExtUtils#getConversationLastTime(EMConversation)} 获取时间
                 */
                if (MLConversationExtUtils.getConversationLastTime(lhs) > MLConversationExtUtils.getConversationLastTime(rhs)) {
                    return -1;
                } else if (MLConversationExtUtils.getConversationLastTime(lhs) < MLConversationExtUtils.getConversationLastTime(rhs)) {
                    return 1;
                }
                return 0;
            }
        });

        // 将列表排序之后，要重新将置顶的item设置到顶部
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (MLConversationExtUtils.getConversationPUSHPIN(list.get(i))) {
                mConversations.add(count, list.get(i));
                count++;
            } else {
                mConversations.add(list.get(i));
            }
        }
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击事件都需要去自己实现，这里通过回调接口实现
     */
    private void setItemClickListener() {

        mConversationAdapter.setOnItemClickListener(new MLConversationAdapter.MLOnItemClickListener() {
            /**
             * 会话列表想的点击监听
             * @param position 点击的项
             */
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.setClass(mActivity, MLChatActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mConversations.get(position).getUserName());
                mActivity.startActivity(intent);
            }

            /**
             * 实现 RecyclerView item 长按事件
             * @param position 触发长按事件的 position
             */
            @Override
            public void onItemLongClick(final int position) {
                final EMConversation conversation = mConversations.get(position);
                final boolean isTop = MLConversationExtUtils.getConversationPUSHPIN(conversation);
                // 根据当前会话不同的状态来显示不同的长按菜单
                List<String> menuList = new ArrayList<String>();
                if (isTop) {
                    menuList.add(mActivity.getResources().getString(R.string.ml_menu_conversation_cancel_top));
                } else {
                    menuList.add(mActivity.getResources().getString(R.string.ml_menu_conversation_top));
                }
                if (conversation.getUnreadMsgCount() > 0 || MLConversationExtUtils.getConversationUnread(conversation)) {
                    menuList.add(mActivity.getResources().getString(R.string.ml_menu_conversation_read));
                } else {
                    menuList.add(mActivity.getResources().getString(R.string.ml_menu_conversation_unread));
                }
                menuList.add(mActivity.getResources().getString(R.string.ml_menu_conversation_clear));
                menuList.add(mActivity.getResources().getString(R.string.ml_menu_conversation_delete));

                mMenus = new String[menuList.size()];
                menuList.toArray(mMenus);

                // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
                alertDialogBuilder = new AlertDialog.Builder(mActivity);

                alertDialogBuilder.setTitle(R.string.ml_dialog_title_conversation);
                alertDialogBuilder.setItems(mMenus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case 0:
                            // 根据当前状态设置会话是否置顶
                            if (isTop) {
                                MLConversationExtUtils.setConversationPushpin(conversation, false);
                            } else {
                                MLConversationExtUtils.setConversationPushpin(conversation, true);
                            }
                            refreshConversation();
                            break;
                        case 1:
                            if (conversation.getUnreadMsgCount() > 0 || MLConversationExtUtils.getConversationUnread(conversation)) {
                                conversation.markAllMessagesAsRead();
                                MLConversationExtUtils.setConversationUnread(conversation, false);
                            } else {
                                MLConversationExtUtils.setConversationUnread(conversation, true);
                            }
                            refreshConversation();
                            break;
                        case 2:
                            // 清空当前会话的消息，同时删除了内存中和数据库中的数据
                            mConversations.get(position).clearAllMessages();
                            refreshConversation();
                            break;
                        case 3:
                            // 删除当前会话，第二个参数表示是否删除此会话的消息
                            EMClient.getInstance().chatManager().deleteConversation(conversation.getUserName(), false);
                            refreshConversation();
                            break;
                        }
                    }
                });
                conversationMenuDialog = alertDialogBuilder.create();
                conversationMenuDialog.show();
            }
        });
    }


    /**
     * 作为{@link MLMessageEvent}事件订阅者需要实现的订阅方法，此方法永远运行在主线程
     *
     * @param event 订阅的事件类型
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLMessageEvent event) {
        // 调用界面刷新方法
        refreshConversation();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 注册订阅者，监听其它事件发送者发出的事件通知
        EventBus.getDefault().register(this);
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override
    public void onResume() {
        super.onResume();
        // 刷新会话界面
        refreshConversation();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
        // 取消观察者的注册
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        // 检测弹出框是否显示，显示中则销毁，防止因activity的销毁导致错误
        if (conversationMenuDialog != null && conversationMenuDialog.isShowing()) {
            conversationMenuDialog.dismiss();
        }
        super.onDestroy();
    }
}
