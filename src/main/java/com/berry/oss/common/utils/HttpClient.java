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
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/24 15:29
 */
public class HttpClient {

    private static final OkHttpClient CLIENT;

    static {
        CLIENT = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static Response doGet(String url, Map<String, Object> params){
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue()).append("&");
        }
        url = url + "?" + sb.toString();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Response response = null;
        try {
            response = CLIENT.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return response;
    }

    public static Response doPost(String url, Map<String, Object> params) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        RequestBody requestBody = RequestBody.create(MediaType.get(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE), gson.toJson(params));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        Response response = null;
        try {
            response = CLIENT.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return response;
    }
}
