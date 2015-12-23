package net.melove.demo.chat.test;

import android.content.Context;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

import net.melove.demo.chat.util.MLLog;

/**
 * Created by lzan13 on 2015/12/22 12:23.
 * <p/>
 * 单例类，给用户用来测试sdk的环境是否正常等
 * 使用方法：
 * 可以在任何类里获取此类的单例，然后调用相应的方法（前提是已经进行了sdk的初始化）
 * <p/>
 * <p/>
 * 包含测试：
 * 1、注册 {@link }
 * 2、登录
 * 3、正常消息监听
 * 4、透传的收发
 * 5、好友监听
 */
public class MLTestHelper {

    private final String TAG = "em_test";

    private static MLTestHelper instance;

    private MLTestHelper() {

    }


    /**
     * 获取单例对象，用来调用此类的一些方法
     *
     * @return 返回当前类的实例
     */
    public static MLTestHelper getInstance() {
        if (instance == null) {
            instance = new MLTestHelper();
        }
        return instance;
    }

    /**
     * 初始化环信的sdk（多次调用会出现问题）
     * 这个方法只是在给用户没有调用初始化的时候调用，如果自己已经初始化过环信的sdk，请不要调用此方法
     */
    public void initEMSDK(Context context) {
        // 这里默认设置成环信官方demo 的appkey
        EMChat.getInstance().setAppkey("easemob-demo#chatdemoui");
        // 调用环信的sdk初始化方法
        EMChat.getInstance().init(context);
        // 打开环信SDK 的debug模式，方便查看logcat 输出日志信息
        EMChat.getInstance().setDebugMode(true);

    }

    /**
     * 测试注册功能是否正常
     */
    public void signup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 开始输出注册相关 log
                logStart();
                // 使用当前获得的毫秒值作为用户名注册
//                String username = String.valueOf(System.currentTimeMillis());
                String username = "1450764179712";
                String password = "123123";
                try {
                    logE("username %s, password %s", username, password);
                    // 调用环信的注册方法，这个通过捕捉异常的方式去判断注册失败时的问题
                    EMChatManager.getInstance().createAccountOnServer(username, password);
                    logE("注册成功！");
                } catch (EaseMobException e) {
                    int code = e.getErrorCode();
                    String error = e.getMessage();
                    if (code == EMError.NONETWORK_ERROR) {
                        logE("网络异常，请检查网络设置，原因：code %d，error %s", code, error);
                    } else if (code == EMError.USER_ALREADY_EXISTS) {
                        logE("账户已经存在，原因：code %d，error %s", code, error);
                    } else if (code == EMError.UNAUTHORIZED) {
                        logE("无权注册，检查后台设置是否为授权注册，原因：code %d，error %s", code, error);
                    } else if (code == EMError.ILLEGAL_USER_NAME) {
                        logE("非法的用户名，原因：code %d，error %s", code, error);
                    } else {
                        logE("注册失败，原因：code %d，error %s", code, error);
                    }
                }
                // 注册相关 log 输出结束
                logEnd();
            }
        }).start();
    }


    /**
     * 测试登陆功能
     * 这里输入两个参数，可以让注册的方法完成后也直接调用
     *
     * @param u 登录的 username
     * @param p 登录的 password
     */
    public void signin(String u, String p) {
        // 测试登录用的username和password
        String username = u;
        String password = p;
        // 开始打印登录相关 log
        logStart();
        // 调用环信的注册方法，需要设置回调函数，可以在回调中判断登录是否成功
        EMChatManager.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                logE("登录成功！");
            }

            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case EMError.INVALID_PASSWORD_USERNAME:
                        logE("用户名或密码无效，原因：code %d，error %s", i, s);
                        break;
                    case EMError.UNABLE_CONNECT_TO_SERVER:
                        logE("不能链接到服务器，原因：code %d，error %s", i, s);
                        break;
                    case EMError.DNS_ERROR:
                        logE("DNS错误，检查本地代理设置，原因：code %d，error %s", i, s);
                        break;
                    case EMError.CONNECT_TIMER_OUT:
                        logE("连接超时，请检查网络情况，原因：code %d，error %s", i, s);
                        break;
                    default:
                        logE("登录错误，原因：code %d，error %s", i, s);
                        break;
                }
                logEnd();
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 发送消息
     *
     * @param to      接收人 demo 的 appkey 下有个 robotOne 的机器人，如果使用Demo 的 appkey，
     *                可以给 robotOne 发送消息，他会自动回复，以此来测试是否发送成功
     * @param content 内容
     */
    public void sendMessage(String to, String content) {
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        TextMessageBody body = new TextMessageBody(content);
        message.addBody(body);
        message.setReceipt(to);
        MLLog.i(message.getMsgId());
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                logE("消息发送成功 msgid %s", message.getMsgId());
            }

            @Override
            public void onError(int i, String s) {
                logE("消息发送失败 原因：code %d, error %s", i, s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });


    }


    /**
     * -----------------------------------------------------------------------------
     * 关于log输出的简单封装一下
     */
    private void logStart() {
        Log.e(TAG, "======================================= start ===============================");
    }

    private void logE(String msg) {
        Log.e(TAG, "|   " + msg);
    }

    private void logE(String msg, Object... args) {
        Log.e(TAG, "|   " + String.format(msg, args));
    }

    private void logEnd() {
        Log.e(TAG, "======================================== end ================================");
    }

}
