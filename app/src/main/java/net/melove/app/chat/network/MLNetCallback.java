package net.melove.app.chat.network;

import org.json.JSONObject;

/**
 * Created by lzan13 on 2016/12/4.
 * 自定义网络请求回调接口
 */
public interface MLNetCallback {

    /**
     * 操作成功回调
     *
     * @param object 请求成功返回的 json 串
     */
    void onSuccess(JSONObject object);

    /**
     * 操作失败回调
     *
     * @param code 失败错误码
     * @param error 失败信息
     */
    void onFailed(int code, String error);
}
