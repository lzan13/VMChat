package net.melove.demo.chat.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLContactsAdapter;
import net.melove.demo.chat.db.MLUserDao;
import net.melove.demo.chat.entity.MLUserEntity;

import java.util.List;

/**
 * 单聊联系人界面 Fragment
 * 继承自自定义的MLBaseFramgnet类，为了减少代码量，在MLBaseFrament类中定义接口回调
 * 包含此Fragment的活动窗口必须实现{@link MLBaseFragment.OnMLFragmentListener}接口,
 * 定义创建实例的工厂方法 {@link MLContactsFragment#newInstance}，可使用此方法创建实例
 */
public class MLContactsFragment extends MLBaseFragment {

    private Context mContext;

    private ListView mListView;
    private View mHeadView;
    private MLContactsAdapter mContactsAdapter;

    private MLUserDao mUserDao;


    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLSingleContactsFragment
     */
    public static MLContactsFragment newInstance() {
        MLContactsFragment fragment = new MLContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getParentFragment().getActivity();

        init();

    }

    private void init() {
        mUserDao = new MLUserDao(mContext);

        List<MLUserEntity> list = mUserDao.getContactList();
        mContactsAdapter = new MLContactsAdapter(mContext, list);
        mListView = (ListView) getView().findViewById(R.id.ml_list_contacts);

        mListView.setAdapter(mContactsAdapter);

    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    };
}
