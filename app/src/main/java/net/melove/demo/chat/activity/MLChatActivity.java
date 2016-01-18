package net.melove.demo.chat.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLMessageAdapter;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLToast;

import java.util.List;

/**
 * Class ${FILE_NAME}
 * <p>
 * Created by lzan13 on 2015/10/12 15:00.
 */
public class MLChatActivity extends MLBaseActivity implements EMEventListener {
    private Toolbar mToolbar;

    // 当前聊天的 ID
    private String mChatId;
    // 当前会话对象
    private EMConversation mConversation;

    // ListView 长按弹出菜单项
    private String[] mMenus;
    private ListView mListView;
    private MLMessageAdapter mAdapter;

    // 聊天内容输入框
    private EditText mEditText;
    // 表情按钮
    private View mEmotionView;
    // 发送按钮
    private View mSendView;
    // 语音按钮
    private View mVoiceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        initToolbar();
        initView();
        initListView();
        initConversation();
    }

    private void init() {
        mActivity = this;
        mChatId = getIntent().getStringExtra(MLConstants.ML_C_CHAT_ID);
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        mEditText = (EditText) findViewById(R.id.ml_edit_chat_input);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            /**
             * 检测输入框内容变化
             *
             * @param s 输入框内容
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    mSendView.setVisibility(View.GONE);
                    mVoiceView.setVisibility(View.VISIBLE);
                } else {
                    mSendView.setVisibility(View.VISIBLE);
                    mVoiceView.setVisibility(View.GONE);
                }
            }
        });

        // 获取控件对象
        mEmotionView = findViewById(R.id.ml_img_chat_emotion);
        mSendView = findViewById(R.id.ml_img_chat_send);
        mVoiceView = findViewById(R.id.ml_img_chat_voice);

        // 设置控件的点击监听
        mEmotionView.setOnClickListener(viewListener);
        mSendView.setOnClickListener(viewListener);
        mVoiceView.setOnClickListener(viewListener);
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.ml_listview_message);
        mAdapter = new MLMessageAdapter(mActivity, mChatId, mListView);
        mListView.setAdapter(mAdapter);
        refreshChatUI();
        setItemClickListener();

        setItemLongClickListener();
    }

    /**
     * 初始化 Toolbar 控件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(mChatId);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_arrow);
        mToolbar.setNavigationOnClickListener(viewListener);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }


    /**
     * 初始化会话对象
     */
    private void initConversation() {
        mConversation = EMChatManager.getInstance().getConversationByType(mChatId, EMConversation.EMConversationType.Chat);
        mConversation.markAllMessagesAsRead();
        List<EMMessage> messages = mConversation.getAllMessages();
        for (EMMessage message : messages) {
            String content = ((TextMessageBody) message.getBody()).getMessage();
            MLLog.i(content);
        }
    }

    /**
     * 设置ListView 的点击是监听
     */
    private void setItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMMessage message = mConversation.getAllMessages().get(position);
                MLToast.makeToast("item " + position + ", id " + id + ", msgId " + message.getMsgId()).show();
            }
        });
    }

    /**
     * ListView 列表项的长按监听
     */
    private void setItemLongClickListener() {
        mMenus = new String[]{
                mActivity.getResources().getString(R.string.ml_menu_chat_copy),
                mActivity.getResources().getString(R.string.ml_menu_chat_delete),
                mActivity.getResources().getString(R.string.ml_menu_chat_forward),
                mActivity.getResources().getString(R.string.ml_menu_chat_recall),
        };
        // ListView 的长按监听
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final EMMessage message = mConversation.getAllMessages().get(position);
//                MLToast.makeToast("menu " + position + ", id " + id + ", msgId ").show();
                // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.ml_dialog_title_conversation)
                        .setItems(mMenus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        MLToast.makeToast(R.string.ml_menu_chat_copy).show();
                                        break;
                                    case 1:
                                        MLToast.makeToast(R.string.ml_menu_chat_delete).show();
                                        break;
                                    case 2:
                                        MLToast.makeToast(R.string.ml_menu_chat_forward).show();
                                        break;
                                    case 3:
                                        MLToast.makeToast(R.string.ml_menu_chat_recall).show();
                                        MLEasemobHelper.getInstance().sendRecallMessage(message, new EMCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                refreshChatUI();
                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }

                                            @Override
                                            public void onProgress(int i, String s) {

                                            }
                                        });
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void sendImageMessage() {

    }

    /**
     * 最终调用发送信息方法
     *
     * @param message
     */
    private void sendMessage(final EMMessage message) {
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                MLLog.i("消息发送成功 %s", message.getMsgId());
                refreshChatUI();
            }

            @Override
            public void onError(int i, String s) {
                MLLog.i("消息发送失败 code: %d, error: %s", i, s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
                            MLToast.errorToast("发送内容包含敏感词汇").show();
                        } else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {
                            MLToast.errorToast("不属于当前群组，不能给群组发送消息").show();
                        } else if (message.getError() == EMError.MESSAGE_SEND_TRAFFIC_LIMIT) {
                            MLToast.errorToast("发送文件大小超过限制了").show();
                        } else {
                            MLToast.errorToast("发送失败，稍后重试").show();
                        }
                        refreshChatUI();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage() {
        String content = mEditText.getText().toString();
        mEditText.setText("");

        mSendView.setVisibility(View.GONE);
        mVoiceView.setVisibility(View.VISIBLE);
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody txtBody = new TextMessageBody(content);
        // 设置消息body
        message.addBody(txtBody);

        message.setReceipt(mChatId);
        // 把messgage加到conversation中
        mConversation.addMessage(message);
        sendMessage(message);
    }

    private void refreshChatUI() {
//        mAdapter.refresh();
//        mAdapter.notifyDataSetChanged();
        mAdapter.refreshList();
    }

    /**
     * 聊天界面按钮监听事件
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_img_chat_emotion:

                    break;
                case R.id.ml_img_chat_send:
                    sendTextMessage();
                    break;
                case R.id.ml_img_chat_voice:

                    break;
            }
        }
    };

    /**
     * 环信的监听回调
     *
     * @param event
     */
    @Override
    public void onEvent(EMNotifierEvent event) {

        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                MLLog.i(((TextMessageBody) message.getBody()).getMessage());
                if (mChatId.equals(message.getFrom())) {

                } else {

                }
                refreshChatUI();
                break;
            case EventNewCMDMessage:

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册环信的消息监听器
        EMNotifierEvent.Event[] events = {
                EMNotifierEvent.Event.EventNewMessage,
                EMNotifierEvent.Event.EventNewCMDMessage,
                EMNotifierEvent.Event.EventDeliveryAck,
                EMNotifierEvent.Event.EventReadAck,
                EMNotifierEvent.Event.EventMessageChanged
        };
        EMChatManager.getInstance().registerEventListener(this, events);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
        mToolbar = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMChatManager.getInstance().unregisterEventListener(this);
    }

}
