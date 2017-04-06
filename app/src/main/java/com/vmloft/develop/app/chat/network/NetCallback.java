package com.vmloft.develop.app.chat.network;

/**
 * Created by lzan13 on 2016/12/4.
 * 自定义网络请求回调接口
 */
public interface NetCallback {

    /**
     * 操作成功回调
     *
     * @param object 请求结果
     */
    void onSuccess(Object object);

    /**
     * 操作失败回调
     *
     * @param code 失败错误码
     * @param error 失败信息
     */
    void onFailed(int code, String error);
}
