package com.berry.oss.service;

import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.module.vo.BucketInfoVo;

import java.util.List;

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
     * 获取 bucket 列表
     *
     * @param userId 用户id
     * @param name   全模糊搜索名字
     * @return list
     */
    List<BucketInfoVo> listBucket(Integer userId, String name);

    /**
     * 创建 bucket
     *
     * @param name   bucket 名称
     * @param region 区域code
     * @param acl    acl权限
     */
    void create(String name, String region, String acl);

    /**
     * 检查 当前用户 bucket 是否存在
     *
     * @param bucketName bucket name
     * @return info
     */
    BucketInfo checkUserHaveBucket(String bucketName);

    /**
     * 检查该 bucket 不存在
     *
     * @param bucketName bucketName
     * @return boolean
     */
    Boolean checkBucketNotExist(String bucketName);

    /**
     * 检查用户是否拥有该 bucket
     *
     * @param userId 用户id
     * @param bucket bucket name
     * @return true or false
     */
    Boolean checkUserHaveBucket(Integer userId, String bucket);
}
