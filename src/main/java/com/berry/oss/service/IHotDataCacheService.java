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
     * @param objectFileId objectFileId
     * @return 输入流
     * @throws IOException
     */
    InputStream getObjectIsByObjectId(String objectFileId) throws IOException;

    /**
     * 更新 对象 热点数据score
     *
     * @param objectFileId 对象FileID
     */
    void updateRankKeyScore(String objectFileId);

    /**
     * 设置更新 对象缓存
     *
     * @param objectFileId objectId
     * @param data     data
     */
    void trySetObject(String objectFileId, byte[] data);
}
