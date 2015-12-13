package net.melove.demo.chat.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.easemob.chat.EMVideoCallHelper;

import net.melove.demo.chat.R;

public class MLVideoCallActivity extends AppCompatActivity {


    private SurfaceView mOneselfSurfaceView;
    private SurfaceHolder mOneselfSurfaceHolder;
    private static SurfaceView mFriendSurfaceView;
    private SurfaceHolder mFriendSurfaceHolder;

    private EMVideoCallHelper mVideoCallHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

    }

    private void initView() {
        mOneselfSurfaceView = (SurfaceView) findViewById(R.id.ml_surface_onself);
        mOneselfSurfaceHolder = mOneselfSurfaceView.getHolder();
        mFriendSurfaceView = (SurfaceView) findViewById(R.id.ml_surface_friend);
        mFriendSurfaceHolder = mFriendSurfaceView.getHolder();


        // 获取callHelper,cameraHelper
        mVideoCallHelper = EMVideoCallHelper.getInstance();
//        cameraHelper = new CameraHelper(callHelper, localSurfaceHolder);

        findViewById(R.id.ml_btn_start).setOnClickListener(viewListener);
        findViewById(R.id.ml_btn_change).setOnClickListener(viewListener);
        findViewById(R.id.ml_btn_close).setOnClickListener(viewListener);
    }

    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
