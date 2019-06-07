package com.berry.oss.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SHA256 {

    private static final String ALGORITHMS_NAME = "SHA-256";

    /**
     * 计算文件hash
     * @param filePath 文件路径
     * @return 16进制hash
     */
    public static String hash(String filePath) {
        long start = System.currentTimeMillis();
        File file = new File(filePath);
        String strDes;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            MessageDigest digest = MessageDigest.getInstance(ALGORITHMS_NAME);
            // 每次读取16M
            int bufferSize = 16 * 1024;
            byte[] buffer = new byte[bufferSize];
            int sizeRead;
            while ((sizeRead = in.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
            }
            strDes = bytes2Hex(digest.digest());
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
        System.out.println("calculate complete take time: " + (System.currentTimeMillis() - start) / 1000);
        return strDes;
    }

    /**
     * 计算一个字节数组的 hash
     *
     * @param src 字节数组
     * @return hash
     */
    public static String hash(byte[] src) {
        String strDes;
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHMS_NAME);
            md.update(src);
            strDes = bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
        return strDes;
    }

    private static String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp;
        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString().toUpperCase();
    }

}
