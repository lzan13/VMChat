package net.melove.app.chat.ui.main;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.melove.app.chat.R;
import net.melove.app.chat.ui.MLBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MLOtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MLOtherFragment extends MLBaseFragment {

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLOtherFragment
     */
    public static MLOtherFragment newInstance() {
        MLOtherFragment fragment = new MLOtherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLOtherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

}
