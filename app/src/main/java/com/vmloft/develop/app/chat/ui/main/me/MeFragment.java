package com.vmloft.develop.app.chat.ui.main.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.base.AppFragment;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.ui.contacts.UserManager;
import com.vmloft.develop.app.chat.ui.contacts.UserActivity;

import com.vmloft.develop.library.tools.widget.VMImageView;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

/**
 * Created by lzan13 on 2017/1/9.
 * 当前账户标签部分
 */
public class MeFragment extends AppFragment {

    @BindView(R.id.img_avatar)
    VMImageView avatarView;

    private String currentUsername;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MeFragment
     */
    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MeFragment() {
    }

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    @Override
    protected int layoutId() {
        return R.layout.fragment_me;
    }

    /**
     * 初始化界面控件，将 Fragment 变量和 View 建立起映射关系
     */
    @Override
    protected void init() {
        super.init();

        currentUsername = (String) VMSPUtil.get(mContext, AConstants.SHARED_USERNAME, "");
        UserManager.getInstance().getUser(currentUsername);
    }

    @OnClick({R.id.img_avatar})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_avatar:
                Intent intent = new Intent(mContext, UserActivity.class);
                intent.putExtra(AConstants.EXTRA_CHAT_ID, currentUsername);
//                mContext.onStartActivity(mContext, intent);
                break;
        }
    }
}
