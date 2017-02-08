package net.melove.app.chat.setting;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import net.melove.app.chat.R;
import net.melove.app.chat.app.MLBaseActivity;
import net.melove.app.chat.app.MLBaseFragment;
import net.melove.app.chat.app.MLConstants;
import net.melove.app.chat.contacts.MLUserActivity;
import net.melove.app.chat.contacts.MLUserManager;
import net.melove.app.chat.util.MLSPUtil;
import net.melove.app.chat.widget.MLImageView;

/**
 * Created by lzan13 on 2017/1/9.
 * 当前账户标签部分
 */
public class MLMeFragment extends MLBaseFragment {

    @BindView(R.id.img_avatar) MLImageView mAvatarView;

    private String mCurrentUsername;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLMeFragment
     */
    public static MLMeFragment newInstance() {
        MLMeFragment fragment = new MLMeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLMeFragment() {
        mCurrentUsername = (String) MLSPUtil.get(MLConstants.ML_SHARED_USERNAME, "");
    }

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    @Override protected int initLayoutId() {
        return R.layout.fragment_me;
    }

    /**
     * 初始化界面控件，将 Fragment 变量和 View 建立起映射关系
     */
    @Override protected void initView() {
        ButterKnife.bind(this, getView());

        mActivity = getActivity();
    }

    /**
     * 加载数据
     */
    @Override protected void initData() {
        MLUserManager.getInstance().getUser(mCurrentUsername);
    }

    @OnClick({ R.id.img_avatar }) void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_avatar:
                Intent intent = new Intent(mActivity, MLUserActivity.class);
                intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, mCurrentUsername);

                ((MLBaseActivity) mActivity).onStartActivity(mActivity, intent, mAvatarView);
                break;
        }
    }
}
