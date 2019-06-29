package com.berry.oss.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.berry.oss.common.constant.Constants;

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

    public final String accessKeyId;
    private final SecretKeySpec secretKeySpec;

    private Auth(String accessKeyId, SecretKeySpec secretKeySpec) {
        this.accessKeyId = accessKeyId;
        this.secretKeySpec = secretKeySpec;
    }

    public static Auth create(String accessKeyId, String accessKeySecret) {
        if (StringUtils.isBlank(accessKeyId) || StringUtils.isBlank(accessKeySecret)) {
            throw new IllegalArgumentException("empty key");
        }
        byte[] sk = StringUtils.utf8Bytes(accessKeySecret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(sk, "HmacSHA1");
        return new Auth(accessKeyId, secretKeySpec);
    }


    /**
     * 获取授权 口令，
     * <p>注意：该口令拥有对应账户的所有权限</p>
     *
     * @param expires 有效时长，单位秒。默认3600s
     * @return
     */
    public String accessToken(long expires) {
        long deadline = System.currentTimeMillis() / 1000 + expires;
        // 这里仅保存了 过期时间信息,可添加更多信息，以便日志记录，如请求ip
        StringMap map = new StringMap();
        map.put("deadline", deadline);
        // 1.过期时间 和 bucket 转 json 再 base64 编码 得到 encodeJson
        String json = Json.encode(map);
        String encodeJson = Base64Util.encode(StringUtils.utf8Bytes(json));

        // 2.对 encodeJson 进行 mac 加密 再进行 base64 编码 得到 encodedSign
        Mac mac = this.createMac();
        String encodedSign = Base64Util.encode(mac.doFinal(StringUtils.utf8Bytes(encodeJson)));

        // 3.拼接 accessKeyId encodedSign 和 encodeJson，用英文冒号隔开
        return this.accessKeyId + ":" + encodedSign + ":" + encodeJson;
    }

    public static void main(String[] args) throws IllegalAccessException {

        // 1。 生成 token
        Auth auth = Auth.create("yRdQE7hybEfPD5Kgt4fXCe", "wkZ2RvEnuom/Pa4RTQGmPdFVd6g7/CO");
        String token = auth.accessToken(3600);
        System.out.println(token);

        // 2. 验证 token 并获取信息 json { deadline: Number }
        verifyThenGetData(token, "wkZ2RvEnuom/Pa4RTQGmPdFVd6g7/CO");
//        yRdQE7hybEfPD5Kgt4fXCe:osOs0CWkcnVvbZQBjPTgsu5E49c=:eyJzY29wZSI6ImJlcnJ5IiwiZGVhZGxpbmUiOjE1NjE4MDMyMDR9
    }

    /**
     * 验证签名，返回 json 信息
     *
     * @param encodedData     待解码 token
     * @param accessKeySecret 私钥
     * @return bucket name
     */
    public static void verifyThenGetData(String encodedData, String accessKeySecret) throws IllegalAccessException {
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
        long deadline = object.getLong("deadline");
        Date deadDate = new Date(deadline * 1000);
        if (deadDate.before(new Date())) {
            throw new IllegalAccessException("口令已过期");
        }
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
