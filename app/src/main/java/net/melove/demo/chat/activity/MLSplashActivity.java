package net.melove.demo.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.hyphenate.chat.EMClient;

import net.melove.demo.chat.R;
import net.melove.demo.chat.application.MLEasemobHelper;
import net.melove.demo.chat.test.MLEMCheck;
import net.melove.demo.chat.widget.MLToast;

/**
 * Created by lz on 2015/12/7.
 */
public class MLSplashActivity extends MLBaseActivity {

    // 开屏页持续时间
    private int mTime = 3000;
    // 动画持续时间
    private int mDurationTime = 1500;

    // 开屏页显示的图片控件
    private View mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 调用初始化sdk的一些检查
        MLEMCheck.getInstance().init(this);

        initView();

    }

    /**
     * 初始化当前界面的控件
     */
    private void initView() {
        mImageView = findViewById(R.id.ml_img_splash);
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(mDurationTime);
        mImageView.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                if (MLEasemobHelper.getInstance().isLogined()) {
                    // 获取当前系统时间毫秒数
                    long start = System.currentTimeMillis();
                    // 加载群组到内存
                    EMClient.getInstance().groupManager().loadAllGroups();
                    // 加载所有本地会话到内存
                    EMClient.getInstance().chatManager().loadAllConversations();
                    // 获取加载回话使用的时间差 毫秒表示
                    long costTime = System.currentTimeMillis() - start;
                    if (mTime - costTime > 0) {
                        try {
                            Thread.sleep(mTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 进入主页面
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.sendToTarget();
                } else {
                    try {
                        // 初始化数据
//                        initData();
                        // 睡眠3000毫秒
                        Thread.sleep(mTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 跳转到登录界面
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.sendToTarget();
                }
            }
        }).start();
    }

    /**
     * 跳转场景
     */
    private void jumpScene(Intent intent) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(mActivity, R.anim.ml_anim_fade_in, R.anim.ml_anim_fade_out);
        ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
        mActivity.finish();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int w = msg.what;
            Intent intent = new Intent();
            switch (w) {
                case 0:
                    intent.setClass(mActivity, MLMainActivity.class);
                    jumpScene(intent);
                    break;
                case 1:
                    intent.setClass(mActivity, MLSigninActivity.class);
                    jumpScene(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            MLToast.makeToast("暂时不能返回哦~").show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
