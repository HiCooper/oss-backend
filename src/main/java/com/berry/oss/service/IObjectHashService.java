package com.berry.oss.service;

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
     * @param contentLength 文件大小
     * @return fileId or null
     */
    String checkExist(String hash, Long contentLength);

    /**
     * 增加 哈希引用计算
     *
     * @param hash hash值
     * @param fileId fileId
     * @param size size
     * @return
     */
    Boolean increaseRefCountByHash(String hash, String fileId, Long size);

    /**
     * 减少 哈希引用计算
     *
     * @param hash
     * @return
     */
    Boolean decreaseRefCountByHash(String hash);
}
