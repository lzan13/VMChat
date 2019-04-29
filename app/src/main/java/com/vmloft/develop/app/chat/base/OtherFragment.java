package com.vmloft.develop.app.chat.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.View;

import android.widget.Button;

import butterknife.BindView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.library.tools.utils.VMDate;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.widget.VMViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherFragment extends AppFragment {

    @BindView(R.id.view_custom_viewgroup)
    VMViewGroup viewGroup;

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
    @Override
    protected int layoutId() {
        return R.layout.fragment_other;
    }

    /**
     * 初始化界面控件，将 Fragment 变量和 View 建立起映射关系
     */
    @Override
    protected void init() {
        super.init();

        String[] btns = {
                "Sign out", "Test 1", "Test 2", "Test 3"
        };
        for (int i = 0; i < btns.length; i++) {
            Button btn = new Button(new ContextThemeWrapper(mContext, R.style.VMBtn_Green), null, 0);
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
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 100:
                    signOut();
                    break;
                case 101:
                    saveMessage();
                    break;
                case 102:
                    importMessages();
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
            @Override
            public void run() {
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
    /**
     * 测试搜索消息
     */
    private void testSearchMessage() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation("lz1");
        //List<EMMessage> list = conversation.searchMsgFromDB(EMMessage.Type.TXT, System.currentTimeMillis(), 100, "",
        //        EMConversation.EMSearchDirection.UP);
        List<EMMessage> list =
                conversation.searchMsgFromDB(System.currentTimeMillis(), System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000,
                        100);
        VMLog.i("search message list %d, %s", list.size(), list.toString());
    }

    /**
     * 测试获取消息
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
            @Override
            public void run() {
                EMGroupOptions options = new EMGroupOptions();
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
    private void testFetchGroupMembers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    EMGroup group = EMClient.getInstance().groupManager().getGroup("19369877897217");
                    // 当前群里总人数
                    int groupCount = group.getMemberCount();
                    int count = 0;
                    // 测试分页获取群成员列表
                    EMCursorResult<String> result = null;
                    do {
                        result = EMClient.getInstance()
                                .groupManager()
                                .fetchGroupMembers("19369877897217", result != null ? result.getCursor() : "", 5);
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
            @Override
            public void run() {
                String groupId = "5658110918657";
                String[] usernames = {"lz2"};
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
        EMMessage message = EMMessage.createTxtSendMessage("群消息" + VMDate.currentMilli(), "9946217381889");
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                VMLog.i("message send success!");
            }

            @Override
            public void onError(int i, String s) {
                VMLog.i("message send error code:%d, error:%s", i, s);
            }

            @Override
            public void onProgress(int i, String s) {

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
                new EMTextMessageBody(String.format(mContext.getString(R.string.hint_msg_recall_by_user), message.getUserName()));
        recallMessage.addBody(body);
        recallMessage.setTo(message.getFrom());
        // 设置新消息的 msgId为撤销消息的 msgId
        recallMessage.setMsgId(message.getMsgId());
        // 设置新消息的 msgTime 为撤销消息的 mstTime
        recallMessage.setMsgTime(message.getMsgTime());
        // 设置扩展为撤回消息类型，是为了区分消息的显示
        recallMessage.setAttribute(AConstants.ATTR_RECALL, true);
        // 返回修改消息结果
        boolean result = EMClient.getInstance().chatManager().updateMessage(recallMessage);
    }

    /**
     * 保存一条消息到本地
     */
    private void saveMessage() {
        EMConversation conversation =
                EMClient.getInstance().chatManager().getConversation("lz0", EMConversation.EMConversationType.Chat, true);
        EMMessage textMessage = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        textMessage.setChatType(EMMessage.ChatType.Chat);
        textMessage.setFrom("lz0");
        textMessage.setTo("lz1");
        textMessage.setStatus(EMMessage.Status.SUCCESS);
        textMessage.setUnread(true);
        EMTextMessageBody body = new EMTextMessageBody("test save message");
        textMessage.addBody(body);
        VMLog.i("conversation all message -0- %d", conversation.getAllMessages().size());
        EMClient.getInstance().chatManager().saveMessage(textMessage);
        VMLog.i("conversation all message -1- %d", conversation.getAllMessages().size());
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
        textMessage.setStatus(EMMessage.Status.INPROGRESS);
        textMessage.setUnread(true);
        EMTextMessageBody body = new EMTextMessageBody("test insert message");
        textMessage.addBody(body);
        VMLog.i("conversation all message -0- %d", conversation.getAllMessages().size());
        conversation.insertMessage(textMessage);
        VMLog.i("conversation all message -1- %d", conversation.getAllMessages().size());

        conversation.removeMessage(textMessage.getMsgId());

        VMLog.i("conversation all message -2- %d", conversation.getAllMessages().size());
        //// 图片大小部分因为 size 方法没有设置为 public，所以这个需要通过反射调用这个方法
        //EMMessage imgMessage = EMMessage.createReceiveMessage(EMMessage.Type.IMAGE);
        //imgMessage.setFrom("");
        //imgMessage.setTo("");
        //File file = new File("你本地路径，开始应该是没有的");
        //EMImageMessageBody imgBody = new EMImageMessageBody(file);
        //imgBody.setThumbnailUrl("缩略图远程路径");
        //imgBody.setThumbnailLocalPath("缩略图本地路径");
        //imgBody.setFileName("");
        //imgBody.setRemoteUrl("远程路径");
        //imgBody.setLocalUrl("本地路径");
        //imgMessage.addBody(imgBody);
        //EMClient.getInstance().chatManager().saveMessage(imgMessage);

    }

    /**
     * 导入多条消息
     */
    private void importMessages() {
        String msgJson = "[{\n"
                + "    \"from\": \"lz1\",\n"
                + "    \"msg_id\": \"350475701050148824\",\n"
                + "    \"payload\": {\n"
                + "        \"ext\": {},\n"
                + "        \"bodies\": [{\n"
                + "            \"secret\": \"lRjEWmCoEee9vfnPereH2W_JZYRVXhhnyCYX7ejQ5Y2Va6SN\",\n"
                + "            \"size\": {\n"
                + "                \"width\": 288,\n"
                + "                \"height\": 384\n"
                + "            },\n"
                + "            \"filename\": \"1499166285637\",\n"
                + "            \"url\": \"https://a1.easemob.com/1188170313178478/youhaodongxi/chatfiles/9518c450-60a8-11e7-8229-e3a51fcf21a3\",\n"
                + "            \"type\": \"img\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"direction\": \"\",\n"
                + "    \"timestamp\": \"1499166285638\",\n"
                + "    \"to\": \"19369592684545\",\n"
                + "    \"chat_type\": \"GroupChat\"\n"
                + "},\n"
                + "{\n"
                + "    \"from\": \"lz2\",\n"
                + "    \"msg_id\": \"350475800664868824\",\n"
                + "    \"payload\": {\n"
                + "        \"ext\": {},\n"
                + "        \"bodies\": [{\n"
                + "            \"secret\": \"ouuV2mCoEeeZx7_YUlvSbZggsYQGkVkok97rpEyJDKPm5j3P\",\n"
                + "            \"length\": 2,\n"
                + "            \"thumb_secret\": \"oqzfKmCoEeebQ9fCtwzh9qz80EMC-5Aa4SIzlVf1yFGHox4o\",\n"
                + "            \"size\": {\n"
                + "                \"width\": 360,\n"
                + "                \"height\": 480\n"
                + "            },\n"
                + "            \"file_length\": 258008,\n"
                + "            \"filename\": \"149916630777674.mp4\",\n"
                + "            \"thumb\": \"https://a1.easemob.com/1188170313178478/youhaodongxi/chatfiles/a2acdf20-60a8-11e7-b726-ab0d6e300107\",\n"
                + "            \"type\": \"video\",\n"
                + "            \"url\": \"https://a1.easemob.com/1188170313178478/youhaodongxi/chatfiles/a2eb95d0-60a8-11e7-b6a5-073d17c5638d\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"timestamp\": \"1499166307402\",\n"
                + "    \"to\": \"19369592684545\",\n"
                + "    \"chat_type\": \"GroupChat\"\n"
                + "},\n"
                + "{\n"
                + "    \"chat_type\": \"GroupChat\",\n"
                + "    \"direction\": \"SEND\",\n"
                + "    \"from\": \"lz3\",\n"
                + "    \"msg_id\": \"350476658324539380\",\n"
                + "    \"payload\": {\n"
                + "        \"bodies\": [{\n"
                + "            \"file_length\": \"1.94KB\",\n"
                + "            \"filename\": \"yhdx-4620170704T190822.amr\",\n"
                + "            \"length\": \"2\",\n"
                + "            \"secret\": \"GfcMmmCpEeepRAuL5W3NLlab-3kBku0jcUNleNjnuNxbSoB0\",\n"
                + "            \"type\": \"audio\",\n"
                + "            \"url\": \"https://a1.easemob.com/1188170313178478/youhaodongxi/chatfiles/19f70c90-60a9-11e7-9927-df857dc80880\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"timestamp\": \"1499166505987\",\n"
                + "    \"to\": \"19369592684545\"\n"
                + "},\n"
                + "{\n"
                + "    \"chat_type\": \"GroupChat\",\n"
                + "    \"direction\": \"SEND\",\n"
                + "    \"from\": \"lz4\",\n"
                + "    \"msg_id\": \"350476682890577908\",\n"
                + "    \"payload\": {\n"
                + "        \"bodies\": [{\n"
                + "            \"filename\": \"image-1849353007.jpg\",\n"
                + "            \"secret\": \"HVLhymCpEeeBP8fLp1zGL-bWFjrve4Hclkni9XVDriCrrB2w\",\n"
                + "            \"size\": {\n"
                + "                \"height\": 1263,\n"
                + "                \"width\": 840\n"
                + "            },\n"
                + "            \"type\": \"img\",\n"
                + "            \"url\": \"https://a1.easemob.com/1188170313178478/youhaodongxi/chatfiles/1d52e1c0-60a9-11e7-94a9-071c28363fe3\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"timestamp\": \"1499166511701\",\n"
                + "    \"to\": \"19369592684545\"\n"
                + "},\n"
                + "{\n"
                + "    \"chat_type\": \"GroupChat\",\n"
                + "    \"direction\": \"SEND\",\n"
                + "    \"from\": \"lz5\",\n"
                + "    \"msg_id\": \"350461429712685056\",\n"
                + "    \"payload\": {\n"
                + "        \"bodies\": [{\n"
                + "            \"filename\": \"image269235292.jpg\",\n"
                + "            \"secret\": \"2GwAimCgEeepbS3Xzirr2IlxF_VoO4i_dByB0XMIIDx-UNqG\",\n"
                + "            \"size\": {\n"
                + "                \"height\": 360,\n"
                + "                \"width\": 640\n"
                + "            },\n"
                + "            \"type\": \"img\",\n"
                + "            \"url\": \"https://a1.easemob.com/1188170313178478/youhaodongxi/chatfiles/d86c0080-60a0-11e7-ade1-6dfb6ad635c4\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"timestamp\": \"1499162960283\",\n"
                + "    \"to\": \"19369592684545\"\n"
                + "},\n"
                + "{\n"
                + "    \"from\": \"lz6\",\n"
                + "    \"msg_id\": \"350445529139775524\",\n"
                + "    \"payload\": {\n"
                + "        \"ext\": {},\n"
                + "        \"bodies\": [{\n"
                + "            \"msg\": \"忒MSN\",\n"
                + "            \"type\": \"txt\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"timestamp\": \"1499159258091\",\n"
                + "    \"to\": \"19369592684545\",\n"
                + "    \"chat_type\": \"GroupChat\"\n"
                + "},\n"
                + "{\n"
                + "    \"from\": \"lz7\",\n"
                + "    \"msg_id\": \"350445455370360868\",\n"
                + "    \"payload\": {\n"
                + "        \"ext\": {},\n"
                + "        \"bodies\": [{\n"
                + "            \"msg\": \"测试\",\n"
                + "            \"type\": \"txt\"\n"
                + "        }]\n"
                + "    },\n"
                + "    \"timestamp\": \"1499159240911\",\n"
                + "    \"to\": \"19369592684545\",\n"
                + "    \"chat_type\": \"GroupChat\"\n"
                + "}]";
        List<EMMessage> messageList = new ArrayList<EMMessage>();
        try {
            JSONArray jsonArray = new JSONArray(msgJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                long timestamp = jsonObject.optLong("timestamp");
                String from = jsonObject.optString("from");
                String to = jsonObject.optString("to");
                String msgId = jsonObject.optString("msg_id");

                JSONObject bodyObject = jsonObject.optJSONObject("payload").optJSONArray("bodies").getJSONObject(0);
                String type = bodyObject.optString("type");
                EMMessage message = null;
                if (type.equals("txt")) {
                    message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    EMTextMessageBody body = new EMTextMessageBody(bodyObject.optString("msg"));
                    message.addBody(body);
                } else if (type.equals("video")) {
                    message = EMMessage.createReceiveMessage(EMMessage.Type.VIDEO);
                    EMVideoMessageBody body = new EMVideoMessageBody();
                    body.setThumbnailUrl(bodyObject.optString("thumb"));
                    body.setThumbnailSecret(bodyObject.optString("thumb_secret"));
                    body.setRemoteUrl(bodyObject.optString("url"));
                    body.setVideoFileLength(bodyObject.optLong("file_length"));
                    body.setSecret(bodyObject.optString("secret"));
                    message.addBody(body);
                } else if (type.equals("audio")) {
                    message = EMMessage.createReceiveMessage(EMMessage.Type.VOICE);
                    File file = new File("");
                    EMVoiceMessageBody body = new EMVoiceMessageBody(file, bodyObject.optInt("length"));
                    body.setRemoteUrl(bodyObject.optString("url"));
                    body.setSecret(bodyObject.optString("secret"));
                    body.setFileName(bodyObject.optString("filename"));
                    message.addBody(body);
                } else if (type.equals("img")) {
                    message = EMMessage.createReceiveMessage(EMMessage.Type.IMAGE);
                    File file = new File("");
                    // 这里使用反射获取 ImageBody，为了设置 size
                    Class<?> bodyClass = Class.forName("com.hyphenate.chat.EMImageMessageBody");
                    Class<?>[] parTypes = new Class<?>[1];
                    parTypes[0] = File.class;
                    Constructor<?> constructor = bodyClass.getDeclaredConstructor(parTypes);
                    Object[] pars = new Object[1];
                    pars[0] = file;
                    EMImageMessageBody body = (EMImageMessageBody) constructor.newInstance(pars);
                    Method setSize = Class.forName("com.hyphenate.chat.EMImageMessageBody")
                            .getDeclaredMethod("setSize", int.class, int.class);
                    setSize.setAccessible(true);
                    int width = bodyObject.optJSONObject("size").optInt("width");
                    int height = bodyObject.optJSONObject("size").optInt("height");
                    setSize.invoke(body, width, height);

                    body.setFileName(bodyObject.optString("filename"));
                    body.setSecret(bodyObject.optString("secret"));
                    body.setRemoteUrl(bodyObject.optString("url"));
                    body.setThumbnailUrl(bodyObject.optString("thumb"));
                    message.addBody(body);
                }
                message.setFrom(from);
                message.setTo(to);
                message.setMsgTime(timestamp);
                message.setMsgId(msgId);
                message.setChatType(EMMessage.ChatType.GroupChat);
                message.setStatus(EMMessage.Status.SUCCESS);
                messageList.add(message);
            }

            VMLog.d("conversation 1- count: %d", EMClient.getInstance().chatManager().getAllConversations().size());
            EMClient.getInstance().chatManager().importMessages(messageList);
            EMClient.getInstance().chatManager().loadAllConversations();
            VMLog.d("conversation 2- count: %d", EMClient.getInstance().chatManager().getAllConversations().size());

            EMMessage imgMsg = EMMessage.createReceiveMessage(EMMessage.Type.FILE);
            File file = new File("");
            EMImageMessageBody body = new EMImageMessageBody(file);
            imgMsg.addBody(body);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录
     */
    private void signOut() {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.hint_sign_out));
        progressDialog.show();

        IMHelper.getInstance().signOut(new EMCallBack() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
//                mContext.runOnUiThread(new Runnable() {
//                    @Override public void run() {
//                        mContext.onStartActivity(mContext, new Intent(mContext, SignInActivity.class));
//                        mContext.finishAfterTransition();
//                    }
//                });
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
