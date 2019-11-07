package com.berry.oss.common.utils;


import com.berry.oss.common.constant.Constants;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author xueancao
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final int MAGIC_NUM = 1024;

    private static final String GUTTER = ".";

    /**
     * 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）&拼接成字符
     *
     * @param paramsMap map
     * @return urlStr
     */
    public static String sortMap(Map<String, Object> paramsMap) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<>(paramsMap.entrySet());
        infoIds.sort(Comparator.comparing(Map.Entry::getKey));
        // 构造URL 键值对的格式
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, Object> item : infoIds) {
            if (isNotBlank(item.getKey()) && item.getValue() != null) {
                String key = item.getKey();
                String val = item.getValue().toString();
                buf.append(key).append("=").append(val);
                buf.append("&");
            }
        }
        String buff = buf.toString();
        if (!buff.isEmpty()) {
            buff = buff.substring(0, buff.length() - 1);
        }
        return buff;
    }

    /**
     * 根据文件大小，获取格式化文件描述
     *
     * @param size
     * @return
     */
    public static String getFormattedSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        double KB = (double) size / MAGIC_NUM;
        if (KB <= MAGIC_NUM) {
            return df.format(KB) + "KB";
        }
        double MB = KB / MAGIC_NUM;
        if (MB <= MAGIC_NUM) {
            return df.format(MB) + "MB";
        }
        double GB = MB / MAGIC_NUM;
        if (GB <= MAGIC_NUM) {
            return df.format(GB) + "GB";
        }
        throw new RuntimeException("File size too Big!!!");
    }

    /**
     * 获取 文件拓展名
     *
     * @param fileName 文件名
     * @return 拓展名
     */
    public static String getExtName(String fileName) {
        if (!fileName.contains(GUTTER)) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }

    /**
     * 随机生成指定长度位字符串
     *
     * @return 指定长度位字符串
     */
    public static String getRandomStr(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789./";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 将请求参数流转为字符串
     *
     * @param request
     * @return
     */
    public static String requestToStr(HttpServletRequest request) {
        String requestStr = null;
        try {
            InputStream inStream;
            inStream = request.getInputStream();
            int bufferSize = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[bufferSize];
                int count;
                while ((count = inStream.read(tempBytes, 0, bufferSize)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                requestStr = new String(outStream.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestStr;
    }

    /**
     * 去除特殊字符
     *
     * @param str
     * @return
     */
    public static String strFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 将特殊字符 替换为 '-'
     *
     * @param str
     * @return
     */
    public static String filterUnsafeUrlCharts(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String regEx = "[<>\"#%{}^\\[\\]`\\s\\\\]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("-").trim();
    }

    /**
     * 获取文件媒体类型
     *
     * @param fileName 文件名
     * @return
     */
    public static String getContentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        }
        if (fileName.endsWith(".gif")) {
            return MediaType.IMAGE_GIF_VALUE;
        }
        if (fileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        }
        if (fileName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
        }
        if (fileName.endsWith(".json")) {
            return MediaType.APPLICATION_JSON_VALUE;
        }
        if (fileName.endsWith(".xml")) {
            return MediaType.APPLICATION_XML_VALUE;
        }
        if (fileName.endsWith(".md")) {
            return MediaType.TEXT_MARKDOWN_VALUE;
        }
        if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    public static byte[] utf8Bytes(String data) {
        return data.getBytes(Constants.UTF_8);
    }

    public static String utf8String(byte[] data) {
        return new String(data, Constants.UTF_8);
    }

}
