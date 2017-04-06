package com.vmloft.develop.app.chat.network;

import java.io.IOException;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by lzan13 on 2016/12/9.
 * 自定义实现项目网络请求管理器
 */
public class NetworkManager {

    // 请求服务器地址
    private String baseUrl = "http://118.190.25.131:5200/api/v1/";
    private String mToken = "";
    private int mDeadline = 0;

    // 当前单例类的实例
    private static NetworkManager instanc;
    private NetworkAPI mNetworkAPI;

    private Retrofit mRetrofit;

    /**
     * 私有构造方法，在里边做一些全局的对象初始化操作
     */
    private NetworkManager() {
        // 获取 Retrofit 对象
        mRetrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
        // 使用 Retrofit 创建自定义网络请求接口的实例
        mNetworkAPI = mRetrofit.create(NetworkAPI.class);
    }

    /**
     * 获取当前类实例
     */
    public static NetworkManager getInstance() {
        if (instanc == null) {
            instanc = new NetworkManager();
        }
        return instanc;
    }

    /**
     * 获取向七牛上传图片 token
     *
     * @param key 上传图片时的 key，根据上传图片文件名称 md5 获取
     */
    public String getUploadToken(String key) {
        // 请求服务器获取
        Call<ResponseBody> call = mNetworkAPI.getUploadToken(key);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                JSONObject object = new JSONObject(response.body().string());
                mToken = object.optString("token");
                mDeadline = object.optInt("deadline");
                return mToken;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 账户登录认证，获取 access token
     *
     * @param username 账户名
     * @param password 账户密码
     * @return 返回请求结果
     */
    public JSONObject authToken(String username, String password) throws JSONException {
        JSONObject result = new JSONObject();
        Call<ResponseBody> call = mNetworkAPI.authToken(username, password);
        try {
            Response<ResponseBody> response = call.execute();
            JSONObject object = new JSONObject(response.body().string());
            result.put("code", object.optJSONObject("status").optInt("code"));
            result.put("msg", object.optJSONObject("status").optString("msg"));
            result.put("data", object.opt("data"));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", -1);
            result.put("msg", "Request server failed");
            return result;
        }
    }

    /**
     * 创建新账户
     *
     * @param username 账户名
     * @param password 账户密码
     * @return 是否成功
     */
    public JSONObject createUser(String username, String password) throws JSONException {
        JSONObject result = new JSONObject();
        // 创建请求
        Call<ResponseBody> call = mNetworkAPI.createUser(username, password);
        try {
            // 进行同步请求，并接受返回值
            Response<ResponseBody> response = call.execute();
            JSONObject object = new JSONObject(response.body().string());
            result.put("code", object.optJSONObject("status").optInt("code"));
            result.put("msg", object.optJSONObject("status").optString("msg"));
            result.put("data", object.opt("data"));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", -1);
            result.put("msg", "Request server failed");
            return result;
        }
    }

    /**
     * 获取单个用户信息
     *
     * @param username 账户名
     * @return 请求结果
     * @throws JSONException
     */
    public JSONObject getUser(String username) throws JSONException {
        JSONObject result = new JSONObject();
        // 创建请求
        Call<ResponseBody> call = mNetworkAPI.getUser(username);
        try {
            // 进行同步请求，并接受返回值
            Response<ResponseBody> response = call.execute();
            JSONObject object = new JSONObject(response.body().string());
            result.put("code", object.optJSONObject("status").optInt("code"));
            result.put("msg", object.optJSONObject("status").optString("msg"));
            result.put("data", object.opt("data"));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", -1);
            result.put("msg", "Request server failed");
            return result;
        }
    }

    /**
     * 获取一组用户详细信息
     *
     * @param names username 集合
     * @param accessToken 账户 token
     * @return 请求结果
     */
    public JSONObject getUsersInfo(String names, String accessToken) throws JSONException {
        JSONObject result = new JSONObject();
        // 创建请求
        Call<ResponseBody> call = mNetworkAPI.getUsersInfo(names, accessToken);
        try {
            // 进行同步请求，并接受返回值
            Response<ResponseBody> response = call.execute();
            JSONObject object = new JSONObject(response.body().string());
            result.put("code", object.optJSONObject("status").optInt("code"));
            result.put("msg", object.optJSONObject("status").optString("msg"));
            result.put("data", object.opt("data"));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", -1);
            result.put("msg", "Request server failed");
            return result;
        }
    }
}
