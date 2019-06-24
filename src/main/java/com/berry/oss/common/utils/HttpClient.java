package com.berry.oss.common.utils;

import okhttp3.*;
import okhttp3.internal.http.RealInterceptorChain;

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

    public static Response doPost(String url, Map<String, Object> params) {
        Request request = new Request.Builder()
                .post(createPostRequestBody(params))
                .url(url)
                .build();
        Call call = CLIENT.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return response;
    }

    private static RequestBody createPostRequestBody(Map<String, Object> params) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params == null || params.isEmpty()){
            return bodyBuilder.build();
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            bodyBuilder.add(entry.getKey(), entry.getValue().toString());
        }

        return bodyBuilder.build();
    }
}
