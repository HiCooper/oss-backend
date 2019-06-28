package com.berry.oss.service;

import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.module.vo.BucketInfoVo;

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
     * 创建 bucket
     *
     * @param name   bucket 名称
     * @param region 区域code
     * @param acl    acl权限
     */
    void create(String name, String region, String acl);

    /**
     * bucket 基本信息
     *
     * @param name bucket 名称
     * @return
     */
    BucketInfoVo detail(String name);

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
