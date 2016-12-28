package net.melove.app.chat.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.leakcanary.RefWatcher;
import net.melove.app.chat.util.MLLog;

/**
 * Created by lzan13 on 2015/7/6.
 * Fragment的基类，进行简单的封装，实现 ViewPager 结合 Fragment 懒加载
 */
public abstract class MLBaseFragment extends Fragment {

    protected String className = this.getClass().getSimpleName();

    protected FragmentActivity mActivity;

    // 是否第一次加载
    protected boolean isFirstLoad = true;
    // 是否已经初始化 View
    protected boolean isInitView = false;
    // 是否显示
    protected boolean isVisible = false;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        MLLog.i("onAttach: %s", className);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLLog.i("onCreate: %s", className);
    }

    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 当前 Fragment 显示时加载数据
        if (isVisibleToUser) {
            isVisible = true;
            loadData();
        } else {
            isVisible = false;
        }
        MLLog.i("setUserVisibleHint: %s, %b", className, isVisibleToUser);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initLayoutId(), container, false);
        MLLog.i("onCreateView: %s", className);
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        isInitView = true;
        loadData();
        MLLog.i("onActivityCreated: %s", className);
    }

    @Override public void onStart() {
        super.onStart();
        MLLog.i("onStart: %s", className);
    }

    @Override public void onResume() {
        super.onResume();
        MLLog.i("onResume: %s", className);
    }

    @Override public void onPause() {
        super.onPause();
        MLLog.i("onPause: %s", className);
    }

    @Override public void onStop() {
        super.onStop();
        MLLog.i("onStop: %s", className);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        MLLog.i("onDestroyView: %s", className);
    }

    @Override public void onDetach() {
        super.onDetach();
        MLLog.i("onDetach: %s", className);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        MLLog.i("onDestroy: %s", className);
        RefWatcher refWatcher = MLApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    /**
     * 加载数据方法，是否真正加载由内部判断
     */
    private void loadData() {
        // 只是打印输出当前状态
        if (isFirstLoad) {
            MLLog.i("第一次加载数据 isVisible: %b, %s", isVisible, className);
        } else {
            MLLog.i("不是第一次加载数据 isVisible: %b, %s", isVisible, className);
        }
        // 这里确定要不要执行数据加载
        if (!isFirstLoad || !isVisible || !isInitView) {
            return;
        }
        // 满足条件，调用数据加载
        initData();
        isFirstLoad = false;
    }

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    protected abstract int initLayoutId();

    /**
     * 初始化界面控件，将 Fragment 变量和 View 建立起映射关系
     */
    protected abstract void initView();

    /**
     * 加载数据
     */
    protected abstract void initData();
}
