package com.vmloft.develop.app.chat.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lzan13 on 2016/12/9.
 * 定义 Retrofit 网络请求接口，这里主要是请求自己的服务器 API
 */
public interface NetworkAPI {

    /**
     * 获取七牛上传文件 token
     *
     * @param key 上传文件经过 md5 生成的 key，一般作为服务器保存的文件名
     * @return 请求结果
     */
    @GET("auth/upload-token/{key}") Call<ResponseBody> getUploadToken(@Path("key") String key);

    /**
     * 获取账户 accessToken
     *
     * @param username 账户名
     * @param password 账户密码
     * @return 请求结果
     */
    @FormUrlEncoded @POST("auth/token") Call<ResponseBody> authToken(
            @Field("username") String username, @Field("password") String password);

    /**
     * 创建新账户
     *
     * @param username 账户名
     * @param password 账户密码
     * @return 请求结果
     */
    @FormUrlEncoded @POST("users/create") Call<ResponseBody> createUser(
            @Field("username") String username, @Field("password") String password);

    /**
     * 更新账户信息
     *
     * @param email 邮箱地址
     * @param nickname 昵称
     * @param signature 签名
     * @param location 地址
     * @param gender 性别
     * @param accessToken 账户 token
     * @return 请求结果
     */
    @Multipart @PUT("users/update") Call<ResponseBody> updateUser(@Part("email") String email,
            @Part("nickname") String nickname, @Part("signature") String signature,
            @Part("location") String location, @Part("gender") String gender,
            @Part("access_token") String accessToken);

    /**
     * 更新账户头像
     *
     * @param avatar 头像地址
     * @param accessToken 账户 token
     * @return 请求结果
     */
    @Multipart @PUT("users/avatar") Call<ResponseBody> updateAvatar(@Part("avatar") String avatar,
            @Part("access_token") String accessToken);

    /**
     * 更新账户背景图
     *
     * @param cover 背景图地址
     * @param accessToken 账户 token
     * @return 请求结果
     */
    @Multipart @PUT("users/cover") Call<ResponseBody> updateCover(@Part("cover") String cover,
            @Part("access_token") String accessToken);

    /**
     * 获取用户信息
     *
     * @param username 账户名
     * @return 请求结果
     */
    @GET("users/{username}") Call<ResponseBody> getUser(@Path("username") String username);

    /**
     * 获取一组用户详细信息
     *
     * @param names 好友名称集合
     * @return 请求结果
     */
    @GET("friends/{names}") Call<ResponseBody> getUsersInfo(@Path("names") String names,
            @Query("access_token") String accessToken);

    /**
     * 添加好友
     *
     * @param accessToken 请求认证
     * @return 请求结果
     */
    @PUT("friends/{username}") Call<ResponseBody> addFriend(
            @Part("access_token") String accessToken);

    /**
     * 删除好友
     *
     * @param accessToken 请求认证
     * @return 请求结果
     */
    @DELETE("friends/{username}") Call<ResponseBody> removeFriend(
            @Part("access_token") String accessToken);

    /**
     * 获取好友列表
     *
     * @param accessToken 请求认证 token
     * @return 请求结果
     */
    @GET("friends/list") Call<ResponseBody> getFriends(@Query("access_token") String accessToken);

}
