package net.melove.app.chat.communal.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.squareup.leakcanary.RefWatcher;

import net.melove.app.chat.application.MLApplication;
import net.melove.app.chat.communal.util.MLLog;

/**
 * Created by lzan13 on 2015/7/6.
 * Fragment的基类，定义Fragment公共接口回调
 */
public class MLBaseFragment extends Fragment {

    private String className = this.getClass().getSimpleName();

    protected FragmentActivity mActivity;

    /**
     * 这个接口必须由包含此Fragment的Activity来实现，用来实现Activity和Fragment的交互，
     * 以及当前Fragment与其它Fragment的交互
     * 具体的可参考官方文档：
     * 与其它Fragment进行沟通：http://developer.android.com/training/basics/fragments/communicating.html
     */
    public static interface OnMLFragmentListener {
        public void onFragmentClick(int a, int b, String s);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MLLog.i("%s onActivityCreated", className);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLLog.i("%s onCreate", className);
    }

    @Override
    public void onPause() {
        super.onPause();
        MLLog.i("%s onPause ", className);
    }

    @Override
    public void onStart() {
        super.onStart();
        MLLog.i("%s onStart ", className);
    }

    @Override
    public void onStop() {
        super.onStop();
        MLLog.i("%s onStop ", className);
    }

    @Override
    public void onResume() {
        super.onResume();
        MLLog.i("%s onResume ", className);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MLLog.i("%s onDetach ", className);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLLog.i("%s onDestroyView ", className);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLLog.i("%s onDestroy ", className);
        RefWatcher refWatcher = MLApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
