package net.melove.demo.chat.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.activity.MLNewApplyForActivity;

/**
 * 单聊联系人界面 Fragment
 */
public class MLContactsFragment extends MLBaseFragment {

    private Activity mActivity;

    private ListView mListView;
    private View mHeadView;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @param param1
     * @param param2
     * @return MLSingleContactsFragment
     */
    public static MLContactsFragment newInstance(String param1, String param2) {
        MLContactsFragment fragment = new MLContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MLContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        init();

    }

    private void init() {

        mHeadView = LayoutInflater.from(mActivity).inflate(R.layout.item_contacts_head, null);
        mHeadView.findViewById(R.id.ml_btn_contacts_add).setOnClickListener(viewListener);
        mHeadView.findViewById(R.id.ml_btn_contacts_group).setOnClickListener(viewListener);
        mHeadView.findViewById(R.id.ml_btn_contacts_room).setOnClickListener(viewListener);

        mListView = (ListView) getView().findViewById(R.id.ml_list_contacts);
        mListView.addHeaderView(mHeadView);
        mListView.setAdapter();

    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_contacts_add:
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MLNewApplyForActivity.class);
                    mActivity.startActivity(intent);
                    break;
                case R.id.ml_btn_contacts_group:

                    break;
                case R.id.ml_btn_contacts_room:

                    break;
            }
        }
    };
}
