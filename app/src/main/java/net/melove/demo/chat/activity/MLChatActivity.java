package net.melove.demo.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import net.melove.demo.chat.R;

/**
 * Class ${FILE_NAME}
 * <p/>
 * Created by lzan13 on 2015/10/12 15:00.
 */
public class MLChatActivity extends MLBaseActivity {

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

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ml_btn_chat_emotion:

                    break;
                case R.id.ml_btn_chat_send:

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
