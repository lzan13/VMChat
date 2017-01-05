package net.melove.app.chat.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;

import android.widget.Button;
import butterknife.BindView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import java.util.ArrayList;
import java.util.List;
import net.melove.app.chat.R;
import net.melove.app.chat.sign.MLSignInActivity;
import net.melove.app.chat.widget.MLViewGroup;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.util.MLLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MLOtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MLOtherFragment extends MLBaseFragment {

    @BindView(R.id.view_custom_viewgroup) MLViewGroup viewGroup;

    private ProgressDialog mProgressDialog;

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
        mActivity = getActivity();

        String[] btns = {
                "Sign out", "Import Message", "Insert Message", "Save Message", "Update Message",
                "Send Group", "Sync Group", "Get Group", "Remove Group Member", "Push Message"
        };
        viewGroup = (MLViewGroup) getView().findViewById(R.id.view_custom_viewgroup);
        for (int i = 0; i < btns.length; i++) {
            Button btn = new Button(mActivity);
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
                    importMessages();
                    break;
                case 102:
                    insertMessage();
                    break;
                case 103:
                    saveMessage();
                    break;
                case 104:
                    updateMessage();
                    break;
                case 105:
                    sendGroupMessage();
                    break;
                case 106:
                    testSyncGroups();
                    break;
                case 107:
                    testGetGroup();
                    break;
                case 108:
                    testRemoveGroupMember();
                    break;
                case 109:
                    testPushGroupMessage();
                    break;
            }
        }
    };

    private void testPushGroupMessage() {
        new Thread(new Runnable() {
            @Override public void run() {
                //EMClient.getInstance().pushManager().enableOfflinePush();
                List<String> list = new ArrayList<>();
                list.add("283660121626640920");
                try {
                    EMClient.getInstance().pushManager().updatePushServiceForGroup(list, true);
                    List<String> noPustGroups = EMClient.getInstance().pushManager().getNoPushGroups();
                    MLLog.d("No push group: %d", noPustGroups.size());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void testRemoveGroupMember() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    EMGroup group =
                            EMClient.getInstance().groupManager().getGroup("283660121626640920");
                    MLLog.i("group: name %s, owner %s, member count %d", group.getGroupName(),
                            group.getOwner(), group.getMemberCount(),
                            group.getMembers().toArray().toString());
                    EMClient.getInstance()
                            .groupManager()
                            .removeUserFromGroup("277326560065749524",
                                    "6eba068614024e258ee8ffd284298aae");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 测试从服务器获取群详情
     */
    private void testGetGroup() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    // 这个操作会导致 so 请求群详情崩溃
                    EMGroup group = EMClient.getInstance()
                            .groupManager()
                            .getGroupFromServer("283660121626640920");
                    MLLog.i("group: name %s, owner %s, member count %d", group.getGroupName(),
                            group.getOwner(), group.getMemberCount(),
                            group.getMembers().toArray().toString());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void testSyncGroups() {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    List<EMGroup> groups =
                            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    MLLog.i("groups count: %d", groups.size());
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
        EMMessage message =
                EMMessage.createTxtSendMessage("群消息" + MLDateUtil.getCurrentMillisecond(),
                        "1460022071257");
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override public void onSuccess() {
                MLLog.i("message send success!");
            }

            @Override public void onError(int i, String s) {
                MLLog.i("message send error code:%d, error:%s", i, s);
            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 更新消息
     */
    private void updateMessage() {
        EMMessage message = EMMessage.createTxtSendMessage("", "");
        // 更改要撤销的消息的内容，替换为消息已经撤销的提示内容
        EMMessage recallMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody body = new EMTextMessageBody(
                String.format(mActivity.getString(R.string.ml_hint_msg_recall_by_user),
                        message.getUserName()));
        recallMessage.addBody(body);
        recallMessage.setTo(message.getFrom());
        // 设置新消息的 msgId为撤销消息的 msgId
        recallMessage.setMsgId(message.getMsgId());
        // 设置新消息的 msgTime 为撤销消息的 mstTime
        recallMessage.setMsgTime(message.getMsgTime());
        // 设置扩展为撤回消息类型，是为了区分消息的显示
        recallMessage.setAttribute(MLConstants.ML_ATTR_RECALL, true);
        // 返回修改消息结果
        boolean result = EMClient.getInstance().chatManager().updateMessage(recallMessage);
    }

    /**
     * 保存一条消息到本地
     */
    private void saveMessage() {
        EMMessage textMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        textMessage.setFrom("lz0");
        textMessage.setTo("lz1");
        textMessage.setStatus(EMMessage.Status.SUCCESS);
        EMTextMessageBody body = new EMTextMessageBody("test save message");
        textMessage.addBody(body);
        EMClient.getInstance().chatManager().saveMessage(textMessage);
    }

    /**
     * 测试插入一条消息到当前会话
     */
    private void insertMessage() {
        // 测试插入一条消息
        EMConversation conversation = EMClient.getInstance()
                .chatManager()
                .getConversation("lz1", EMConversation.EMConversationType.Chat, true);
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
        String msgJson = "[{\n" +
                "\"uuid\": \"5dd2241a-4ffa-11e6-9396-31c48b60c199\",\n" +
                "\"type\": \"chatmessage\",\n" +
                "\"created\": 1469184741585,\n" +
                "\"modified\": 1469184741585,\n" +
                "\"timestamp\": 1469184741193,\n" +
                "\"from\": \"lz0\",\n" +
                "\"msg_id\": \"221705959213369320\",\n" +
                "\"to\": \"lz1\",\n" +
                "\"chat_type\": \"chat\",\n" +
                "\"payload\": {\n" +
                "    \"bodies\": [\n" +
                "      {\n" +
                "        \"msg\": \"特殊\",\n" +
                "        \"type\": \"txt\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"ext\": {}\n" +
                "  }\n" +
                "},\n" +
                "{\n" +
                "\"uuid\": \"f8235b18-5240-11e6-9c2c-096df3a4c703\",\n" +
                "\"type\": \"chatmessage\",\n" +
                "\"created\": 1469434967583,\n" +
                "\"modified\": 1469434967583,\n" +
                "\"timestamp\": 1469434967362,\n" +
                "\"from\": \"lz0\",\n" +
                "\"msg_id\": \"222780672404621268\",\n" +
                "\"to\": \"lz1\",\n" +
                "\"chat_type\": \"chat\",\n" +
                "\"payload\": {\n" +
                "    \"bodies\": [\n" +
                "      {\n" +
                "        \"msg\": \"到三点\",\n" +
                "        \"type\": \"txt\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"ext\": {}\n" +
                "  }\n" +
                "}]";
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

                messageList.add(message);
            }
            EMClient.getInstance().chatManager().importMessages(messageList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录
     */
    private void signOut() {

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(mActivity.getString(R.string.ml_hint_sign_out));
        mProgressDialog.show();

        MLHyphenate.getInstance().signOut(new EMCallBack() {
            @Override public void onSuccess() {
                mProgressDialog.dismiss();
                mActivity.startActivity(new Intent(mActivity, MLSignInActivity.class));
                mActivity.finish();
            }

            @Override public void onError(int i, String s) {

            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }
}
