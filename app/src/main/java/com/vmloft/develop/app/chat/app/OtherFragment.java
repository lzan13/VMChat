package com.vmloft.develop.app.chat.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;

import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.vmloft.develop.app.chat.sign.SignInActivity;
import com.vmloft.develop.library.tools.widget.VMViewGroup;
import java.util.ArrayList;
import java.util.List;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.library.tools.utils.VMDateUtil;
import com.vmloft.develop.library.tools.utils.VMLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherFragment extends AppFragment {

    @BindView(R.id.view_custom_viewgroup) VMViewGroup viewGroup;

    private ProgressDialog progressDialog;

    /**
     * 工厂方法，用来创建一个Fragment的实例
     *
     * @return OtherFragment
     */
    public static OtherFragment newInstance() {
        OtherFragment fragment = new OtherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OtherFragment() {
        // Required empty public constructor
    }

    /**
     * 初始化 Fragment 界面 layout_id
     *
     * @return 返回布局 id
     */
    @Override protected int initLayoutId() {
        return R.layout.fragment_other;
    }

    /**
     * 初始化界面控件，将 Fragment 变量和 View 建立起映射关系
     */
    @Override protected void initView() {

        ButterKnife.bind(this, getView());

        String[] btns = {
                "Sign out", "Test 1", "Test 2", "Test 3"
        };
        for (int i = 0; i < btns.length; i++) {
            Button btn = new Button(new ContextThemeWrapper(activity, R.style.VMBtn_Green), null, 0);
            btn.setText(btns[i]);
            btn.setId(100 + i);
            btn.setOnClickListener(viewListener);
            viewGroup.addView(btn);
        }
    }

    /**
     * 加载数据
     */
    @Override protected void initData() {

    }

    /**
     * 测试按钮的监听事件
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            switch (v.getId()) {
                case 100:
                    signOut();
                    break;
                case 101:
                    testGetGroupDetails();
                    break;
                case 102:
                    break;
                case 103:

                    break;
            }
        }
    };

    /**
     * -------------------------------- 推送相关 ---------------------------
     * 设置推送免打扰时间段
     */
    private void testPushConfig() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMClient.getInstance().pushManager().disableOfflinePush(0, 24);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ----------------------------- 消息相关 --------------------
     */
    private void testGetMessage() {
        EMConversation conversation =
                EMClient.getInstance().chatManager().getConversation("lz2", EMConversation.EMConversationType.Chat, true);
        EMMessage message = conversation.getMessage("123123", true);

        VMLog.d("message id is %s", message != null ? message.getMsgId() : null);
    }

    /**
     * --------------------------------- Group 相关 ---------------------------------
     * 创建群组
     */
    private void testCreateGroup() {
        new Thread(new Runnable() {
            @Override public void run() {
                EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();
                options.maxUsers = 1500;
                options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;

                List<String> hxids = new ArrayList<String>();
                for (int i = 0; i < 500; i++) {
                    hxids.add("lds_" + i);
                }
                String[] members = hxids.toArray(new String[0]);

                try {
                    EMGroup group =
                            EMClient.getInstance().groupManager().createGroup("测试加入500人", "群组简介", members, "邀请其他人的理由", options);
                    VMLog.d("text create group success");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取群组详情
     */
    private void testGetGroupDetails() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer("16364170444803");
                    // 当前群里总人数
                    int groupCount = group.getMemberCount();
                    int count = 0;
                    // 测试分页获取群成员列表
                    EMCursorResult<String> result = null;
                    do {
                        result = EMClient.getInstance()
                                .groupManager()
                                .fetchGroupMembers("16364170444803", result != null ? result.getCursor() : "", 5);
                        count += result.getData().size();
                        VMLog.d("group members result: count: %d, %s", result.getData().size(), result.getData().toString());
                    } while (count < groupCount - 1);
                    VMLog.d("group details: %s", group.getDescription());
                } catch (HyphenateException e) {
                    VMLog.e("group details error: code - %d, msg - %s", e.getErrorCode(), e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 邀请加入群组
     */
    private void invitationJoinGroup() {
        new Thread(new Runnable() {
            @Override public void run() {
                String groupId = "5658110918657";
                String[] usernames = { "lz2" };
                String reason = "邀请加入群组理由";
                try {
                    EMClient.getInstance().groupManager().inviteUser(groupId, usernames, reason);
                    VMLog.i("invitation join group success");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 测试给不存在的群发送消息
     */
    private void sendGroupMessage() {
        //创建一条文本消息,content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage("群消息" + VMDateUtil.getCurrentMillisecond(), "9946217381889");
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        message.setMessageStatusCallback(new EMCallBack() {
            @Override public void onSuccess() {
                VMLog.i("message send success!");
            }

            @Override public void onError(int i, String s) {
                VMLog.i("message send error code:%d, error:%s", i, s);
            }

            @Override public void onProgress(int i, String s) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    /**
     * --------------------------------- Message 相关 ---------------------------------
     * 更新消息
     */
    private void updateMessage() {
        EMMessage message = EMMessage.createTxtSendMessage("", "");
        // 更改要撤销的消息的内容，替换为消息已经撤销的提示内容
        EMMessage recallMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody body =
                new EMTextMessageBody(String.format(activity.getString(R.string.hint_msg_recall_by_user), message.getUserName()));
        recallMessage.addBody(body);
        recallMessage.setTo(message.getFrom());
        // 设置新消息的 msgId为撤销消息的 msgId
        recallMessage.setMsgId(message.getMsgId());
        // 设置新消息的 msgTime 为撤销消息的 mstTime
        recallMessage.setMsgTime(message.getMsgTime());
        // 设置扩展为撤回消息类型，是为了区分消息的显示
        recallMessage.setAttribute(Constants.ATTR_RECALL, true);
        // 返回修改消息结果
        boolean result = EMClient.getInstance().chatManager().updateMessage(recallMessage);
    }

    /**
     * 保存一条消息到本地
     */
    private void saveMessage() {
        EMMessage textMessage = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        textMessage.setChatType(EMMessage.ChatType.ChatRoom);
        textMessage.setFrom("lz0");
        textMessage.setTo("14359007920129");
        textMessage.setStatus(EMMessage.Status.SUCCESS);
        EMTextMessageBody body = new EMTextMessageBody("test save chatroom message");
        textMessage.addBody(body);
        EMClient.getInstance().chatManager().saveMessage(textMessage);
    }

    /**
     * 测试插入一条消息到当前会话
     */
    private void insertMessage() {
        // 测试插入一条消息
        EMConversation conversation =
                EMClient.getInstance().chatManager().getConversation("lz1", EMConversation.EMConversationType.Chat, true);
        EMMessage textMessage = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        textMessage.setFrom("lz1");
        textMessage.setTo("lz0");
        textMessage.setStatus(EMMessage.Status.SUCCESS);
        EMTextMessageBody body = new EMTextMessageBody("test insert message");
        textMessage.addBody(body);
        conversation.insertMessage(textMessage);
        conversation.markMessageAsRead(textMessage.getMsgId());
    }

    /**
     * 导入多条消息
     */
    private void importMessages() {
        String msgJson = "[{\n"
                + "\"uuid\": \"5dd2241a-4ffa-11e6-9396-31c48b60c199\",\n"
                + "\"type\": \"chatmessage\",\n"
                + "\"created\": 1469184741585,\n"
                + "\"modified\": 1469184741585,\n"
                + "\"timestamp\": 1469184741193,\n"
                + "\"from\": \"lz0\",\n"
                + "\"msg_id\": \"221705959213369320\",\n"
                + "\"to\": \"lz1\",\n"
                + "\"chat_type\": \"chat\",\n"
                + "\"payload\": {\n"
                + "    \"bodies\": [\n"
                + "      {\n"
                + "        \"msg\": \"特殊\",\n"
                + "        \"type\": \"txt\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"ext\": {}\n"
                + "  }\n"
                + "},\n"
                + "{\n"
                + "\"uuid\": \"f8235b18-5240-11e6-9c2c-096df3a4c703\",\n"
                + "\"type\": \"chatmessage\",\n"
                + "\"created\": 1469434967583,\n"
                + "\"modified\": 1469434967583,\n"
                + "\"timestamp\": 1469434967362,\n"
                + "\"from\": \"lz0\",\n"
                + "\"msg_id\": \"222780672404621268\",\n"
                + "\"to\": \"lz2\",\n"
                + "\"chat_type\": \"chat\",\n"
                + "\"payload\": {\n"
                + "    \"bodies\": [\n"
                + "      {\n"
                + "        \"msg\": \"到三点\",\n"
                + "        \"type\": \"txt\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"ext\": {}\n"
                + "  }\n"
                + "}]";
        List<EMMessage> messageList = new ArrayList<EMMessage>();
        try {
            JSONArray jsonArray = new JSONArray(msgJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String uuid = jsonObject.optString("uuid");
                String type = jsonObject.optString("type");
                long created = jsonObject.optLong("created");
                long modified = jsonObject.optLong("modified");
                long timestamp = jsonObject.optLong("timestamp");
                String from = jsonObject.optString("from");
                String to = jsonObject.optString("to");
                String msgId = jsonObject.optString("msg_id");
                String chatType = jsonObject.optString("chat_type");

                JSONObject payload = jsonObject.optJSONObject("payload");
                JSONObject body = payload.optJSONArray("bodies").getJSONObject(0);

                String msgStr = body.optString("msg");
                String msgTypeStr = body.optString("type");
                String ext = jsonObject.optString("ext");

                EMMessage message = EMMessage.createTxtSendMessage(msgStr, to);
                message.setFrom(from);
                message.setMsgTime(timestamp);
                message.setMsgId(msgId);
                message.setChatType(EMMessage.ChatType.Chat);
                message.setStatus(EMMessage.Status.SUCCESS);

                EMClient.getInstance()
                        .chatManager()
                        .getConversation(message.getTo(), EMConversation.EMConversationType.Chat, true);
                messageList.add(message);
            }
            VMLog.d("conversation 1- count: %d", EMClient.getInstance().chatManager().getAllConversations().size());
            EMClient.getInstance().chatManager().importMessages(messageList);
            EMClient.getInstance().chatManager().loadAllConversations();
            VMLog.d("conversation 2- count: %d", EMClient.getInstance().chatManager().getAllConversations().size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录
     */
    private void signOut() {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.hint_sign_out));
        progressDialog.show();

        Hyphenate.getInstance().signOut(new EMCallBack() {
            @Override public void onSuccess() {
                progressDialog.dismiss();
                activity.runOnUiThread(new Runnable() {
                    @Override public void run() {
                        activity.onStartActivity(activity, new Intent(activity, SignInActivity.class));
                        activity.finishAfterTransition();
                    }
                });
            }

            @Override public void onError(int i, String s) {

            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }
}
