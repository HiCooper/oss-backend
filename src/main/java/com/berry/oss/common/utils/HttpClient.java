package com.berry.oss.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Title HttpClient
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/24 15:29
 */
public class HttpClient {

    /**
     * 默认 http 客户端
     */
    private static final OkHttpClient CLIENT;
    /**
     * 连接超时时间 单位秒(默认10s)
     */
    private static final int CONNECT_TIMEOUT = 10;
    /**
     * 回调超时
     */
    private static final int CALL_TIMEOUT = 10;
    /**
     * 回复超时时间 单位秒(默认30s)
     */
    private static final int READ_TIMEOUT = 30;
    /**
     * 底层HTTP库所有的并发执行的请求数量
     */
    private static final int DISPATCHER_MAX_REQUESTS = 64;
    /**
     * 底层HTTP库对每个独立的Host进行并发请求的数量
     */
    private static final int DISPATCHER_MAX_REQUESTS_PER_HOST = 16;
    /**
     * 底层HTTP库中复用连接对象的最大空闲数量
     */
    private static final int CONNECTION_POOL_MAX_IDLE_COUNT = 32;
    /**
     * 底层HTTP库中复用连接对象的回收周期（单位分钟）
     */
    private static final int CONNECTION_POOL_MAX_IDLE_MINUTES = 5;

    static {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(DISPATCHER_MAX_REQUESTS);
        dispatcher.setMaxRequestsPerHost(DISPATCHER_MAX_REQUESTS_PER_HOST);

        ConnectionPool pool = new ConnectionPool(CONNECTION_POOL_MAX_IDLE_COUNT,
                CONNECTION_POOL_MAX_IDLE_MINUTES, TimeUnit.MINUTES);

        CLIENT = new OkHttpClient.Builder()
                .callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .connectionPool(pool)
                .build();
    }

    public static byte[] doGet(String url, Map<String, Object> params) {
        String sortMap = StringUtils.sortMap(params);
        url = url + "?" + sortMap;
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (null != body) {
                    return body.bytes();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doPost(String url, Map<String, Object> params) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        RequestBody requestBody = RequestBody.create(MediaType.get(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE), gson.toJson(params));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (null != body) {
                    return body.string();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
