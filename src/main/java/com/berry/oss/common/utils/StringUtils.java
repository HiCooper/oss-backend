package com.berry.oss.common.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
     * 按照长度获取字符串，如果超出截取最大长度，后面加...
     *
     * @param str
     * @param len
     * @return
     */
    public static String maxString(String str, Integer len) {

        if (str == null || str.length() <= len) {
            return str;
        }
        return str.substring(0, len) + "...";
    }


    /**
     * 返回等长字符，中间补零，如果前缀+字符串>长度，返回字符串
     * eg:lengthMaxString("A",4,"3") == "A003"
     *
     * @param prefix
     * @param len
     * @param str
     * @return
     */
    public static String lengthMaxNubmer(String prefix, int len, String str) {

        if (str == null || str.length() > len) {
            return str;
        }

        if (prefix.length() + str.length() > len) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (int i = 0; i < len - prefix.length() - str.length(); i++) {
            sb.append("0");
        }
        sb.append(str);

        return sb.toString();
    }


    /**
     * 隐藏字符串指定位置的字符
     *
     * @param str    指定字符串
     * @param index  起始位置
     * @param length 字符长度
     * @return 返回隐藏字符后的字符串
     */
    public static String hideChars(String str, int index, int length) {
        return hideChars(str, index, length, true);
    }

    /**
     * 隐藏字符串指定位置的字符
     *
     * @param str       指定字符串
     * @param start     起始位置
     * @param end       结束位置
     * @param confusion 是否混淆隐藏的字符个数
     * @return 返回隐藏字符后的字符串
     */
    public static String hideChars(String str, int start, int end, boolean confusion) {

        StringBuilder buf = new StringBuilder();
        if (!isEmpty(str)) {
            int startIndex = Math.min(start, end);
            int endIndex = Math.max(start, end);
            // 如果起始位置超出索引范围则默认置为0
            if (startIndex < 0 || startIndex > str.length()) {
                startIndex = 0;
            }
            // 如果结束位置超出索引范围则默认置为字符串长度
            if (endIndex < 0 || endIndex > str.length()) {
                endIndex = str.length();
            }
            String temp = newString("*", confusion ? 4 : endIndex - startIndex);
            buf.append(str).replace(startIndex, endIndex, temp);

        }
        return buf.toString();
    }

    /**
     * 根据指定字符串和重复次数生成新字符串
     *
     * @param str         指定字符串
     * @param repeatCount 重复次数
     * @return 返回生成的新字符串
     */
    private static String newString(String str, int repeatCount) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            buf.append(str);
        }
        return buf.toString();
    }


    /**
     * 将指定字符串转换成驼峰命名方式
     *
     * @param str 指定字符串
     * @return 返回驼峰命名方式
     */
    public static String toCalmelCase(String str) {

        StringBuilder buffer = new StringBuilder(str);
        if (buffer.length() > 0) {
            // 将首字母转换成小写
            char c = buffer.charAt(0);
            buffer.setCharAt(0, Character.toLowerCase(c));
            String reg = "_\\w";
            Pattern pattern = Pattern.compile(reg);
            Matcher m = pattern.matcher(buffer.toString());
            while (m.find()) {
                // 匹配的字符串
                String temp = m.group();
                // 匹配的位置
                int index = buffer.indexOf(temp);
                // 去除匹配字符串中的下划线，并将剩余字符转换成大写
                buffer.replace(index, index + temp.length(), temp.replace("_", "").toUpperCase());
            }
        }
        return buffer.toString();
    }


    /**
     * 将指定数组转换成字符串
     *
     * @param objs 指定数组
     * @return 返回转换后的字符串
     */
    public static String array2String(Object[] objs) {

        StringBuilder buffer = new StringBuilder();
        if (objs != null) {
            for (Object obj : objs) {
                buffer.append(obj).append(",");
            }
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }


    /**
     * 判断属性是否为日期类型
     *
     * @param clazz     数据类型
     * @param fieldName 属性名
     * @return 如果为日期类型返回true，否则返回false
     */
    public static boolean isDateType(Class clazz, String fieldName) {

        boolean flag = false;
        try {
            Field field = clazz.getDeclaredField(fieldName);
            Object typeObj = field.getType().newInstance();
            flag = typeObj instanceof Date;
        } catch (Exception e) {
            // 把异常吞掉直接返回false
        }
        return flag;
    }

    /***
     * 合并字符数组
     *
     * @param a
     * @return
     */
    public static char[] mergeArray(char[]... a) {
        // 合并完之后数组的总长度
        int index = 0;
        int sum = 0;
        for (char[] anA : a) {
            sum = sum + anA.length;
        }
        char[] result = new char[sum];
        for (char[] anA : a) {
            int lengthOne = anA.length;
            if (lengthOne == 0) {
                continue;
            }
            // 拷贝数组
            System.arraycopy(anA, 0, result, index, lengthOne);
            index = index + lengthOne;
        }
        return result;
    }

    /**
     * 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）&拼接成字符
     *
     * @param paramsMap
     * @return
     */
    public static String sortMap(Map<String, String> paramsMap) {

        List<Map.Entry<String, String>> infoIds = new ArrayList<>(paramsMap.entrySet());
        infoIds.sort(Comparator.comparing(Map.Entry::getKey));
        // 构造URL 键值对的格式
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> item : infoIds) {
            if (StringUtils.isNotBlank(item.getKey())) {
                String key = item.getKey();
                String val = item.getValue();
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
     * 生成微信支付签名
     * 将map按照（ASCII码从小到大排序）字典序列排序-,生成sign-微信支付
     *
     * @param paraMap
     * @return
     */
    public static String getWxPaySign(Map<String, String> paraMap, String merKey) {
        String sortedMap = sortMap(paraMap);
        // 拼接api密钥
        sortedMap += "&key=" + merKey;
        return DigestUtils.md5Hex(sortedMap).toUpperCase();
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
     * 邮箱验证
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 随机生成32位字符串
     *
     * @return 32位字符串
     */
    public static String getRandomStr() {
        return getRandomStr(32);
    }

    /**
     * 随机生成指定长度位字符串
     *
     * @return 指定长度位字符串
     */
    public static String getRandomStr(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
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
            InputStream inStream = null;
            inStream = request.getInputStream();
            int bufferSize = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[bufferSize];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, bufferSize)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                requestStr = new String(outStream.toByteArray(), "UTF-8");
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

}
