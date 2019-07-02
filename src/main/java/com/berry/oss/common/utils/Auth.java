package com.berry.oss.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.berry.oss.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-29 11:03
 * fileName：Auth
 * Use：
 */
public class Auth {

    /**
     * 加密json 中 请求ip key
     */
    private static final String ENCODE_JSON_REQUEST_IP_KEY = "requestIp";
    /**
     * 授权token 可访问的唯一 地址
     */
    private static final String UPLOAD_URL = "/ajax/bucket/file/create";
    /**
     * 加密json 中 token过期时间 key
     */
    private static final String ENCODE_JSON_DEADLINE_KEY = "deadline";

    private static final Logger logger = LoggerFactory.getLogger(Auth.class);

    public final String accessKeyId;
    private final SecretKeySpec secretKeySpec;

    private Auth(String accessKeyId, SecretKeySpec secretKeySpec) {
        this.accessKeyId = accessKeyId;
        this.secretKeySpec = secretKeySpec;
    }

    /**
     * 验证 upload 请求token 签名
     *
     * @param dataArr     待解码 token 字符串数组
     * @param accessKeySecret 私钥
     */
    public static void verifyUploadToken(String[] dataArr, String accessKeySecret, String requestUrl) throws IllegalAccessException {
        String accessKeyId = dataArr[0];
        String encodedSign = dataArr[1];
        String encodeJson = dataArr[2];

        Mac mac = Auth.create(accessKeyId, accessKeySecret).createMac();
        String encodedSignReal = Base64Util.encode(mac.doFinal(StringUtils.utf8Bytes(encodeJson)));
        if (!encodedSignReal.equals(encodedSign)) {
            throw new IllegalAccessException("签名校验失败");
        }
        String json = StringUtils.utf8String(Base64Util.decode(encodeJson));
        // 验证json 有效期
        JSONObject object = JSONObject.parseObject(json);
        long deadline = object.getLong(ENCODE_JSON_DEADLINE_KEY);
        Date deadDate = new Date(deadline * 1000);
        if (deadDate.before(new Date())) {
            throw new IllegalAccessException("口令已过期");
        }
        // 验证 目标访问url 必须是 'UPLOAD_URL'
        if (!requestUrl.equals(UPLOAD_URL)) {
            throw new IllegalAccessException("非授权访问url");
        }
        logger.info("来源IP：{}，成功通过AccessKeyId:{},访问URL:{}", object.getString(ENCODE_JSON_REQUEST_IP_KEY), accessKeyId, requestUrl);
    }

    /**
     * 校验通用请求 token 签名
     *
     * @param originAuthorization 原始 token
     * @param urlString           请求url
     * @param body                请求体
     * @param contentType         请求体类型
     * @param accessKeyId         accessKeyId
     * @param accessKeySecret     accessKeySecret
     * @throws IllegalAccessException 签名校验失败
     */
    public static void validRequest(String originAuthorization, String urlString, byte[] body, String contentType, String accessKeyId, String accessKeySecret) throws IllegalAccessException {

        URI uri = URI.create(urlString);
        String path = uri.getRawPath();
        String query = uri.getRawQuery();

        Mac mac = Auth.create(accessKeyId, accessKeySecret).createMac();

        mac.update(StringUtils.utf8Bytes(path));

        if (query != null && query.length() != 0) {
            mac.update((byte) ('?'));
            mac.update(StringUtils.utf8Bytes(query));
        }
        mac.update((byte) '\n');
        if (body != null && Constants.FORM_MIME.equalsIgnoreCase(contentType)) {
            mac.update(body);
        }

        String digest = Base64Util.encode(mac.doFinal());
        String signRequestStr = accessKeyId + ":" + digest;

        String authorization = "OSS " + signRequestStr;
        if (!authorization.equals(originAuthorization)) {
            throw new IllegalAccessException("签名校验失败");
        }
    }

    private static Auth create(String accessKeyId, String accessKeySecret) {
        if (StringUtils.isBlank(accessKeyId) || StringUtils.isBlank(accessKeySecret)) {
            throw new IllegalArgumentException("empty key");
        }
        byte[] sk = StringUtils.utf8Bytes(accessKeySecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(sk, "HmacSHA1");
        return new Auth(accessKeyId, secretKeySpec);
    }

    private Mac createMac() {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return mac;
    }
}
