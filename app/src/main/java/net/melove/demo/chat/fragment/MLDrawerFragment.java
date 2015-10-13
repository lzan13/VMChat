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
import net.melove.demo.chat.activity.MLUserInfoActivity;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.db.MLUserDao;
import net.melove.demo.chat.info.MLUserInfo;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.util.MLSPUtil;
import net.melove.demo.chat.widget.MLImageView;

/**
 * 侧滑抽屉Fragment，
 * 继承自自定义的MLBaseFramgnet类，为了减少代码量，在MLBaseFrament类中定义接口回调
 * 包含此Fragment的活动窗口必须实现{@link MLBaseFragment.OnMLFragmentListener}接口,
 * 定义创建实例的工厂方法 {@link MLDrawerFragment#newInstance}，可使用此方法创建实例
 */
public class MLDrawerFragment extends MLBaseFragment {

    private MLUserDao mUserDao;
    private MLUserInfo mUserInfo;
    private String mUsername;

    // 初始化获取参数key
    private static final String ML_PARAM1 = "param1";
    private static final String ML_PARAM2 = "param2";

    //
    private String mParam1;
    private String mParam2;

    private Activity mActivity;

    private ImageView mCover;
    private MLImageView mAvatar;
    private TextView mSigninText;
    private TextView mNicknameText;
    private TextView mSignatureText;
    private View mChatMenu, mOtherMenu, mSettingMenu;

    private OnMLFragmentListener mListener;

    public static MLDrawerFragment newInstance(DrawerLayout layout) {
        return newInstance(layout, "", "");
    }

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @param layout
     * @param param1
     * @param param2
     * @return MLDrawerFragment
     */
    public static MLDrawerFragment newInstance(DrawerLayout layout, String param1, String param2) {
        MLDrawerFragment fragment = new MLDrawerFragment();
        Bundle args = new Bundle();
        args.putString(ML_PARAM1, param1);
        args.putString(ML_PARAM2, param2);
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
            mParam1 = getArguments().getString(ML_PARAM1);
            mParam2 = getArguments().getString(ML_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        init();
        initView();
    }

    private void init() {
        mUserDao = new MLUserDao(mActivity);
        mUsername = (String) MLSPUtil.get(mActivity, MLConstants.ML_C_USERNAME, "");
        mUserInfo = mUserDao.getContact(mUsername);
    }

    /**
     * 初始化侧滑抽屉菜单
     */
    private void initView() {
        mCover = (ImageView) getView().findViewById(R.id.ml_img_drawer_top_cover);
        mAvatar = (MLImageView) getView().findViewById(R.id.ml_img_drawer_top_avatar);
        mSigninText = (TextView) getView().findViewById(R.id.ml_text_drawer_signin);
        mNicknameText = (TextView) getView().findViewById(R.id.ml_text_drawer_top_nickname);
        mSignatureText = (TextView) getView().findViewById(R.id.ml_text_drawer_top_signature);

        if (!mUsername.isEmpty()) {
            mAvatar.setOnClickListener(viewListener);
            mSigninText.setVisibility(View.GONE);
            mNicknameText.setText(mUsername);
            mSignatureText.setText(mUserInfo.getSignature());
        } else {
            mSigninText.setVisibility(View.VISIBLE);
            mSigninText.setOnClickListener(viewListener);
            mNicknameText.setText(R.string.ml_signin);
            mSignatureText.setText(R.string.ml_signature);
        }


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
            Intent intent = null;
            switch (v.getId()) {
                case R.id.ml_img_drawer_top_avatar:
                    intent = new Intent();
                    if (mUsername != null) {
                        intent.setClass(mActivity, MLUserInfoActivity.class);
                    } else {
                        intent.setClass(mActivity, MLSigninActivity.class);
                    }
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity,
                            R.anim.ml_anim_slide_right_in, R.anim.ml_anim_slide_left_out);
                    ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
                    mActivity.finish();
                    break;
                case R.id.ml_text_drawer_signin:
                    intent = new Intent();
                    intent.setClass(mActivity, MLSigninActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                    break;
                case R.id.ml_layout_btn_chat:
                    onListener(0x10, 0x00);
                    break;
                case R.id.ml_layout_btn_other:
                    onListener(0x10, 0x01);
                    break;
                case R.id.ml_layout_btn_setting:
                    onListener(0x10, 0x03);
                    break;
            }
        }
    };

    public void onListener(int a, int b) {
        if (mListener != null) {
            mListener.onFragmentClick(a, b, null);
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
