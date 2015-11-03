package net.melove.demo.chat.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.R;

public class MLTestActivity extends AppCompatActivity {

    private TextView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
    }

    private void initView(){
        mContentView = (TextView) findViewById(R.id.ml_text_test_content);

    }

    /**
     * 初始化广播接受
     */
    private void initBroadcast() {
        MLNewMessageBroadcastReceiver msgReceiver = new MLNewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
    }

    /**
     * 实现一个新消息接收广播
     */
    private class MLNewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();
            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            // 获取发送方的username
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            TextMessageBody body = (TextMessageBody) message.getBody();
            mContentView.setText(body.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcast();
    }
}
