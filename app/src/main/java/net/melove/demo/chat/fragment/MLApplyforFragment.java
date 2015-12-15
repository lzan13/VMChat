package net.melove.demo.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.activity.MLBaseActivity;
import net.melove.demo.chat.activity.MLUserInfoActivity;
import net.melove.demo.chat.adapter.MLApplyForAdapter;
import net.melove.demo.chat.db.MLApplyForDao;
import net.melove.demo.chat.entity.MLApplyForEntity;
import net.melove.demo.chat.widget.MLToast;

import java.util.List;

/**
 * Created by lzan13 on 2015/3/28.
 */
public class MLApplyforFragment extends MLBaseFragment {

    private Activity mActivity;

    private ViewStub mViewStub;

    private MLApplyForDao mApplyForDao;
    private List<MLApplyForEntity> mList;
    private MLApplyForAdapter mApplyForAdapter;
    private ListView mListView;

    public MLApplyforFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apply_for, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initView();
    }

    private void init() {
        mActivity = getActivity();
        mApplyForDao = new MLApplyForDao(mActivity);
    }


    private void initView() {

        mList = mApplyForDao.getApplyForList();
        mApplyForAdapter = new MLApplyForAdapter(mActivity, mList);
        // 初始化ListView
        mListView = (ListView) getView().findViewById(R.id.ml_listview_applyfor);
        mListView.setAdapter(mApplyForAdapter);

        setItemClickListener();
        if (mList.size() == 0) {
            mViewStub = (ViewStub) getView().findViewById(R.id.ml_viewstub);
            mViewStub.setVisibility(View.VISIBLE);
        }
    }

    /**
     * by lzan13 2015-11-2 11:25:04
     * 列表项点击事件
     */
    private void setItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MLToast.makeToast("item " + position).show();
            }
        });
    }

}
