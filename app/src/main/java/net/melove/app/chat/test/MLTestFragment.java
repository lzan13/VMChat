package net.melove.app.chat.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.app.chat.R;
import net.melove.app.chat.MLConstants;
import net.melove.app.chat.MLHyphenate;
import net.melove.app.chat.ui.MLBaseFragment;
import net.melove.app.chat.util.MLDateUtil;
import net.melove.app.chat.util.MLLog;
import net.melove.app.chat.ui.widget.MLViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Fragment， 继承自自定义的MLBaseFramgnet类，为了减少代码量，在MLBaseFrament类中定义接口回调 包含此Fragment的活动窗口必须实现{@link
 * MLBaseFragment.OnMLFragmentListener}接口, 定义创建实例的工厂方法 {@link MLTestFragment#newInstance}，可使用此方法创建实例
 */
public class MLTestFragment extends MLBaseFragment {

    private OnMLFragmentListener mListener;
    private MLViewGroup viewGroup;

    /**
     * 使用这个工厂方法创建一个新的实例
     *
     * @return 一个新的Fragment MLTestFragment.
     */
    public static MLTestFragment newInstance() {
        MLTestFragment fragment = new MLTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MLTestFragment() {
        // Required empty public constructor
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        init();
    }

    private void init() {
        String[] btns = {
                "Sign out", "Import Message", "Insert Message", "Save Message", "Update Message",
                "Send Group", "Get Group", "Create Group", "Send CMD", "Get ChatRoom",
                "Get User", "Customer"
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
                    testGetGroup();
                    break;
                case 107:
                    testCreateGroup();
                    break;
                case 108:
                    sendCMDMessage();
                    break;
                case 109:
                    getChatRoom();
                    break;
                case 110:
                    testGetUser();
                    break;
                case 111:
                    testCustomer();
                    break;
            }
        }
    };

    /**
     * 测试联系客服发送扩展
     */
    private void testCustomer() {
        //创建一条文本消息,content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message =
                EMMessage.createTxtSendMessage("测试联系客服 " + MLDateUtil.getCurrentMillisecond(),
                        "ml_customer_01");
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
        try {
            JSONObject weichat = new JSONObject();
            JSONObject visitor = new JSONObject();
            visitor.put("trueName", "风中小莫");
            visitor.put("qq", "1234567890");
            visitor.put("email", "lzan13@easemob.com");
            weichat.put("visitor", visitor);
            message.setAttribute("weichat", weichat);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
     * 测试同步好友列表
     */
    private void testGetUser() {
        new Thread(new Runnable() {
            @Override public void run() {
                List<String> contacts = new ArrayList<String>();
                // TODO 切换账户时会出现第二个账户获取到的联系人是第一个账户的好友
                EMClient.getInstance()
                        .contactManager()
                        .aysncGetAllContactsFromServer(new EMValueCallBack<List<String>>() {
                            @Override public void onSuccess(List<String> list) {
                                MLLog.i("contacts count %d, names %s", list.size(),
                                        list.toString());
                            }

                            @Override public void onError(int i, String s) {

                            }
                        });
            }
        }).start();
    }

    /**
     * 获取聊天室
     */
    private void getChatRoom() {
        try {
            EMChatRoom chatRoom =
                    EMClient.getInstance().chatroomManager().fetchChatRoomFromServer("", true);
            chatRoom.getMemberList();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送透传消息
     */
    private void sendCMDMessage() {
        EMMessage cmdMessage = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action = "online";
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMessage.setReceipt("lz0");
        cmdMessage.addBody(cmdBody);
        cmdMessage.setAttribute("ml_online_client", "app");
        EMClient.getInstance().chatManager().sendMessage(cmdMessage);
    }

    int count = 0;

    /**
     * 测试创建群组
     */
    private void testCreateGroup() {
        new Thread(new Runnable() {
            @Override public void run() {
                /**
                 * 创建群组
                 * @param groupName 群组名称
                 * @param desc 群组简介
                 * @param allMembers 群组初始成员，如果没有就传空数组，不能为 null
                 * @param reason 邀请成员加入的reason
                 * @param option 群组类型选项，可以设置群组最大用户数(默认200)及群组类型@see {@link EMGroupStyle}
                 *               option里的GroupStyle分别为：
                 *               EMGroupStylePrivateOnlyOwnerInvite——私有群，只有群主可以邀请人；
                 *               EMGroupStylePrivateMemberCanInvite——私有群，群成员也能邀请人进群；
                 *               EMGroupStylePublicJoinNeedApproval——公开群，加入此群除了群主邀请，只能通过申请加入此群；
                 *               EMGroupStylePublicOpenJoin ——公开群，任何人都能加入此群
                 * @return 创建好的group
                 * @throws HyphenateException
                 */
                EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                option.maxUsers = 100;
                option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                String[] members = { "lz1", "lz2", "lz3" };
                try {
                    EMGroup group = EMClient.getInstance()
                            .groupManager()
                            .createGroup("测试群组" + count++, "SDK端创建群组，测试默认属性", members, "这个群不错",
                                    option);
                    Log.i("lzan13",
                            "group id: %s" + group.getGroupId() + "members: " + group.getMembers());
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
        //new Thread(new Runnable() {
        //    @Override public void run() {
        //        try {
        //            // 这个操作会导致 so 请求群详情崩溃
        //            EMGroup group = EMClient.getInstance()
        //                    .groupManager()
        //                    .getGroupFromServer("1476160492861");
        //            MLLog.i("group: name %s, member count %d, members", group.getGroupName(),
        //                    group.getMemberCount(), group.getMembers().toArray().toString());
        //        } catch (HyphenateException e) {
        //            e.printStackTrace();
        //        }
        //    }
        //}).start();

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
        recallMessage.setReceipt(message.getFrom());
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
        textMessage.setReceipt("lz1");
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
        textMessage.setReceipt("lz0");
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
        MLHyphenate.getInstance().signOut(new EMCallBack() {
            @Override public void onSuccess() {
                mListener.onFragmentClick(0x00, 0x01, null);
            }

            @Override public void onError(int i, String s) {

            }

            @Override public void onProgress(int i, String s) {

            }
        });
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnMLFragmentListener) context;
        } catch (ClassCastException e) {
            MLLog.e("必须实现Fragment的回调接口！");
            e.printStackTrace();
        }
    }
}
