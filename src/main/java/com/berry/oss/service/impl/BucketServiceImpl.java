package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.service.IBucketInfoDaoService;
import com.berry.oss.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:33
 * fileName：BucketServiceImpl
 * Use：
 */
@Service
public class BucketServiceImpl implements IBucketService {

    private final IBucketInfoDaoService bucketInfoDaoService;

    @Autowired
    public BucketServiceImpl(IBucketInfoDaoService bucketInfoDaoService) {
        this.bucketInfoDaoService = bucketInfoDaoService;
    }

    @Override
    public BucketInfo checkBucketExist(String bucketId) {
        BucketInfo bucketInfo = bucketInfoDaoService.getById(bucketId);
        if (null == bucketInfo) {
            throw new BaseException(ResultCode.BUCKET_NOT_EXIST);
        }
        return bucketInfo;
    }

    @Override
    public BucketInfo checkBucketExist(Integer userId, String bucketName) {
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>().eq("user_id", userId).eq("name", bucketName));
        if (null == bucketInfo) {
            throw new BaseException(ResultCode.BUCKET_NOT_EXIST);
        }
        return bucketInfo;
    }
}
