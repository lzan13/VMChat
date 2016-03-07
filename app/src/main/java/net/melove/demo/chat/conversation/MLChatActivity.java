package net.melove.demo.chat.conversation;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.base.MLBaseActivity;
import net.melove.demo.chat.application.MLConstants;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.notification.MLNotifier;
import net.melove.demo.chat.common.util.MLLog;
import net.melove.demo.chat.common.widget.MLToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/10/12 15:00.
 */
public class MLChatActivity extends MLBaseActivity implements EMMessageListener {
    private Toolbar mToolbar;

    // 消息监听器
    private EMMessageListener mMessageListener;
    // 当前聊天的 ID
    private String mChatId;
    // 当前会话对象
    private EMConversation mConversation;

    // ListView 用来显示消息
    private ListView mListView;
    private MLMessageAdapter mAdapter;

    // 聊天扩展菜单
    private LinearLayout mAttachMenuLayout;
    // 是否为阅后即焚
    private boolean isBurn;

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
    }

    private void init() {
        mActivity = this;
        mMessageListener = this;
        mChatId = getIntent().getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);

        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(mChatId, null, true);
        // 设置当前会话未读数为 0
        mConversation.markAllMessagesAsRead();
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

        // 设置扩展菜单点击监听
        mAttachMenuLayout = (LinearLayout) findViewById(R.id.ml_layout_attach_menu);
        mAttachMenuLayout.setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_photo).setOnClickListener(viewListener);
        findViewById(R.id.ml_img_attch_photo).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_video).setOnClickListener(viewListener);
        findViewById(R.id.ml_img_attach_video).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_file).setOnClickListener(viewListener);
        findViewById(R.id.ml_img_attach_file).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_location).setOnClickListener(viewListener);
        findViewById(R.id.ml_img_attach_location).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_gift).setOnClickListener(viewListener);
        findViewById(R.id.ml_img_attach_gift).setOnClickListener(viewListener);
        findViewById(R.id.ml_attach_contacts).setOnClickListener(viewListener);
        findViewById(R.id.ml_img_attach_contacts).setOnClickListener(viewListener);

    }


    /**
     * 初始化 ListView
     */
    private void initListView() {
        mListView = (ListView) findViewById(R.id.ml_listview_message);
        mAdapter = new MLMessageAdapter(mActivity, mChatId, mListView);
        mListView.setAdapter(mAdapter);
        setItemClickListener();
        setItemLongClickListener();
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
     * 设置ListView 的点击监听
     */
    private void setItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final EMMessage message = mConversation.getAllMessages().get(position);
                // 判断当前消息是否为【阅后即焚】类型
                if (message.getBooleanAttribute(MLConstants.ML_ATTR_TYPE, false)) {


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
                // 判断当前消息是否是发送方，并且是发送成功才能撤回
                if (message.direct() == EMMessage.Direct.SEND
                        && message.status() == EMMessage.Status.SUCCESS) {
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
                                        // 复制消息到剪切板
                                        copyMessage(message);
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
                                        // 撤回消息
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
     * 复制消息到剪切板
     *
     * @param message
     */
    private void copyMessage(EMMessage message) {
        //        MLToast.makeToast(R.string.ml_menu_chat_copy).show();
        // 获取剪切板管理者
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建剪切板数据对象
        ClipData clipData = ClipData.newPlainText("message", ((EMTextMessageBody) message.getBody()).getMessage());
        // 将刚创建的数据对象添加到剪切板
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 撤回消息
     *
     * @param message 需要撤回的消息
     */
    private void recallMessage(EMMessage message) {
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

    /**
     * 最终调用发送信息方法
     *
     * @param message
     */
    private void sendMessage(final EMMessage message) {
        setMessageAttribute(message);
        refreshChatUI();
        // 设置消息状态回调
        message.setMessageStatusCallback(new EMCallBack() {
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
                        if (message.getError() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                            MLToast.errorToast("发送内容包含敏感词汇").show();
                        } else if (message.getError() == EMError.GROUP_PERMISSION_DENIED) {
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
        // 调用sdk的消息发送方法，发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
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
        EMTextMessageBody txtBody = new EMTextMessageBody(content);
        // 设置消息body
        message.addBody(txtBody);
        // 设置消息接收者
        message.setReceipt(mChatId);
        sendMessage(message);
    }

    /**
     * 发送阅后即焚类型的消息
     */
    private void setMessageAttribute(EMMessage message) {
        if (isBurn) {
            // 设置消息扩展类型为阅后即焚
            message.setAttribute(MLConstants.ML_ATTR_BURN, true);
        }
    }

    /**
     * 发送图片消息
     *
     * @param path 要发送的图片的路径
     */
    private void sendImageMessage(String path) {
        // 根据图片路径创建一条图片消息
    }


    /**
     * 处理Activity的返回值得方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MLLog.d("onActivityResult requestCode %d, resultCode %d", requestCode, resultCode);
        switch (requestCode) {
            case MLConstants.ML_REQUEST_CODE_PHOTO:
                if (data != null) {
                    Uri imageUri = data.getData();
                    String imagePath = imageUri.getPath();
                    String imageAuthority = imageUri.getAuthority();
                    MLLog.d("imageUri %s", imageUri);
                    MLLog.d("imagePath %s", imagePath);
                    MLLog.d("imageAuthority %s", imageAuthority);
                    sendImageMessage(imagePath);
                }
                break;
            case MLConstants.ML_REQUEST_CODE_VIDEO:
                break;
            case MLConstants.ML_REQUEST_CODE_FILE:
                if (data != null) {
                    Uri fileUri = data.getData();
                    String filePath = fileUri.getPath();
                    String fileAuthority = fileUri.getAuthority();
                    MLLog.d("fileUri %s", fileUri);
                    MLLog.d("filePath %s", filePath);
                    MLLog.d("fileAuthority %s", fileAuthority);
                }
                break;
            case MLConstants.ML_REQUEST_CODE_LOCATION:
                break;
            case MLConstants.ML_REQUEST_CODE_GIFT:
                break;
            case MLConstants.ML_REQUEST_CODE_CONTACTS:

                break;
            default:
                break;
        }
    }


    /**
     * 聊天界面按钮监听事件
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 表情按钮
                case R.id.ml_img_chat_emotion:

                    break;
                // 发送按钮
                case R.id.ml_img_chat_send:
                    sendTextMessage();
                    break;
                // 语音按钮
                case R.id.ml_img_chat_voice:
                    MLToast.makeToast(R.string.ml_voice).show();
                    break;
                // 附件菜单
                case R.id.ml_attach_photo:
                case R.id.ml_img_attch_photo:
                    // 选择图片
                    openSelectPhoto();
                    break;
                case R.id.ml_attach_video:
                case R.id.ml_img_attach_video:
                    // 选择视频文件

                    break;
                case R.id.ml_attach_file:
                case R.id.ml_img_attach_file:
                    // 选择文件
                    openSelectFile();
                    break;
                case R.id.ml_attach_location:
                case R.id.ml_img_attach_location:
                    // 选择位置

                    break;
                case R.id.ml_attach_gift:
                case R.id.ml_img_attach_gift:
                    // 选择选择礼物

                    break;
                case R.id.ml_attach_contacts:
                case R.id.ml_img_attach_contacts:
                    // 选择联系人

                    break;
                // 附件菜单背景，用来关闭菜单
                case R.id.ml_layout_attach_menu:
                    onAttachMenu();
                    break;
            }
        }
    };

    /**
     * 打开系统文件选择器，去选择文件
     */
    private void openSelectFile() {
        // 设置intent属性，跳转到系统文件选择界面
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // 设置intent要选择的文件类型，这里用 * 表示选择全部类型
        intent.setType("*/*");
        startActivityForResult(intent, MLConstants.ML_REQUEST_CODE_FILE);
    }

    /**
     * 打开系统图片选择器，去进行选择图片
     */
    private void openSelectPhoto() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // 设置intent要选择的文件类型，这里用设置为image 图片类型
            intent.setType("image/*");
        } else {
            // 在Android 系统版本大于19 上，调用系统选择图片方法稍有不同
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        mActivity.startActivityForResult(intent, MLConstants.ML_REQUEST_CODE_PHOTO);
    }

    private void refreshChatUI() {
        mAdapter.refreshList();
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
                // 打开或关闭附件菜单
                onAttachMenu();
                break;
            case R.id.ml_action_delete:
                // 清空会话信息
//                MLToast.makeToast("清空会话").show();
                // 此方法只清除内存中加载的消息，并没有清除数据库中保存的消息
//                 mConversation.clear();
                // 清除全部信息，包括数据库中的
                mConversation.clearAllMessages();
                refreshChatUI();
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
    protected void onNewIntent(Intent intent) {
        String chatId = intent.getStringExtra(MLConstants.ML_EXTRA_CHAT_ID);
        if (mChatId.equals(chatId)) {
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册环信的消息监听器
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        refreshChatUI();
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
        // 再当前界面处于非活动状态时 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }
    /**
     * ----------------------------------------------------------------------
     * 环信消息监听主要方法
     */
    /**
     * 收到新消息
     *
     * @param list 收到的新消息集合
     */
    @Override
    public void onMessageReceived(List<EMMessage> list) {
        // 循环遍历当前收到的消息
        for (EMMessage message : list) {
            if (mChatId.equals(message.getFrom())) {
                refreshChatUI();
            } else {
                MLNotifier.getInstance(mActivity).sendMessageNotification(message);
            }
        }
    }

    /**
     * 收到新的 CMD 消息
     *
     * @param list
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            // 透传消息
            EMMessage cmdMessage = list.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
            // 判断是不是撤回消息的透传
            if (body.action().equals(MLConstants.ML_ATTR_RECALL)) {
                MLEasemobHelper.getInstance().receiveRecallMessage(cmdMessage);
                refreshChatUI();
            }
        }
    }

    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
    @Override
    public void onMessageReadAckReceived(List<EMMessage> list) {
        refreshChatUI();
    }

    /**
     * 收到新的发送回执
     *
     * @param list 收到发送回执的消息集合
     */
    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list) {
        refreshChatUI();
    }

    /**
     * 消息的状态改变
     *
     * @param message 发生改变的消息
     * @param object  包含改变的消息
     */
    @Override
    public void onMessageChanged(EMMessage message, Object object) {
        refreshChatUI();
    }
}
