package com.vmloft.develop.app.chat.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.base.AppFragment;
import com.vmloft.develop.app.chat.widget.recycler.LinearLayoutManager;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人界面 Fragment
 * 定义创建实例的工厂方法 {@link ContactsFragment#newInstance}，可使用此方法创建实例
 */
public class ContactsFragment extends AppFragment {

    // 代替 ListView 用来显示联系人列表
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    // 联系人界面适配器
    private ContactsAdapter adapter;

    private List<UserEntity> contactsList = new ArrayList<>();

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return MLSingleContactsFragment
     */
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    @Override
    protected int layoutId() {
        return R.layout.fragment_contacts;
    }

    /**
     * 初始化界面
     */
    @Override
    protected void init() {
        super.init();
        loadContactsList();
        adapter = new ContactsAdapter(mContext, contactsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        setItemCallBack();
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击监听都要自己去做，然后通过回调接口实现
     */
    private void setItemCallBack() {
        adapter.setItemCallBack(new ItemCallBack() {
            @Override
            public void onAction(int action, Object tag) {
                int position = (int) tag;
                switch (action) {
                    case R.id.img_avatar:
                        itemClick(position);
                        break;
                    case AConstants.ACTION_CLICK:
                        itemClick(position);
                        break;
                    case AConstants.ACTION_LONG_CLICK:
                        itemLongClick(position);
                        break;
                }
            }
        });
    }

    /**
     * 列表项点击事件
     */
    private void itemClick(int position) {
        UserEntity userEntity = contactsList.get(position);
        Intent intent = new Intent();
        intent.setClass(mContext, UserActivity.class);
        intent.putExtra(AConstants.EXTRA_CHAT_ID, userEntity.getUserName());
//        mContext.onStartActivity(mContext, intent);
    }

    /**
     * 列表项长按事件
     */
    private void itemLongClick(int position) {

    }

    /**
     * 加载联系人列表
     */
    private void loadContactsList() {
        if (contactsList == null) {
            contactsList = new ArrayList<>();
            contactsList.addAll(UserManager.getInstance().getContactsMap().values());
        } else {
            contactsList.clear();
            contactsList.addAll(UserManager.getInstance().getContactsMap().values());
        }
    }

    /**
     * 刷新邀请信息界面
     */
    private void refreshContactsList() {
        loadContactsList();
        if (adapter == null) {
            adapter = new ContactsAdapter(mContext, contactsList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(ContactsEvent event) {
//        refreshContactsList();
//    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override
    public void onResume() {
        super.onResume();
        // 刷新联系人界面
        refreshContactsList();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
    }
}
