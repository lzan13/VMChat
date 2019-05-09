package com.vmloft.develop.app.chat.base;

import com.vmloft.develop.library.tools.base.VMLazyFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create by lzan13 on 2019/04/11
 *
 * 定义项目 Fragment 懒加载基类
 */
public abstract class AppLazyFragment extends VMLazyFragment {

    private Unbinder unbinder;

    @Override
    protected void initView() {
        unbinder = ButterKnife.bind(this, getView());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
