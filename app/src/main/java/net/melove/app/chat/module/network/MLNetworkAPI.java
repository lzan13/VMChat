package net.melove.app.chat.module.network;

import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by lzan13 on 2016/12/9.
 * 定义 Retrofit 网络请求接口，这里主要是请求自己的服务器 API
 */
public interface MLNetworkAPI {

    /**
     * 获取七牛上传文件 token
     *
     * @param key 上传文件经过 md5 生成的 key，一般作为服务器保存的文件名
     */
    @GET("upload_token/{key}") Call<ResponseBody> getUploadToken(@Path("key") String key);

    @FormUrlEncoded @POST("user/async") Call<ResponseBody> asyncUsers(
            @Field("usernames") String[] object);
}
