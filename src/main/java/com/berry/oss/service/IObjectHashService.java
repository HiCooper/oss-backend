package com.berry.oss.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 21:04
 * fileName：IObjectHashService
 * Use：
 */
public interface IObjectHashService {
    /**
     * 检查该文件是否已存在于系统中
     * 有且仅有一个 是正确，否则均为异常
     *
     * @param hash          hash
     * @return fileId or null
     */
    String checkExist(String hash);

    /**
     * 增加 哈希引用计算, 记录可能不存在，所以需要 fileId  size 进行创建时初始化
     * 不能异步， 否则批量创建是，hash引用将会出错
     *
     * @param hash   hash值
     * @param fileId fileId
     * @param size   size
     */
    void increaseRefCountByHash(String hash, String fileId, Long size);

    /**
     * 减少 哈希引用计算
     *
     * @param hash
     */
    void decreaseRefCountByHash(String hash);

    /**
     * 引用计数 批量 -1
     * @param hashList hash 集合
     */
    void batchDecreaseRefCountByHash(List<String> hashList);

    /**
     * 空引用对象数据清理
     */
    void scanNonReferenceObjectThenClean();
}
