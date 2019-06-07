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
     * 检查 bucket 是否存在
     * @param bucketId bucket id
     * @return BucketInfo
     */
    BucketInfo checkBucketExist(String bucketId);
}
