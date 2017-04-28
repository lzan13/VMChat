package com.vmloft.develop.app.chat.room;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.vmloft.develop.app.chat.app.AppFragment;
import com.vmloft.develop.app.chat.R;

/**
 * Created by lzan13 on 2016/12/7.
 * 聊天室列表
 */
public class RoomsFragment extends AppFragment {

    // 代替 ListView 用来显示联系人列表
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    // 联系人界面适配器
    private RoomsAdapter adapter;


    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLSingleContactsFragment
     */
    public static RoomsFragment newInstance() {
        RoomsFragment fragment = new RoomsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RoomsFragment() {
        // Required empty public constructor
    }

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    @Override protected int initLayoutId() {
        return R.layout.fragment_room;
    }

    /**
     * 初始化界面控件，将 Fragment 变量和 View 建立起映射关系
     */
    @Override protected void initView() {
        ButterKnife.bind(this, getView());
    }

    /**
     * 加载数据
     */
    @Override protected void initData() {

    }
}
