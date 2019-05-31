package com.berry.oss.common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * ThreeDESecbUtil {3DES加密解密的工具类 }
 * 32位密钥  加密方式：DESede即DESede/ECB/PKCS5Padding
 *
 * @author Berry_Cooper
 * @date 2017-11-15
 */
public class ThreeDESecbUtil {

    /**
     * 定义加密算法，有DES、DESede(即3DES)、Blowfish
     */
    private static final String ALGORITHM = "DESede";
    /**
     * 密钥
     */
    private static SecretKey securekey;


    public static void main(String[] args) {
        String msg = "{'hello'}";
        //key长度必须为32位
        String key = "1A2B3C4D5E6F78901234A5B68E9F0";
        System.out.println("【加密前】：" + msg);

        //加密
        String secretArr = ThreeDESecbUtil.encrypt(key, msg);
        System.out.println("【加密后】：" + secretArr);

        //解密
        String myMsgArr = ThreeDESecbUtil.decrypt(key, secretArr);
        System.out.println("【解密后】：" + myMsgArr);
    }

    /**
     * 加密
     *
     * @param enKey 加密密钥
     * @param src   待加密内容
     * @return 加密的密文
     */
    public static String encrypt(String enKey, String src) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(enKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            securekey = keyFactory.generateSecret(dks);
            // 实例化负责加密/解密的Cipher工具类
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            // 初始化为加密模式
            c1.init(Cipher.ENCRYPT_MODE, securekey);
            return ConvertTools.bytesToHexString(c1.doFinal(src.getBytes()));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }


    /**
     * 解密
     *
     * @param enKey 加密密钥
     * @param src   已加密的密文
     * @return 已解密的明文
     */
    public static String decrypt(String enKey, String src) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(enKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            securekey = keyFactory.generateSecret(dks);

            Cipher c = Cipher.getInstance(ALGORITHM);
            // 初始化为解密模式
            c.init(Cipher.DECRYPT_MODE, securekey);
            byte[] result = c.doFinal(ConvertTools.hexStringToByte(src));
            return new String(result);
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
}
