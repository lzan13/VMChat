package net.melove.demo.chat.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import net.melove.demo.chat.R;
import net.melove.demo.chat.activity.MLChatActivity;
import net.melove.demo.chat.activity.MLMainActivity;
import net.melove.demo.chat.adapter.MLConversationAdapter;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.entity.MLConversationEntity;
import net.melove.demo.chat.widget.MLToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单聊会话列表界面Fragment
 */
public class MLConversationsFragment extends MLBaseFragment {

    private List<MLConversationEntity> mConversationList;
    private String[] mMenus = null;
    private ListView mListView;
    private MLConversationAdapter mAdapter;


    public static MLConversationsFragment newInstance() {
        MLConversationsFragment fragment = new MLConversationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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

        mActivity = getParentFragment().getActivity();

        init();
        initConversationListView();
    }

    private void init() {
    }

    /**
     * 初始化会话列表
     */
    private void initConversationListView() {
        MLConversationEntity temp = null;

        Map<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        mConversationList = new ArrayList<MLConversationEntity>();
        for (EMConversation conversation : conversations.values()) {
            temp = new MLConversationEntity(conversation);
            mConversationList.add(temp);
        }

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

    public void refrshConversation() {
        MLConversationEntity temp = null;
        mConversationList.clear();
        Map<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        mConversationList = new ArrayList<MLConversationEntity>();
        for (EMConversation conversation : conversations.values()) {
            temp = new MLConversationEntity(conversation);
            mConversationList.add(temp);
        }
        mAdapter.refreshList();
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
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mConversationList.get(position).getChatId());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, ((MLMainActivity) mActivity).getToolbar(), "toolbar");
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
}
