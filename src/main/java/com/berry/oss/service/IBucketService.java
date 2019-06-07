package com.berry.oss.service;

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
     * @return boolean
     */
    boolean checkBucketExist(String bucketId);
}
