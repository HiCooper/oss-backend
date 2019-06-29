package com.berry.oss.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    private static final OkHttpClient CLIENT;

    static {
        CLIENT = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
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
