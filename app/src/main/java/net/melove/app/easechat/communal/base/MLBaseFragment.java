package net.melove.app.easechat.communal.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;

import net.melove.app.easechat.application.MLApplication;
import net.melove.app.easechat.communal.util.MLLog;

/**
 * Created by lzan13 on 2015/7/6.
 * Fragment的基类，定义Fragment公共接口回调
 */
public class MLBaseFragment extends Fragment {

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
        MLLog.i("%s onActivityCreated", this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLLog.i("%s onCreate", this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MLLog.i("%s onPause ", this.getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        super.onStart();
        MLLog.i("%s onStart ", this.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        MLLog.i("%s onStop ", this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MLLog.i("%s onResume ", this.getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MLLog.i("%s onDetach ", this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLLog.i("%s onDestroyView ", this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLLog.i("%s onDestroy ", this.getClass().getSimpleName());
        RefWatcher refWatcher = MLApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
