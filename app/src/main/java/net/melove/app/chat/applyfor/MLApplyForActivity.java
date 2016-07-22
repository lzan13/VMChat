package net.melove.app.chat.applyfor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import net.melove.app.chat.R;
import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.eventbus.MLApplyForEvent;
import net.melove.app.chat.communal.base.MLBaseActivity;
import net.melove.app.chat.contacts.MLContactsInfoActivity;
import net.melove.app.chat.conversation.MLConversationExtUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lzan13 on 2015/8/28.
 * 好友申请通知界面
 */
public class MLApplyForActivity extends MLBaseActivity {


    private Toolbar mToolbar;

    // 当前会话对象，这里主要是记录申请与记录信息
    private EMConversation mConversation;
    // 每次加载申请与通知消息的条数
    private int mPageSize = 30;

    private RecyclerView mRecyclerView;
    private MLApplyForAdapter mApplyForAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apply_for);


        initView();
        initToolbar();
        initListView();

    }

    /**
     * 初始化Toolbar组件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFinish();
            }
        });
    }

    /**
     * 初始化界面控件等
     */
    private void initView() {
        mActivity = this;

    }

    /**
     * 初始化邀请信息列表
     */
    private void initListView() {
        /**
         * 初始化会话对象，这里有三个参数，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         * 因为申请与通知信息都是通过 EMConversation 保存的，所以这里也是通过 EMConversation 来获取
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(MLConstants.ML_CONVERSATION_ID_APPLY_FOR, null, true);
        // 设置当前会话未读数为 0
        mConversation.markAllMessagesAsRead();
        MLConversationExtUtils.setConversationUnread(mConversation, false);
        int count = mConversation.getAllMessages().size();
        if (count < mConversation.getAllMsgCount() && count < mPageSize) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = mConversation.getAllMessages().get(0).getMsgId();
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - count);
        }

        // 实例化适配器
        mApplyForAdapter = new MLApplyForAdapter(mActivity);
        mRecyclerView = (RecyclerView) findViewById(R.id.ml_recyclerview_invited);

        /**
         * 为RecyclerView 设置布局管理器，这里使用线性布局
         * RececlerView 默认的布局管理器：
         * LinearLayoutManager          显示垂直滚动列表或水平的项目
         * GridLayoutManager            显示在一个网格项目
         * StaggeredGridLayoutManager   显示在交错网格项目
         * 自定义的布局管理器，需要继承 {@link android.support.v7.widget.RecyclerView.LayoutManager}
         *
         * add/remove items时的动画是默认启用的。
         * 自定义这些动画需要继承{@link android.support.v7.widget.RecyclerView.ItemAnimator}，
         * 并实现{@link RecyclerView#setItemAnimator(RecyclerView.ItemAnimator)}
         */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        // 设置适配器
        mRecyclerView.setAdapter(mApplyForAdapter);

        // 通过自定义接口来实现RecyclerView item的点击和长按事件
        setItemClickListener();
    }

    /**
     * 刷新邀请信息列表
     */
    private void refreshInvited() {
        /**
         * 更新数据源的数据
         * 这里清空之后要使用 addAll() 的方式填充数据，不能直接 = ，否则Adapter的数据源将改变
         */
        if (mApplyForAdapter != null) {
            mApplyForAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击监听都要自己去做，然后通过回调接口实现
     */
    private void setItemClickListener() {
        mApplyForAdapter.setOnItemClickListener(new MLApplyForAdapter.MLOnItemClickListener() {
            /**
             * Item 点击及长按事件的处理
             * 这里Item的点击及长按监听都在 {@link MLApplyForAdapter} 实现，然后通过回调的方式，
             * 把操作的 Action 传递过来
             *
             * @param position 需要操作的Item的位置
             * @param action   长按菜单需要处理的动作，
             */
            @Override
            public void onItemAction(int position, int action) {
                switch (action) {
                case MLConstants.ML_ACTION_APPLY_FOR_CLICK:
                    jumpUserInfo(position);
                    break;
                case MLConstants.ML_ACTION_APPLY_FOR_AGREE:
                    agreeInvited(position);
                    break;
                case MLConstants.ML_ACTION_APPLY_FOR_REFUSE:
                    refuseInvited(position);
                    break;
                case MLConstants.ML_ACTION_APPLY_FOR_DELETE:
                    deleteInvited(position);
                    break;
                }
            }
        });
    }

    /**
     * 查看当前申请信息的详情，并在详情界面做一些处理
     *
     * @param position 当前选中的 Invited 位置
     */
    private void jumpUserInfo(int position) {
        EMMessage message = mConversation.getAllMessages().get(position);
        Intent intent = new Intent();
        intent.setClass(mActivity, MLContactsInfoActivity.class);
        intent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, message.getStringAttribute(MLConstants.ML_ATTR_USERNAME, "null"));
        mActivity.startActivity(intent);
    }

    /**
     * 同意好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void agreeInvited(int position) {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_message_waiting));
        dialog.show();

        //        final MLInvitedEntity invitedEntity = mInvitedList.get(position);
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    EMClient.getInstance().contactManager().acceptInvitation(invitedEntity.getUserName());
        //                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.AGREED);
        //                    invitedEntity.setTime(MLDateUtil.getCurrentMillisecond());
        //                    // 更新当前的申请信息
        //                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
        //                    dialog.dismiss();
        //                } catch (HyphenateException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }).start();


    }

    /**
     * 拒绝好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void refuseInvited(int positon) {
        //        final ProgressDialog dialog = new ProgressDialog(mActivity);
        //        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_message_waiting));
        //        dialog.show();
        //        final MLInvitedEntity invitedEntity = mInvitedList.get(positon);
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        //                    EMClient.getInstance().contactManager().declineInvitation(invitedEntity.getUserName());
        //                    // 修改当前申请消息的状态
        //                    invitedEntity.setStatus(MLInvitedEntity.InvitedStatus.REFUSED);
        //                    invitedEntity.setTime(MLDateUtil.getCurrentMillisecond());
        //                    // 更新当前的申请信息
        //                    MLInvitedDao.getInstance().updateInvited(invitedEntity);
        //                    dialog.dismiss();
        //                } catch (HyphenateException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }).start();
    }

    private void deleteInvited(final int position) {
        final int index = position;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_apply_for));
        dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_content_delete_invited));
        dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mConversation.removeMessage(mConversation.getAllMessages().get(position).getMsgId());
            }
        });
        dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * 使用 EventBus 的订阅方式监听事件的变化，这里 EventBus 3.x 使用注解的方式确定方法调用的线程
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(MLApplyForEvent event) {
        refreshInvited();
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshInvited();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
