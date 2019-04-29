package com.vmloft.develop.app.chat.base;


import com.vmloft.develop.library.tools.base.VMApp;

/**
 * Created by lzan13 on 2015/8/10
 *
 * 项目入口，做一些初始化操作，
 */
public class App extends VMApp {

    @Override
    public void onCreate() {
        super.onCreate();

        // 调用自定义初始化IM方法
        initIM();
    }

    /**
     * 初始化环信 SDK
     */
    private void initIM() {
        IMHelper.getInstance().init(context);
    }

//    /**
//     * TalkingData 统计平台初始化
//     */
//    private void initTalkingData() {
//        TCAgent.LOG_ON = true;
//        String channel = "";
//        try {
//            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//            channel = ai.metaData.getString("TD_CHANNEL_ID", "dev");
//            VMLog.i("channel %s", channel);
//        } catch (NullPointerException e) {
//            VMLog.e("channel %s, %s", channel, e.getMessage());
//        } catch (PackageManager.NameNotFoundException e) {
//            VMLog.e("channel %s, %s", channel, e.getMessage());
//        }
//        // 初始化 TalkingData
//        TCAgent.init(this, AConstants.TD_APP_ID, channel);
//        // 是否启用 TalkingData 的错误报告
//        TCAgent.setReportUncaughtExceptions(true);
//    }
}
