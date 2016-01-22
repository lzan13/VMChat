package net.melove.demo.chat.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.adapter.MLMessageAdapter;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.notification.MLNotifier;
import net.melove.demo.chat.util.MLLog;
import net.melove.demo.chat.widget.MLToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    // ListView
    private ListView mListView;
    private MLMessageAdapter mAdapter;

    private LinearLayout mAttachMenuLayout;

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
        mChatId = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
    }

    /**
     * 初始化 Toolbar 控件
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.ml_widget_toolbar);
        mToolbar.setTitle(mChatId);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(viewListener);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        // 初始化输入框控件，并添加输入框监听
        mEditText = (EditText) findViewById(R.id.ml_edit_chat_input);
        mEditText.addTextChangedListener(textWatcher);

        // 获取控件对象
        mEmotionView = findViewById(R.id.ml_img_chat_emotion);
        mSendView = findViewById(R.id.ml_img_chat_send);
        mVoiceView = findViewById(R.id.ml_img_chat_voice);

        // 设置控件的点击监听
        mEmotionView.setOnClickListener(viewListener);
        mVoiceView.setOnClickListener(viewListener);
        mSendView.setOnClickListener(viewListener);
        mSendView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switch (v.getId()) {
                    case R.id.ml_img_chat_send:
                        sendDestroyMessage();
                        break;
                }
                return true;
            }
        });
        mAttachMenuLayout = (LinearLayout) findViewById(R.id.ml_layout_attach_menu);
        mAttachMenuLayout.setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_photo).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_video).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_file).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_location).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_gift).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_contacts).setOnClickListener(viewListener);

    }


    /**
     * 初始化 ListView
     */
    private void initListView() {
        mListView = (ListView) findViewById(R.id.ml_listview_message);
        mAdapter = new MLMessageAdapter(mActivity, mChatId, mListView);
        mListView.setAdapter(mAdapter);
        refreshChatUI();
        setItemClickListener();
        setItemLongClickListener();
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

    private TextWatcher textWatcher = new TextWatcher() {
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
    };

    /**
     * 设置ListView 的点击是监听
     */
    private void setItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final EMMessage message = mConversation.getAllMessages().get(position);
//                MLToast.makeToast("item " + position + ", id " + id + ", msgId " + message.getMsgId()).show();
                // 判断当前消息是否为【阅后即焚】类型
                if (message.getStringAttribute(MLConstants.ML_ATTR_TYPE, "null").equals(MLConstants.ML_ATTR_TYPE_DESTROY)) {
                    final AlertDialog dialog = new AlertDialog.Builder(mActivity).create();
//                    dialog.setTitle("阅后即焚内容");
                    View tView = mActivity.getLayoutInflater().inflate(R.layout.widget_dialog_title_destroy, null);
                    TextView titleView = (TextView) tView.findViewById(R.id.ml_text_dialog_title);
                    final TextView timeView = (TextView) tView.findViewById(R.id.ml_text_dialog_title_time);
                    titleView.setText("消息内容");
                    timeView.setText("5");
                    dialog.setCustomTitle(tView);
                    dialog.setMessage(((TextMessageBody) message.getBody()).getMessage());
                    dialog.show();
                    // 创建定时器，进行销毁消息的计时
                    final Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        int time = 5;

                        @Override
                        public void run() {
                            if (time <= 0) {
                                message.setType(EMMessage.Type.TXT);
                                message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_ATTR_TYPE_DESTROYED);
                                TextMessageBody body = new TextMessageBody("消息已销毁");
                                message.addBody(body);
                                EMChatManager.getInstance().updateMessageBody(message);
                                refreshChatUI();
                                timer.cancel();
                                dialog.dismiss();
                            }
                            time--;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timeView.setText(String.valueOf(time));
                                }
                            });
                        }
                    };
                    timer.schedule(task, 1000, 1000);
                }
            }
        });
    }

    /**
     * ListView 列表项的长按监听
     */
    private void setItemLongClickListener() {
        // ListView 的长按监听
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final EMMessage message = mConversation.getAllMessages().get(position);
                // 这里要根据消息的类型去判断要弹出的菜单
                List<String> mMenuList = new ArrayList<String>();
                mMenuList.add(mActivity.getResources().getString(R.string.ml_menu_chat_copy));
                mMenuList.add(mActivity.getResources().getString(R.string.ml_menu_chat_delete));
                mMenuList.add(mActivity.getResources().getString(R.string.ml_menu_chat_forward));
                if (message.direct == EMMessage.Direct.SEND) {
                    mMenuList.add(mActivity.getResources().getString(R.string.ml_menu_chat_recall));
                }
                String[] mMenus = new String[mMenuList.size()];
                mMenuList.toArray(mMenus);
                // 创建并显示 ListView 的长按弹出菜单，并设置弹出菜单 Item的点击监听
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.ml_dialog_title_conversation)
                        .setItems(mMenus, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        MLToast.makeToast(R.string.ml_menu_chat_copy).show();
                                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText("message", ((TextMessageBody) message.getBody()).getMessage());
                                        clipboardManager.setPrimaryClip(clipData);
                                        break;
                                    case 1:
                                        MLToast.makeToast(R.string.ml_menu_chat_delete).show();
                                        mConversation.removeMessage(message.getMsgId());
                                        refreshChatUI();
                                        break;
                                    case 2:
                                        MLToast.makeToast(R.string.ml_menu_chat_forward).show();
                                        break;
                                    case 3:
                                        recallMessage(message);
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    /**
     * 撤回消息
     *
     * @param message 需要撤回的消息
     */
    private void recallMessage(EMMessage message) {
        String msgId = message.getMsgId();

//        if () {
//
//        }
        // 显示撤回消息操作的 dialog
        final ProgressDialog pd = new ProgressDialog(mActivity);
        pd.setMessage("正在撤回 请稍候……");
        pd.show();
        MLEasemobHelper.getInstance().sendRecallMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                pd.dismiss();
                refreshChatUI();
            }

            @Override
            public void onError(final int i, final String s) {
                pd.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (s.equals("time")) {
                            MLToast.errorToast("消息已经超过两分钟 无法撤回").show();
                        } else {
                            MLToast.errorToast("撤回失败 失败错误码（" + i + " " + s + ")").show();
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

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
        refreshChatUI();
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
//                        } else if (message.getError() == EMError.MESSAGE_SEND_TRAFFIC_LIMIT) {
//                            MLToast.errorToast("发送文件大小超过限制了").show();
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

    /**
     * 发送阅后即焚类型的消息
     */
    private void sendDestroyMessage() {
        String content = mEditText.getText().toString();
        mEditText.setText("");
        mSendView.setVisibility(View.GONE);
        mVoiceView.setVisibility(View.VISIBLE);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody txtBody = new TextMessageBody(content);
        // 设置消息body
        message.addBody(txtBody);
        message.setAttribute(MLConstants.ML_ATTR_TYPE, MLConstants.ML_ATTR_TYPE_DESTROY);
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
                case R.id.ml_attach_photo:

                    break;
                case R.id.ml_attach_video:

                    break;
                case R.id.ml_attach_file:

                    break;
                case R.id.ml_attach_location:

                    break;
                case R.id.ml_attach_gift:

                    break;
                case R.id.ml_attach_contacts:

                    break;
                case R.id.ml_layout_attach_menu:
                    onAttachMenu();
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
                    refreshChatUI();
                } else {
                    MLNotifier.getInstance(mActivity).sendMessageNotification(message);
                }
                break;
            case EventNewCMDMessage:
                // 透传消息
                EMMessage cmdMessage = (EMMessage) event.getData();
                CmdMessageBody body = (CmdMessageBody) cmdMessage.getBody();
                // 判断是不是撤回消息的透传
                if (body.action.equals(MLConstants.ML_ATTR_TYPE_RECALL)) {
//                    MLEasemobHelper.getInstance().receiveRecallMessage(cmdMessage);
                    refreshChatUI();
                }
                break;
        }
    }

    /**
     * 打开和关闭附件菜单
     */
    private void onAttachMenu() {
        // 判断当前附件菜单显示状态，显示就关闭，不显示就打开
        if (mAttachMenuLayout.isShown()) {
            mAttachMenuLayout.setVisibility(View.GONE);
        } else {
            mAttachMenuLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 重写菜单项的选择事件
     *
     * @param item 点击的是哪一个菜单项
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ml_action_call:
                MLToast.makeToast("还未实现语音通话功能").show();
                break;
            case R.id.ml_action_attachment:
//                MLToast.makeToast("附件").show();
                onAttachMenu();
                break;
            case R.id.ml_action_delete:
                MLToast.makeToast("清空会话").show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
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
