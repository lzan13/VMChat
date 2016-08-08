package net.melove.app.chat.conversation.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;

import net.melove.app.chat.application.MLConstants;
import net.melove.app.chat.application.MLEasemobHelper;


/**
 * 通话呼叫监听广播实现，用来监听其他账户对自己的呼叫
 */
public class MLCallReceiver extends BroadcastReceiver {
    private String TYPE_VIDEO = "video";
    private String TYPE_VOICE = "voice";

    public MLCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断环信是否登录成功
        if (!MLEasemobHelper.getInstance().isLoginedInBefore()) {
            return;
        }

        // 呼叫方的usernmae
        String callFrom = intent.getStringExtra(MLConstants.ML_EXTRA_FROM);
        // 呼叫类型，有语音和视频两种
        String callType = intent.getStringExtra(MLConstants.ML_EXTRA_TYPE);
        // 呼叫接收方
        String callTo = intent.getStringExtra(MLConstants.ML_EXTRA_TO);

        // 判断下当前被呼叫的为自己的时候才启动通话界面 TODO 这个当不同appkey下相同的username时就无效了
        if (callTo.equals(EMClient.getInstance().getCurrentUser())) {
            // 创建界面跳转 intent
            Intent callIntent = new Intent();
            // 根据通话类型跳转到语音通话或视频通话界面
            if (callType.equals(TYPE_VIDEO)) {
                callIntent.setClass(context, MLVideoCallActivity.class);
            } else if (callType.equals(TYPE_VOICE)) {
                callIntent.setClass(context, MLVoiceCallActivity.class);
            }
            // 设置 activity 启动方式
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 设置呼叫方 username 参数
            callIntent.putExtra(MLConstants.ML_EXTRA_CHAT_ID, callFrom);
            // 设置通话为对方打来
            callIntent.putExtra(MLConstants.ML_EXTRA_IS_INCOMING_CALL, true);
            // 根据 intent 跳转到相应的界面
            context.startActivity(callIntent);
        }
    }
}
