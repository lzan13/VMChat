package net.melove.demo.chat.test;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.communal.base.MLBaseFragment;
import net.melove.demo.chat.communal.util.MLDate;
import net.melove.demo.chat.communal.util.MLLog;
import net.melove.demo.chat.communal.widget.MLViewGroup;

/**
 * 测试Fragment，
 * 继承自自定义的MLBaseFramgnet类，为了减少代码量，在MLBaseFrament类中定义接口回调
 * 包含此Fragment的活动窗口必须实现{@link MLBaseFragment.OnMLFragmentListener}接口,
 * 定义创建实例的工厂方法 {@link MLTestFragment#newInstance}，可使用此方法创建实例
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getParentFragment().getActivity();
        init();
    }

    private void init() {
        String[] btns = {"登出", "导入消息", "更新消息", "群消息", "创建群组", "AlertDialog", "ProgressDialog"};
        viewGroup = (MLViewGroup) getView().findViewById(R.id.ml_view_custom_viewgroup);
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
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case 100:
                signOut();
                break;
            case 101:
                importMessage();
                break;
            case 102:
                updateMessage();
                break;
            case 103:
                sendGroupMessage();
                break;
            case 104:
                testCreateGroup();
                break;
            case 105:
                testAlertDialog();
                break;
            case 106:
                testProgressDialog();
                break;
            }
        }
    };

    /**
     * 退出登录
     */
    private void signOut() {
        MLEasemobHelper.getInstance().signOut(new EMCallBack() {
            @Override
            public void onSuccess() {
                mListener.onFragmentClick(0x00, 0x01, null);
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 测试保存一条消息到本地
     */
    private void importMessage() {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        EMTextMessageBody textMessageBody = new EMTextMessageBody("导入消息" + MLDate.getCurrentDate());
        message.addBody(textMessageBody);
        message.setFrom("lz8");
        // 保存一条消息到本地，这个保存会直接加入到内存中
        EMClient.getInstance().chatManager().saveMessage(message);
        // 导入一个消息集合到本地，
//        EMClient.getInstance().chatManager().importMessages(list);

    }

    /**
     * 更新消息
     */
    private void updateMessage() {
        // 更改要撤销的消息的内容，替换为消息已经撤销的提示内容
        //        EMMessage recallMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //        EMTextMessageBody body = new EMTextMessageBody(String.format(context.getString(R.string.ml_hint_msg_recall_by_user), message.getUserName()));
        //        recallMessage.addBody(body);
        //        recallMessage.setReceipt(message.getFrom());
        //        // 设置新消息的 msgId为撤销消息的 msgId
        //        recallMessage.setMsgId(message.getMsgId());
        //        // 设置新消息的 msgTime 为撤销消息的 mstTime
        //        recallMessage.setMsgTime(message.getMsgTime());
        //        // 设置扩展为撤回消息类型，是为了区分消息的显示
        //        recallMessage.setAttribute(MLConstants.ML_ATTR_RECALL, true);
        //        // 返回修改消息结果
        //        result = EMClient.getInstance().chatManager().updateMessage(recallMessage);
    }

    /**
     * 测试给不存在的群发送消息
     */
    private void sendGroupMessage() {
        //创建一条文本消息,content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage("群消息" + MLDate.getCurrentMillisecond(), "1460022071257");
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                MLLog.i("message send success!");
            }

            @Override
            public void onError(int i, String s) {
                MLLog.i("message send error code:%d, error:%s", i, s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void testCreateGroup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 创建群组
                 * @param groupName 群组名称
                 * @param desc 群组简介
                 * @param allMembers 群组初始成员，如果只有自己传null即可
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
                String[] members = {"lz1", "lz2"};
                try {
                    EMGroup group = EMClient.getInstance().groupManager().createGroup("SDK测试群组2", "SDK端创建群组，测试默认属性", members, "这个群不错", option);
                    MLLog.i("group is members only - " + group.isMembersOnly());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 测试主题
     */
    private void testActivityTheme() {
        Intent intent = new Intent();
        intent.setClass(mActivity, MLTestActivity.class);
        mActivity.startActivity(intent);
    }

    private void testAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle("Test");
        dialog.setMessage("Test Dialog Message!");
        dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private void testProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("Test progress dialog");
        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnMLFragmentListener) context;
        } catch (ClassCastException e) {
            MLLog.e("必须实现Fragment的回调接口！");
            e.printStackTrace();
        }
    }
}
