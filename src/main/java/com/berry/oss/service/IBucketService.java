package com.berry.oss.service;

import com.berry.oss.core.entity.BucketInfo;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:31
 * fileName：IBucketService
 * Use：
 */
public interface IBucketService {

    /**
     * 检查 bucket name 是否存在
     *
     * @param bucketName bucket name
     * @return
     */
    BucketInfo checkBucketExist(String bucketName);

    /**
     * 检查该 bucket 不存在
     *
     * @param bucketName bucketName
     * @return
     */
    Boolean checkBucketNotExist(String bucketName);
}
