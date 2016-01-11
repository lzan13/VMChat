package net.melove.demo.chat.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.test.MLTestHelper;
import net.melove.demo.chat.util.MLDate;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLToast;
import net.melove.demo.chat.widget.MLViewGroup;

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
        viewGroup = (MLViewGroup) getView().findViewById(R.id.ml_view_custom_viewgroup);

        getView().findViewById(R.id.ml_btn_test_signout).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_jump).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_toast).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_delete_conversation).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_import_message).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_signup).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_signin).setOnClickListener(viewListener);
        getView().findViewById(R.id.ml_btn_test_record).setOnClickListener(viewListener);

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

    private void importMessage() {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        TextMessageBody textMessageBody = new TextMessageBody("导入消息" + MLDate.getCurrentDate());
        message.addBody(textMessageBody);
        message.setFrom("lz0");
        EMChatManager.getInstance().importMessage(message, false);

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

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_test_signout:
                    signOut();
                    break;
                case R.id.ml_btn_test_jump:
                    mListener.onFragmentClick(0x20, 0x00, null);
                    break;
                case R.id.ml_btn_test_toast:
                    MLToast.makeToast(1, "Test Toast").show();
                    break;
                case R.id.ml_btn_test_delete_conversation:
                    EMChatManager.getInstance().deleteConversation("lz0");
                    break;
                case R.id.ml_btn_test_import_message:
                    importMessage();
                    break;
                case R.id.ml_btn_test_signup:
                    MLTestHelper.getInstance().signup();
                    break;
                case R.id.ml_btn_test_signin:
                    MLTestHelper.getInstance().signin("lz1", "123123");
                    break;
                case R.id.ml_btn_test_send_message:
                    MLTestHelper.getInstance().sendMessage("", "测试发送消息");
                    break;
                case R.id.ml_btn_test_record:

                    break;
            }
        }
    };
}
