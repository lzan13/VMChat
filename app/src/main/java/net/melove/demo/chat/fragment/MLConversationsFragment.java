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

    // 异步加载数据管理器
    private LoaderManager mLoaderManager;
    private List<MLConversationEntity> mConversationList;
    private String[] mMenus = null;
    private ListView mListView;


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
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(10001, null, callback);
    }

    private void initConversationListView() {
        MLConversationEntity temp = null;

        Map<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        mConversationList = new ArrayList<MLConversationEntity>();
        for (EMConversation conversation : conversations.values()) {
            temp = new MLConversationEntity(conversation);
            mConversationList.add(temp);
        }

        // 实例化会话列表的 Adapter 对象
        MLConversationAdapter mAdapter = new MLConversationAdapter(mActivity, mConversationList);

        // 初始化会话列表的 ListView 控件
        mListView = (ListView) getView().findViewById(R.id.ml_listview_conversation);
        mListView.setAdapter(mAdapter);
        // 设置列表项点击监听
        setItemClickListener();

        // 设置列表项长按监听
        setItemLongClickListener();
        // 设置长按弹出上下文菜单，和 onContextItemSelected 配套使用
//        setContextMenuListener();

    }

    private void setContextMenuListener() {
        mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "Delete");
                menu.add(0, 1, 0, "Top");
                menu.add(0, 2, 0, "Other");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:

                break;
            case 1:
                break;
            case 2:

                break;
        }
        return super.onContextItemSelected(item);
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
                intent.putExtra(MLConstants.ML_C_CHAT_ID, mConversationList.get(position).getChatId());
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

    private LoaderManager.LoaderCallbacks<Cursor> callback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
}
