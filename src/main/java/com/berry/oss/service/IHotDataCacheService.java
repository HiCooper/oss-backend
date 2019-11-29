package com.berry.oss.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2019/11/29 14:41
 * fileName：IHotDataCacheService
 * Use：
 */
public interface IHotDataCacheService {

    /**
     * 根据 对象全路径获取 对象输入流
     *
     * @param objectId 对象id
     * @return
     */
    InputStream getObjectIsByObjectId(String objectId) throws IOException;

    /**
     * 设置更新 对象缓存
     *
     * @param objectId    对象id
     * @param inputStream 数据流
     */
    void setObject(String objectId, InputStream inputStream) throws IOException;
}
