package net.melove.app.chat.module.network;

import android.util.Base64;
import java.io.IOException;
import net.melove.app.chat.util.MLLog;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by lzan13 on 2016/12/3.
 * 封装 MobAPI 网络请求管理类
 */
public class MLMobManager {
    // 请求地址
    private String baseUrl = "http://apicloud.mob.com/";
    // 请求需要 key
    private final String MOB_API_KEY = "199439878d8e7";
    // 请求操作的表
    private final String MOB_API_TABLE = "test";

    // 当前单例类的实例
    private static MLMobManager instanc;
    private Retrofit mRetrofit;
    private MLMobAPI mMobAPI;

    private MLMobManager() {
        mRetrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
        mMobAPI = mRetrofit.create(MLMobAPI.class);
    }

    /**
     * 获取当前类实例
     */
    public static MLMobManager getInstance() {
        if (instanc == null) {
            instanc = new MLMobManager();
        }
        return instanc;
    }

    /**
     * 使用 Retrofit 网络框架请求 MobAPI 保存数据接口
     * 这里将参数使用 Base64 进行编码，并且模式选择 URL_SAFE 和 NO_WRAP
     * 其中 URL_SAFE 是为了在 url 中不出错
     * NO_WRAP 是为了防止编码后结尾出现
     *
     * @param key 要保存数据的 key
     * @param value 要保存数据的 value
     */
    public void asyncPutData(String key, String value, final MLNetCallback callback) {
        String keyBase64 = Base64.encodeToString(key.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        String valueBase64 =
                Base64.encodeToString(value.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        Call<ResponseBody> call =
                mMobAPI.saveData(MOB_API_KEY, MOB_API_TABLE, keyBase64, valueBase64);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MLLog.d("请求成功：%s", response.message());
                ResponseBody body = response.body();
                try {
                    JSONObject object = new JSONObject(body.string());
                    int code = Integer.valueOf(object.optString("retCode"));
                    String msg = object.optString("msg");
                    if (code != 200) {
                        callback.onFailed(code, msg);
                    } else {
                        callback.onSuccess(object);
                    }
                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    callback.onFailed(-1, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                MLLog.d("请求失败：%s", t.getMessage());
                callback.onFailed(-1, t.getMessage());
            }
        });
    }

    /**
     * 使用 Retrofit 网络框架异步请求 MobAPI 获取数据接口
     * 这里将参数使用 Base64 进行编码，并且模式选择 URL_SAFE 和 NO_WRAP
     * 其中 URL_SAFE 是为了在 url 中不出错
     * NO_WRAP 是为了防止编码后结尾出现 '\n'
     *
     * @param key 获取数据的筛选条件
     */
    public void asyncGetData(String key, final MLNetCallback callback) {
        String keyBase64 = Base64.encodeToString(key.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        Call<ResponseBody> call = mMobAPI.getData(MOB_API_KEY, MOB_API_TABLE, keyBase64);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MLLog.d("请求成功：%s", response.message());
                ResponseBody body = response.body();
                try {
                    JSONObject object = new JSONObject(body.string());
                    int code = Integer.valueOf(object.optString("retCode"));
                    String msg = object.optString("msg");
                    if (code != 200) {
                        callback.onFailed(code, msg);
                    } else {
                        callback.onSuccess(object);
                    }
                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    callback.onFailed(-1, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                MLLog.d("请求失败：%s", t.getMessage());
                callback.onFailed(-1, t.getMessage());
            }
        });
    }

    /**
     * 使用 Retrofit 同步请求 MobAPI 获取数据接口
     *
     * @param key 获取数据的筛选条件
     * @return 返回获取到的数据
     */
    public String syncGetData(String key) {
        String keyBase64 = Base64.encodeToString(key.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        Call<ResponseBody> call = mMobAPI.getData(MOB_API_KEY, MOB_API_TABLE, keyBase64);
        try {
            Response<ResponseBody> response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
