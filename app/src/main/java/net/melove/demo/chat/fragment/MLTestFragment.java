package net.melove.demo.chat.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.common.base.MLBaseFragment;
import net.melove.demo.chat.common.util.MLDate;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.widget.MLViewGroup;

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
        String[] btns = {"登出", "导入消息", "更新消息", "TestAdapter"};
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
        EMClient.getInstance().chatManager().saveMessage(message);

    }

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

                    break;
            }
        }
    };
}
