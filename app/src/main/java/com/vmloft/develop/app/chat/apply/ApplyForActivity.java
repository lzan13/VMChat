package com.vmloft.develop.app.chat.apply;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.app.AppActivity;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.app.chat.contacts.UserActivity;
import com.vmloft.develop.app.chat.conversation.ConversationExtUtils;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lzan13 on 2015/8/28. 好友申请通知界面
 */
public class ApplyForActivity extends AppActivity {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    // 当前会话对象，这里主要是记录申请与记录信息
    private EMConversation mConversation;
    // 每次加载申请与通知消息的条数
    private int mPageSize = 20;

    private ApplyForAdapter mAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        initView();
    }

    /**
     * 初始化界面控件等
     */
    private void initView() {
        activity = this;

        getToolbar().setTitle(R.string.apply);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onFinish();
            }
        });

        /**
         * 初始化会话对象，这里有三个参数，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         * 因为申请与通知信息都是通过 EMConversation 保存的，所以这里也是通过 EMConversation 来获取
         */
        mConversation = EMClient.getInstance()
                .chatManager()
                .getConversation(Constants.CONVERSATION_ID_APPLY, null, true);
        // 设置当前会话未读数为 0
        mConversation.markAllMessagesAsRead();
        ConversationExtUtils.setConversationUnread(mConversation, false);

        int count = mConversation.getAllMessages().size();
        if (count < mConversation.getAllMsgCount() && count < mPageSize) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = mConversation.getAllMessages().get(0).getMsgId();
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            mConversation.loadMoreMsgFromDB(msgId, mPageSize - count);
        }

        // 实例化适配器
        mAdapter = new ApplyForAdapter(activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // 设置适配器
        mRecyclerView.setAdapter(mAdapter);

        // 通过自定义接口来实现RecyclerView item的点击和长按事件
        setItemClickListener();
    }

    /**
     * 刷新邀请信息列表
     */
    private void refresh() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击监听都要自己去做，然后通过回调接口实现
     */
    private void setItemClickListener() {
        mAdapter.setItemCallBack(new ItemCallBack() {
            @Override public void onAction(int action, Object tag) {
                String msgId = (String) tag;
                switch (action) {
                    case Constants.ACTION_CLICK:
                        jumpUserInfo(msgId);
                        break;
                    case Constants.ACTION_LONG_CLICK:
                        deleteApply(msgId);
                        break;
                    case Constants.ACTION_AGREED:
                        agreeApply(msgId);
                        break;
                    case Constants.ACTION_REJECT:
                        rejectApply(msgId);
                        break;
                }
            }
        });
    }

    /**
     * 查看当前申请用户的详细信息
     *
     * @param msgId 当前操作 Item 项申请信息 id
     */
    private void jumpUserInfo(String msgId) {
        EMMessage message = mConversation.getMessage(msgId, false);
        Intent intent = new Intent();
        intent.setClass(activity, UserActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_ID,
                message.getStringAttribute(Constants.ATTR_USERNAME, "null"));
        intent.putExtra(Constants.EXTRA_MSG_ID, msgId);
        activity.startActivity(intent);
    }

    /**
     * 同意好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     *
     * @param msgId 当前操作项的消息 id
     */
    private void agreeApply(final String msgId) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getResources().getString(R.string.dialog_message_waiting));
        dialog.show();

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    // 同意申请
                    EMMessage message = mConversation.getMessage(msgId, false);
                    String username = message.getStringAttribute(Constants.ATTR_USERNAME, "");
                    EMClient.getInstance().contactManager().acceptInvitation(username);

                    // 更新当前的申请信息
                    message.setAttribute(Constants.ATTR_STATUS,
                            activity.getString(R.string.agreed));
                    EMClient.getInstance().chatManager().updateMessage(message);
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 拒绝好友请求，环信的同意和拒绝好友请求 都需要异步处理，这里新建线程去调用
     */
    private void rejectApply(final String msgId) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getResources().getString(R.string.dialog_message_waiting));
        dialog.show();

        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    // 拒绝好友申请
                    EMMessage message = mConversation.getMessage(msgId, false);
                    String username = message.getStringAttribute(Constants.ATTR_USERNAME, "");
                    EMClient.getInstance().contactManager().declineInvitation(username);

                    // 更新当前的申请信息
                    message.setAttribute(Constants.ATTR_STATUS,
                            activity.getString(R.string.rejected));
                    EMClient.getInstance().chatManager().updateMessage(message);
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 删除申请通知
     *
     * @param msgId 当前操作的 item 消息 id
     */
    private void deleteApply(final String msgId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(activity.getResources().getString(R.string.dialog_title_apply));
        dialog.setMessage(
                activity.getResources().getString(R.string.dialog_content_delete_invited));
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                mConversation.removeMessage(msgId);
            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * 使用 EventBus 的订阅方式监听事件的变化，这里 EventBus 3.x 使用注解的方式确定方法调用的线程
     */
    @Subscribe(threadMode = ThreadMode.MAIN) public void onEventBus(ApplyEvent event) {
        refresh();
    }

    /**
     * 重写父类的onResume方法， 在这里注册广播
     */
    @Override public void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 重写父类的onStop方法，在这里边记得将注册的广播取消
     */
    @Override public void onStop() {
        super.onStop();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }
}
