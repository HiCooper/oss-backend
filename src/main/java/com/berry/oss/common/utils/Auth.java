package com.berry.oss.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.berry.oss.common.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
     * 加密json 中 授权目标方法 key
     */
    private static final String ENCODE_JSON_TARGET_URL_KEY = "targetUrl";
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
     * 验证签名，返回 json 信息
     *
     * @param encodedData     待解码 token
     * @param accessKeySecret 私钥
     * @return bucket name
     */
    public static void verifyThenGetData(String encodedData, String accessKeySecret, String requestUrl) throws IllegalAccessException {
        String[] dataArr = encodedData.split(":");
        if (dataArr.length != Constants.ENCODE_DATA_LENGTH) {
            throw new IllegalArgumentException("非法口令");
        }
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
        // 验证 目标访问url
        if (!requestUrl.equals(object.getString(ENCODE_JSON_TARGET_URL_KEY))) {
            throw new IllegalAccessException("非授权访问url");
        }
        logger.info("来源IP：{}，成功通过AccessKeyId:{},访问URL:{}", object.getString(ENCODE_JSON_REQUEST_IP_KEY), accessKeyId, requestUrl);
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
