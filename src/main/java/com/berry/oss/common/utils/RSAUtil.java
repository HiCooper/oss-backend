package com.berry.oss.common.utils;

import org.springframework.core.io.ClassPathResource;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-15 20:45
 * fileName：RSAUtil
 * Use：JAVA实现RSA加密, 非对称加密算法
 * 公钥加密，私钥解密，用于加密传输，保护数据,不关注信息来源，仅保证私钥持有者能看到消息内容
 * 私钥加密(签名)，公钥解密(认证)，用于数字签名,确定信息来源来自私钥持有者
 * 公钥： 加密和认证
 * 私钥： 解密和签名
 */

public final class RSAUtil {

    private static final String KEY_ALGORITHM = "RSA";

    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 默认公钥的持久化文件存放位置
     */
    private static String PUBLIC_KEY_FILE = "rsa/publicKey_rsa_1024.pub";

    /**
     * 默认私钥的持久化文件存放位置
     */
    private static String PRIVATE_KEY_FILE = "rsa/privateKey_rsa_1024";

    static {
        // 检查密钥是否初始化
        ClassPathResource publicFile = new ClassPathResource(PUBLIC_KEY_FILE);
        if (!publicFile.exists()) {
            try {
                initKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化密钥
     *
     * @throws Exception
     */
    private static void initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        PublicKey publicKey = keyPair.getPublic();
        // 私钥
        PrivateKey privateKey = keyPair.getPrivate();

        //保存公钥对象和私钥对象为持久化文件
        ObjectOutputStream oos1 = null;
        ObjectOutputStream oos2 = null;
        try {
            ClassPathResource publicFile = new ClassPathResource(PUBLIC_KEY_FILE);
            ClassPathResource privateFile = new ClassPathResource(PRIVATE_KEY_FILE);
            oos1 = new ObjectOutputStream(new FileOutputStream(publicFile.getPath()));
            oos2 = new ObjectOutputStream(new FileOutputStream(privateFile.getPath()));
            oos1.writeObject(publicKey);
            oos2.writeObject(privateKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            oos1.close();
            oos2.close();
        }
    }

    /**
     * base64 加密
     *
     * @param data 待加密数据字节数组
     * @return 加密字符串
     */
    private static String encryptBase64(byte[] data) {
        return new String(Base64.getEncoder().encode(data));
    }


    /**
     * base64 解密
     *
     * @param encodeStr encodeStr
     * @return 解密后字节数组
     * @throws Exception
     */
    private static byte[] decryptBase64(String encodeStr) {
        return Base64.getDecoder().decode(encodeStr.getBytes());
    }

    /**
     * 获取公钥
     *
     * @return 字符串公钥
     * @throws Exception
     */
    private static String getPublicKey() throws Exception {
        ClassPathResource publicFile = new ClassPathResource(PUBLIC_KEY_FILE);
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicFile.getPath()));
        PublicKey publicKey = (PublicKey) inputStream.readObject();
        return encryptBase64(publicKey.getEncoded());
    }

    /**
     * 获取密钥
     *
     * @return 字符串密钥
     * @throws Exception
     */
    private static String getPrivateKey() throws Exception {
        ClassPathResource privateFile = new ClassPathResource(PRIVATE_KEY_FILE);
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(privateFile.getPath()));
        PrivateKey privateKey = (PrivateKey) inputStream.readObject();
        return encryptBase64(privateKey.getEncoded());
    }

    //------------ 私钥签名， 公钥认证 （常用） ------------

    /**
     * 生成数字签名
     * (私钥签名)
     *
     * @param data 加密数据
     * @return 签名
     * @throws Exception
     */
    public static String sign(String data) throws Exception {
        String privateKey = getPrivateKey();
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBase64(privateKey);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data.getBytes());

        return encryptBase64(signature.sign());
    }

    /**
     * 校验数字签名
     * (公钥认证)
     *
     * @param secret 加密数据
     * @param sign   数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(String secret, String sign)
            throws Exception {

        // 解密由base64编码的公钥
        String publicKey = getPublicKey();
        byte[] keyBytes = decryptBase64(publicKey);

        // 取得公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        // 用公钥对信息认证
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(secret.getBytes());

        // 验证签名是否正常
        return signature.verify(decryptBase64(sign));
    }

    // ------------ 私钥加密，公钥解密 ------------

    /**
     * 私钥加密<br>
     *
     * @param data 待加密信息
     * @return 密文
     * @throws Exception
     */
    public static String encryptByPrivateKey(String data)
            throws Exception {
        // 对密钥解密
        String key = getPrivateKey();
        byte[] keyBytes = decryptBase64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] secret = cipher.doFinal(data.getBytes());
        return encryptBase64(secret);
    }

    /**
     * 公钥解密<br>
     *
     * @param secret 密文
     * @return 解密后信息
     * @throws Exception
     */
    public static String decryptByPublicKey(String secret)
            throws Exception {
        // 对密钥解密
        String key = getPublicKey();
        byte[] keyBytes = decryptBase64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] proclaimed = cipher.doFinal(decryptBase64(secret));
        return new String(proclaimed);
    }

}
