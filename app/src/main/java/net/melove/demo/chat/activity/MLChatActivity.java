package net.melove.demo.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.util.MLLog;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/10/12 15:00.
 */
public class MLChatActivity extends MLBaseActivity {

    /**
     * 测试群ID:1447497414589
     */
    private String testGroupId = "1447497414589";
    private EMConversation mConversation;

    private Activity mActivity;

    private Toolbar mToolbar;

    private EditText mEditText;
    private RadioButton mEmotion;
    private View mSendView;
    private View mVoiceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        initToolbar();
        initView();
        initConversation();
    }

    private void init() {
        mActivity = this;
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);

        mToolbar.setTitle(R.string.ml_info_detailed);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.ml_white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.icon_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(viewListener);
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.ml_edit_chat_input);
        mEmotion = (RadioButton) findViewById(R.id.ml_btn_chat_emotion);
        mSendView = findViewById(R.id.ml_btn_chat_send);
        mVoiceView = findViewById(R.id.ml_btn_chat_voice);

        mEmotion.setOnClickListener(viewListener);
        mSendView.setOnClickListener(viewListener);
        mVoiceView.setOnClickListener(viewListener);
    }

    private void initConversation() {
        mConversation = EMChatManager.getInstance().getConversationByType(testGroupId, EMConversation.EMConversationType.Chat);


    }

    private void sendText(){
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        // 如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        TextMessageBody txtBody = new TextMessageBody("测试被删后往群里发送消息");
        // 设置消息body
        message.addBody(txtBody);
        // 设置要发给谁,用户username或者群聊groupid
        message.setReceipt(testGroupId);
        // 把messgage加到conversation中
        mConversation.addMessage(message);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                MLLog.i("消息发送成功 O(∩_∩)O~~");
            }

            @Override
            public void onError(int i, String s) {
                MLLog.i("消息发送失败 -_-||");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_chat_emotion:

                    break;
                case R.id.ml_btn_chat_send:
                    sendText();
                    break;
                case R.id.ml_btn_chat_voice:

                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
