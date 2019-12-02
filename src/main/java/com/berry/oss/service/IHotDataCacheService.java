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
     * @param objectId objectId
     * @return 输入流
     * @throws IOException
     */
    InputStream getObjectIsByObjectId(String objectId) throws IOException;

    /**
     * 设置更新 对象缓存
     *
     * @param objectId    objectId
     * @param data data
     * @throws IOException
     */
    void trySetObject(String objectId, byte[] data) throws IOException;
}
