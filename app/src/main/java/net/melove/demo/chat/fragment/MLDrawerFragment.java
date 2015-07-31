package net.melove.demo.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.activity.MLSigninActivity;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLImageView;

/**
 * 侧滑抽屉Fragment，
 * 继承自自定义的MLBaseFramgnet类，为了减少代码量，在MLBaseFrament类中定义接口回调
 * 包含此Fragment的活动窗口必须实现{@link MLBaseFragment.OnMLFragmentListener}接口,
 * 定义创建实例的工厂方法 {@link MLDrawerFragment#newInstance}，可使用此方法创建实例
 */
public class MLDrawerFragment extends MLBaseFragment {
    // 初始化获取参数key
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //
    private String mParam1;
    private String mParam2;

    private Activity mActivity;

    private ImageView mCover;
    private MLImageView mAvatar;
    private TextView mNickname;
    private TextView mSignature;
    private View mChatMenu, mOtherMenu, mSettingMenu;

    private OnMLFragmentListener mListener;

    public static MLDrawerFragment newInstance(DrawerLayout layout) {
        return newInstance(layout, "", "");
    }

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @param param1
     * @param param2
     * @return
     */
    public static MLDrawerFragment newInstance(DrawerLayout layout, String param1, String param2) {
        MLDrawerFragment fragment = new MLDrawerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        layout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);

        return fragment;
    }

    public MLDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    private void init() {
        mCover = (ImageView) getView().findViewById(R.id.ml_img_drawer_top_cover);
        mAvatar = (MLImageView) getView().findViewById(R.id.ml_img_drawer_top_avatar);
        mAvatar.setOnClickListener(viewListener);
        mNickname = (TextView) getView().findViewById(R.id.ml_text_drawer_top_nickname);
        mSignature = (TextView) getView().findViewById(R.id.ml_text_drawer_top_signature);

        mChatMenu = getView().findViewById(R.id.ml_layout_btn_chat);
        mOtherMenu = getView().findViewById(R.id.ml_layout_btn_other);
        mSettingMenu = getView().findViewById(R.id.ml_layout_btn_setting);
        mChatMenu.setOnClickListener(viewListener);
        mOtherMenu.setOnClickListener(viewListener);
        mSettingMenu.setOnClickListener(viewListener);

    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_img_drawer_top_avatar:
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MLSigninActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity,
                            R.anim.ml_fade_in, R.anim.ml_fade_out);
                    ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                    mActivity.finish();
                    break;
                case R.id.ml_layout_btn_chat:
                    onListener(R.id.ml_layout_btn_chat);
                    break;
                case R.id.ml_layout_btn_other:
                    onListener(R.id.ml_layout_btn_other);
                    break;
                case R.id.ml_layout_btn_setting:
                    onListener(R.id.ml_layout_btn_setting);
                    break;
            }
        }
    };

    public void onListener(int i) {
        if (mListener != null) {
            mListener.onClick(i);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMLFragmentListener) activity;
        } catch (ClassCastException e) {
            MLLog.e("必须实现Fragment的回调接口！");
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
