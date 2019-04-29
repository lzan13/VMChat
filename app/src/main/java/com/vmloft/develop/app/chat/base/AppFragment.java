package com.vmloft.develop.app.chat.base;

import com.vmloft.develop.library.tools.base.VMFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lzan13 on 2015/7/6.
 *
 * Fragment的基类，进行简单的封装
 */
public abstract class AppFragment extends VMFragment {

    private Unbinder unbinder;

    @Override
    protected void init() {
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
