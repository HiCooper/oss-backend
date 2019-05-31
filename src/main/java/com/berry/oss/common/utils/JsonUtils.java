package com.berry.oss.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setSerializationInclusion(Include.NON_NULL);
        JSON.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
    }

    public static String toJson(Object obj) {
        try {
            return JSON.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 使用阿里巴巴的fast json 将对象转换成json字符串工具类
     * <p>
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String obj2json(Object obj) throws Exception {
        return com.alibaba.fastjson.JSON.toJSONString(obj);
    }


    /**
     * 使用阿里巴巴的fast json 将json字符串转换成对象工具类
     * <p>
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T json2obj(String jsonStr, Class<T> clazz) throws Exception {
        return com.alibaba.fastjson.JSON.parseObject(jsonStr, clazz);
    }


    /**
     * 使用阿里巴巴的fast json 将json转换成Map工具类
     * <p>
     *
     * @param jsonStr
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> json2map(String jsonStr) throws Exception {
        return com.alibaba.fastjson.JSON.parseObject(jsonStr, Map.class);
    }


    /**
     * 使用阿里巴巴的fast json 将Map转换成对象工具类
     * <p>
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T map2obj(Map<?, ?> map, Class<T> clazz) throws Exception {
        return com.alibaba.fastjson.JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(map), clazz);
    }
}
