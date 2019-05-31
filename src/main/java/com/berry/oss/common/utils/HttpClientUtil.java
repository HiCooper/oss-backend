package com.berry.oss.common.utils;

/**
 * @author Berry_Cooper
 * @date 2017/12/25.
 */

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 封装了采用HttpClient发送HTTP请求的方法
 * <p>
 * <p>
 * 本工具所采用的是HttpComponents-Client-4.3.2
 * ===========================
 * ===========================
 * sendPostSSLRequest 发送https post请求，  兼容 http post 请求
 * sendGetSSLRequest  发送https get请求，  兼容 http get 请求
 */

public class HttpClientUtil {

    /***
     * 连接超时,建立链接超时时间,毫秒.
     */
    private static final int CONN_TIMEOUT = 10000;
    /**
     * 响应超时,响应超时时间,毫秒.
     */
    private static final int READ_TIMEOUT = 300000;
    /**
     * https 请求方式
     */
    private static String https = "https";

    /**
     * 初始化client
     */
    private static HttpClient client;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * Https Post
     * <p>
     * 默认：
     * UTF-8编码
     * 参数类型 json
     *
     * @param url          serverUrl
     * @param parameterStr 参数json串
     * @return 请求结果
     */
    public static String doPost(String url, String parameterStr) {
        return sendPostSSLRequest(url, parameterStr, Consts.UTF_8.name(), "application/json");
    }

    /**
     * Https Post
     *
     * @param url          serverUrl
     * @param parameterStr 参数json串
     * @param charset      指定字符编码
     * @param mimeType     请求对象MIME类型
     * @return 请求结果
     */
    public static String doPost(String url, String parameterStr, String charset, String mimeType) {
        return sendPostSSLRequest(url, parameterStr, charset, mimeType);
    }

    /**
     * Http Post
     *
     * @param url    serverUrl
     * @param params params 请求参数map
     * @return 请求结果
     * @throws Exception 请求异常
     */
    public static String doPost(String url, Map<String, String> params) throws Exception {
        return postForm(url, params, null, CONN_TIMEOUT, READ_TIMEOUT);
    }

    /**
     * Http Post
     * 自定义网络参数
     *
     * @param url         serverUrl
     * @param params      请求参数map
     * @param connTimeout 自定义连接超时时间
     * @param readTimeout 自定义读取超时时间
     * @return 请求结果
     * @throws Exception 请求异常
     */
    public static String doPost(String url, Map<String, String> params, Integer connTimeout, Integer readTimeout) throws Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }

    /**
     * https or http Get
     * 默认UTF-8编码
     *
     * @param url serverUrl
     * @return 请求结果
     */
    public static String doGet(String url) {
        return sendGetSSLRequest(url, Consts.UTF_8.name());
    }

    /**
     * https or http Get
     * 自定义字符编码
     *
     * @param url     serverUrl
     * @param charset 指定字符编码
     * @return 请求结果
     */
    public static String doGet(String url, String charset) {
        return sendGetSSLRequest(url, charset);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url      地址
     * @param body     RequestBody
     * @param charset  编码
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @return 结果
     */
    private static String sendPostSSLRequest(String url, String body, String charset, String mimeType) {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "通信失败";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            customReqConf.setConnectTimeout(CONN_TIMEOUT);
            customReqConf.setSocketTimeout(READ_TIMEOUT);
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith(https)) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();

                res = client.execute(post);

            } else {
                // 执行 Http 请求.
                client = HttpClientUtil.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
            if (url.startsWith(https) && client instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) client).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 提交form表单
     *
     * @param url         url
     * @param params      参数
     * @param headers     请求头
     * @param connTimeout 连接超时
     * @param readTimeout 读取超时
     * @return 结果
     * @throws Exception 异常
     */
    private static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws Exception {

        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<>();
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                post.setEntity(entity);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse res;
            if (url.startsWith(https)) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtil.client;
                res = client.execute(post);
            }
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {
            post.releaseConnection();

            if (url.startsWith(https) && (client instanceof CloseableHttpClient)) {
                ((CloseableHttpClient) client).close();
            }
        }
    }


    /**
     * 发送一个 GET 请求(https or http)
     *
     * @param url     url
     * @param charset 字符编码
     * @return 结果
     */
    private static String sendGetSSLRequest(String url, String charset) {

        HttpClient client = null;
        HttpGet get = new HttpGet(url);
        String result = "通信失败";
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            customReqConf.setConnectTimeout(CONN_TIMEOUT);
            customReqConf.setSocketTimeout(READ_TIMEOUT);
            get.setConfig(customReqConf.build());

            HttpResponse res = null;

            if (url.startsWith(https)) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);

            } else {
                // 执行 Http 请求.
                client = HttpClientUtil.client;
                res = client.execute(get);
            }

            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        } finally {
            get.releaseConnection();
            if (url.startsWith(https) && client instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) client).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 从 response 里获取 CHARSET
     *
     * @param response 响应
     * @return 字符编码
     */
    private static String getCharsetFromResponse(HttpResponse response) {
        // Content-Type:text/html; CHARSET=GBK
        if (response.getEntity() != null && response.getEntity().getContentType() != null && response.getEntity().getContentType().getValue() != null) {
            String contentType = response.getEntity().getContentType().getValue();
            if (contentType.contains("CHARSET=")) {
                return contentType.substring(contentType.indexOf("CHARSET=") + 8);
            }
        }
        return null;
    }


    /**
     * 创建 SSL连接
     *
     * @return 建立 连接result
     */
    private static CloseableHttpClient createSSLInsecureClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);

        return HttpClients.custom().setSSLSocketFactory(sslsf).build();

    }

//    /**
//     * 文件上传
//     * @param fileName
//     */
//    public static void uploadFile(String fileName,String serverUrl) {
//        try {
//            File file = new File(fileName);
//            if (file.exists()) {
//                HttpClient client = HttpClientUtil.client;
//
//                HttpPost httpPost = new HttpPost(serverUrl);
//                httpPost.addHeader("Authorization","7178f37c-8e70-4f92-9576-350e957942f0");
//
//                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
//                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//                        .addTextBody("package_id","testtestteststetet")
//                        .addTextBody("name",file.getName())
//                        .addPart("upload", new FileBody(file));
//
//                HttpEntity entity = builder.build();
//                httpPost.setEntity(entity);
//                HttpResponse response = client.execute(httpPost);
//
//                HttpEntity responseEntity = response.getEntity();
//                String result = EntityUtils.toString(responseEntity);
//                System.out.println(result);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}