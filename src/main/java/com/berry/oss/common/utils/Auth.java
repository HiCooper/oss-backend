package com.berry.oss.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.berry.oss.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-29 11:03
 * fileName：Auth
 * Use：
 */
public final class Auth {

    /**
     * 加密json 中 请求ip key
     */
    private static final String ENCODE_JSON_REQUEST_IP_KEY = "requestIp";

    /**
     * 加密json 中 token过期时间 key
     */
    private static final String ENCODE_JSON_DEADLINE_KEY = "deadline";

    private static final String HMAC_SHA_1 = "HmacSHA1";

    private static final Logger logger = LoggerFactory.getLogger(Auth.class);

    public final String accessKeyId;
    private final SecretKeySpec secretKeySpec;

    private Auth(String accessKeyId, SecretKeySpec secretKeySpec) {
        this.accessKeyId = accessKeyId;
        this.secretKeySpec = secretKeySpec;
    }

    private static final List<String> ACCESS_TOKEN_CAN_REQUEST_API = new ArrayList<>(8);

    // 上传 access_token 可以访问的 api 列表
    static {
        ACCESS_TOKEN_CAN_REQUEST_API.add("/ajax/bucket/create");
        ACCESS_TOKEN_CAN_REQUEST_API.add("/ajax/bucket/upload_byte.json");
        ACCESS_TOKEN_CAN_REQUEST_API.add("/ajax/bucket/upload_base64.json");
    }

    /**
     * 验证 upload 请求token 签名
     *
     * @param dataArr         待解码 token 字符串数组
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
        // 验证 目标访问url 必须是 ACCESS_TOKEN_CAN_REQUEST_API 列表中的地址
        if (ACCESS_TOKEN_CAN_REQUEST_API.stream().noneMatch(requestUrl::matches)) {
            throw new IllegalAccessException("非授权访问url");
        }
        logger.info("来源IP：{}，成功通过AccessKeyId:{},访问URL:{}", object.getString(ENCODE_JSON_REQUEST_IP_KEY), accessKeyId, requestUrl);
    }

    /**
     * 校验通用请求 token 签名
     *
     * @param originAuthorization 原始 token
     * @param urlString           请求url
     * @param accessKeyId         accessKeyId
     * @param accessKeySecret     accessKeySecret
     * @throws IllegalAccessException 签名校验失败
     */
    public static void validRequest(String originAuthorization, String urlString, String accessKeyId, String accessKeySecret) throws IllegalAccessException {

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

        String digest = Base64Util.encode(mac.doFinal());
        String signRequestStr = accessKeyId + ":" + digest;

        String authorization = Constants.OSS_SDK_AUTH_PREFIX + signRequestStr;
        if (!authorization.equals(originAuthorization)) {
            throw new IllegalAccessException("签名校验失败");
        }
    }

    private static Auth create(String accessKeyId, String accessKeySecret) {
        if (StringUtils.isBlank(accessKeyId) || StringUtils.isBlank(accessKeySecret)) {
            throw new IllegalArgumentException("empty key");
        }
        byte[] sk = StringUtils.utf8Bytes(accessKeySecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(sk, HMAC_SHA_1);
        return new Auth(accessKeyId, secretKeySpec);
    }

    private Mac createMac() {
        Mac mac;
        try {
            mac = Mac.getInstance(HMAC_SHA_1);
            mac.init(secretKeySpec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return mac;
    }
}
