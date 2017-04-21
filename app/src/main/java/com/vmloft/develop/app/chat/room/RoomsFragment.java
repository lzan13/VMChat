package com.vmloft.develop.app.chat.room;

import butterknife.ButterKnife;
import com.vmloft.develop.app.chat.app.AppFragment;
import com.vmloft.develop.app.chat.R;

/**
 * Created by lzan13 on 2016/12/7.
 * 聊天室列表
 */
public class RoomsFragment extends AppFragment {

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
