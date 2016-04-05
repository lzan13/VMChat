package net.melove.demo.chat.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.hyphenate.chat.EMClient;

import net.melove.demo.chat.R;
import net.melove.demo.chat.common.base.MLBaseActivity;

/**
 * Created by lzan13 on 2016/4/5.
 * 被踢界面，这里把Activity 做成一个背景透明的Dialog，实现在任何界面都可以弹出被踢提醒
 */
public class MLConflictActivity extends MLBaseActivity {

    private AlertDialog.Builder conflictDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict);

        mActivity = this;
        onConflictDialog();

    }

    /**
     * 弹出账户被其他设备登录的 Dialog
     */
    private void onConflictDialog() {
        conflictDialog = new AlertDialog.Builder(mActivity);
        conflictDialog.setTitle(R.string.ml_dialog_title_conflict);
        conflictDialog.setMessage(R.string.ml_dialog_message_conflict);
        conflictDialog.setNeutralButton(R.string.ml_signin_restart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 自定义操作按钮
                Intent intent = new Intent();
                intent.setClass(mActivity, MLMainActivity.class);
                mActivity.startActivity(intent);
                onFinish();
            }
        });
        conflictDialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消按钮
            }
        });
        conflictDialog.setPositiveButton(R.string.ml_btn_i_know, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确认按钮
            }
        });
        conflictDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
