package net.melove.demo.chat.conversation;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.main.MLMainActivity;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.common.base.MLBaseFragment;
import net.melove.demo.chat.common.widget.MLToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 单聊会话列表界面Fragment
 */
public class MLConversationsFragment extends MLBaseFragment {

    // 保存会话对象的集合
    private List<EMConversation> mConversationList;

    private String[] mMenus = null;
    // 用来显示会话列表的控件
    private ListView mListView;
    private MLConversationAdapter mAdapter;


    /**
     * 创建实例对象的工厂方法
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mAdapter.refreshList();
    }

    /**
     * 加载会话对象到 List 集合，并根据最后一条消息时间进行排序
     */
    public void loadConversationList() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        mConversationList = new ArrayList<EMConversation>();
        synchronized (conversations) {
            for (EMConversation temp : conversations.values()) {
                MLLog.d("loadConversation - name - %s, %s, %d", temp.getUserName(), temp.getLastMessage(), temp.getAllMsgCount());
                mConversationList.add(temp);
            }
        }

        // 使用Collectons的sort()方法 对会话列表进行排序
//        Collections.sort(mConversationList, new Comparator<EMConversation>() {
//            @Override
//            public int compare(EMConversation lhs, EMConversation rhs) {
//                // 这里判断的是当有一方的值为空的情况下，尽量把空值的一方挪到下边，让有消息的一方排在上边，
//                // 防止空值的某一项一直处在最上方
//                if (lhs.getAllMessages().size() == 0 && rhs.getAllMessages().size() == 0) {
//                    return 0;
//                } else if (lhs.getAllMessages().size() == 0 && rhs.getAllMessages().size() > 0) {
//                    return -1;
//                } else if (lhs.getAllMessages().size() >= 0 && rhs.getAllMessages().size() == 0) {
//                    return 1;
//                }
//                // 判断需要排序的两项的顺序
//                if (lhs.getLastMessage().getMsgTime() > rhs.getLastMessage().getMsgTime()) {
//                    return 1;
//                } else if (lhs.getLastMessage().getMsgTime() > rhs.getLastMessage().getMsgTime()) {
//                    return -1;
//                }
//                return 0;
//            }
//        });
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
        mMenus = new String[]{
                mActivity.getResources().getString(R.string.ml_menu_conversation_top),
                mActivity.getResources().getString(R.string.ml_menu_conversation_clear),
                mActivity.getResources().getString(R.string.ml_menu_conversation_delete)
        };
        // ListView 的长按监听
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.ml_dialog_title_conversation)
                        .setItems(mMenus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        MLToast.makeToast(R.string.ml_menu_conversation_top).show();
                                        break;
                                    case 1:
                                        MLToast.makeToast(R.string.ml_menu_conversation_clear).show();
                                        break;
                                    case 2:
                                        MLToast.makeToast(R.string.ml_menu_conversation_delete).show();
                                        break;
                                }
                            }
                        })
                        .show();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshConversation();
    }
}
