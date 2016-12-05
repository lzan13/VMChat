package net.melove.app.chat.module.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lzan13 on 2016/12/3.
 * 定义 Retrofit 网络请求接口
 */
public interface MLMobAPI {

    /**
     * 保存新数据到服务器
     *
     * @param key 请求需要的 key
     * @param table 要保存到的表
     * @param k 数据保存的 key
     * @param v 要保存的数据
     */
    @GET("ucache/put") Call<ResponseBody> saveData(@Query("key") String key,
            @Query("table") String table, @Query("k") String k, @Query("v") String v);

    /**
     * 从服务器获取数据
     *
     * @param key 请求 url 需要的 key
     * @param table 获取数据的表
     * @param k 获取数据的查询条件
     */
    @GET("ucache/get") Call<ResponseBody> getData(@Query("key") String key,
            @Query("table") String table, @Query("k") String k);
}
