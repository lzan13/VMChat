package net.melove.demo.chat.communal.base;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.squareup.leakcanary.RefWatcher;

import net.melove.demo.chat.application.MLApplication;
import net.melove.demo.chat.communal.util.MLLog;

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
    public void onPause() {
        super.onPause();
        MLLog.i("Fragment onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        MLLog.i("Fragment onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        MLLog.i("Fragment onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        MLLog.i("Fragment onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLLog.i("Fragment onDestroy");
        RefWatcher refWatcher = MLApplication.getRefWatcher();
        refWatcher.watch(this);
    }
}
