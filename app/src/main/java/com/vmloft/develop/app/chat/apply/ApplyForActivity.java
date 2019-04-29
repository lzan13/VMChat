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
import butterknife.ButterKnife;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.base.AppActivity;
import com.vmloft.develop.app.chat.contacts.UserActivity;
import com.vmloft.develop.app.chat.conversation.ConversationExtUtils;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;

/**
 * Created by lzan13 on 2015/8/28. 好友申请通知界面
 */
public class ApplyForActivity extends AppActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    // 当前会话对象，这里主要是记录申请与记录信息
    private EMConversation conversation;
    // 每次加载申请与通知消息的条数
    private int pageSize = 20;

    private ApplyForAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        ButterKnife.bind(activity);

        initView();
    }

    /**
     * 初始化界面控件等
     */
    private void initView() {

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
        conversation = EMClient.getInstance()
                .chatManager()
                .getConversation(AConstants.CONVERSATION_ID_APPLY, null, true);
        // 设置当前会话未读数为 0
        conversation.markAllMessagesAsRead();
        ConversationExtUtils.setConversationUnread(conversation, false);

        int count = conversation.getAllMessages().size();
        if (count < conversation.getAllMsgCount() && count < pageSize) {
            // 获取已经在列表中的最上边的一条消息id
            String msgId = conversation.getAllMessages().get(0).getMsgId();
            // 分页加载更多消息，需要传递已经加载的消息的最上边一条消息的id，以及需要加载的消息的条数
            conversation.loadMoreMsgFromDB(msgId, pageSize - count);
        }

        // 实例化适配器
        adapter = new ApplyForAdapter(activity);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // 设置适配器
        recyclerView.setAdapter(adapter);

        // 通过自定义接口来实现RecyclerView item的点击和长按事件
        setItemClickListener();
    }

    /**
     * 刷新邀请信息列表
     */
    private void refresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置列表项的点击监听，因为这里使用的是RecyclerView控件，所以长按和点击监听都要自己去做，然后通过回调接口实现
     */
    private void setItemClickListener() {
        adapter.setItemCallBack(new ItemCallBack() {
            @Override public void onAction(int action, Object tag) {
                String msgId = (String) tag;
                switch (action) {
                    case AConstants.ACTION_CLICK:
                        jumpUserInfo(msgId);
                        break;
                    case AConstants.ACTION_LONG_CLICK:
                        deleteApply(msgId);
                        break;
                    case AConstants.ACTION_AGREED:
                        agreeApply(msgId);
                        break;
                    case AConstants.ACTION_REJECT:
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
        EMMessage message = conversation.getMessage(msgId, false);
        Intent intent = new Intent();
        intent.setClass(activity, UserActivity.class);
        intent.putExtra(AConstants.EXTRA_CHAT_ID,
                message.getStringAttribute(AConstants.ATTR_USERNAME, "null"));
        intent.putExtra(AConstants.EXTRA_MSG_ID, msgId);
        activity.onStartActivity(activity, intent);
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
                    EMMessage message = conversation.getMessage(msgId, false);
                    String username = message.getStringAttribute(AConstants.ATTR_USERNAME, "");
                    EMClient.getInstance().contactManager().acceptInvitation(username);

                    // 更新当前的申请信息
                    message.setAttribute(AConstants.ATTR_STATUS,
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
                    EMMessage message = conversation.getMessage(msgId, false);
                    String username = message.getStringAttribute(AConstants.ATTR_USERNAME, "");
                    EMClient.getInstance().contactManager().declineInvitation(username);

                    // 更新当前的申请信息
                    message.setAttribute(AConstants.ATTR_STATUS,
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
                conversation.removeMessage(msgId);
            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
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
