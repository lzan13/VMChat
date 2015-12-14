package net.melove.demo.chat.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLConversationAdapter;
import net.melove.demo.chat.info.MLConversationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单聊会话列表界面Fragment
 */
public class MLConversationsFragment extends MLBaseFragment {

    private Context mContext;
    private List<MLConversationEntity> mConversationList;

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
        mContext = getParentFragment().getActivity();
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

        mContext = getParentFragment().getActivity();

        init();
        initConversationListView();
    }

    private void init() {


    }

    private void initConversationListView() {
        MLConversationEntity temp = null;

        Map<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        mConversationList = new ArrayList<MLConversationEntity>();
        for (EMConversation conversation : conversations.values()) {
            temp = new MLConversationEntity(conversation);
            mConversationList.add(temp);
        }

        MLConversationAdapter mAdapter = new MLConversationAdapter(mContext, mConversationList);

        mListView = (ListView) getView().findViewById(R.id.ml_listview_conversation);
        mListView.setAdapter(mAdapter);
    }
}
