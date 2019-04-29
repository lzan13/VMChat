package com.vmloft.develop.app.chat.common;

import com.vmloft.develop.library.tools.utils.VMSPUtil;

/**
 * Create by lzan13 on 2019/04/09
 *
 * SharedPreferences 配置管理类
 */
public class ASPManager {

    private final String KEY_RUN_VERSION = "key_run_version";
    private final String KEY_CURR_ACCOUNT = "key_curr_account";

    /**
     * 私有构造，初始化 ShredPreferences 文件名
     */
    private ASPManager() {
        VMSPUtil.init("vmchat.conf");
    }

    /**
     * 内部类实现单例模式
     */
    private static class ASPManagerHolder {
        private static final ASPManager INSTANCE = new ASPManager();
    }

    /**
     * 获取的实例
     */
    public static final ASPManager getInstance() {
        return ASPManagerHolder.INSTANCE;
    }


    /**
     * 保存当前运行版本
     */
    public void putRunVersion(long version) {
        VMSPUtil.put(KEY_RUN_VERSION, version);
    }

    /**
     * 获取当前运行的版本号
     */
    public long getRunVersion() {
        return (long) VMSPUtil.get(KEY_RUN_VERSION, 0l);
    }

    /**
     * 保存当前登录账户信息
     *
     * @param account 当前账户信息 json 串
     */
    public void putCurrAccount(String account) {
        VMSPUtil.put(KEY_CURR_ACCOUNT, account);
    }

    /**
     * 获取当前登录账户信息登录
     *
     * @return 如果为空，说明没有登录
     */
    public String getCurrAccount() {
        return (String) VMSPUtil.get(KEY_CURR_ACCOUNT, "");
    }
}
