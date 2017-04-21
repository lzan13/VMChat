package com.vmloft.develop.app.chat.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.leakcanary.RefWatcher;
import com.vmloft.develop.library.tools.VMBaseFragment;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2015/7/6.
 * Fragment的基类，进行简单的封装，ViewPager 结合 Fragment 实现数据懒加载
 */
public abstract class AppFragment extends VMBaseFragment {

    protected String className = this.getClass().getSimpleName();
}
