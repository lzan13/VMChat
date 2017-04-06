package com.vmloft.develop.app.chat.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import com.vmloft.develop.app.chat.app.AppActivity;
import com.vmloft.develop.app.chat.app.Constants;
import com.vmloft.develop.library.tools.utils.VMFileUtil;
import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.library.tools.utils.VMLog;

/**
 * Created by lzan13 on 2016/4/1.
 * 显示大图界面，
 * TODO 保存，分享图片，缩放查看等
 */
public class BigImageActivity extends AppActivity {

    // 显示原图控件
    private PhotoView mPhotoView;

    private EMMessage mMessage;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        initView();
        initToolbar();
    }

    /**
     * 初始化 Toolbar 控件
     */
    private void initToolbar() {
        getToolbar().setTitle(((EMImageMessageBody) mMessage.getBody()).getFileName());
        setSupportActionBar(getToolbar());
        // 设置toolbar图标
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        // 设置Toolbar图标点击事件，Toolbar上图标的id是 -1
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onFinish();
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        activity = this;

        mPhotoView = (PhotoView) findViewById(R.id.img_image);
        // 启动图片缩放功能
        mPhotoView.enable();

        String msgId = getIntent().getStringExtra(Constants.EXTRA_MSG_ID);
        mMessage = EMClient.getInstance().chatManager().getMessage(msgId);

        // 图片本地路径
        String localPath = ((EMImageMessageBody) mMessage.getBody()).getLocalUrl();
        String remotePath = ((EMImageMessageBody) mMessage.getBody()).getRemoteUrl();
        // 根据图片存在情况加载缩略图显示
        if (VMFileUtil.isFileExists(localPath)) {
            VMLog.i("show big image");
            // 原图存在，直接通过原图路径加载显示
            Glide.with(activity)
                    .load(localPath)
                    .crossFade()
                    .dontAnimate()
                    .placeholder(R.drawable.image_default)
                    .into(mPhotoView);
        } else {
            // 原图不存在
            Glide.with(activity)
                    .load(remotePath)
                    .crossFade()
                    .dontAnimate()
                    .placeholder(R.drawable.image_default)
                    .into(mPhotoView);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }
}
