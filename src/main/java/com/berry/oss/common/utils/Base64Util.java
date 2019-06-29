package com.berry.oss.common.utils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-29 12:37
 * fileName：Base64Util
 * Use：
 */
public final class Base64Util {

    /**
     * base64 编码
     *
     * @param data 待加密数据字节数组
     * @return 加密字符串
     */
    public static String encode(byte[] data) {
        return new String(java.util.Base64.getEncoder().encode(data));
    }

    /**
     * base64 解码
     *
     * @param encodeStr encodeStr
     * @return 解密后字节数组
     */
    public static byte[] decode(String encodeStr) {
        return java.util.Base64.getDecoder().decode(encodeStr.getBytes());
    }

}
