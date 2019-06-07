package com.berry.oss.service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:33
 * fileName：IObjectService
 * Use：
 */
public interface IObjectService {

    /**
     * 保存对象信息
     * @param bucketId bucketId
     * @param acl 读写权限
     * @param hash hash
     * @param contentLength  文件大小
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param fileId 文件di
     * @return
     */
    Boolean saveObjectInfo(String bucketId, String acl, String hash, Long contentLength, String fileName, String filePath, String fileId);
}
