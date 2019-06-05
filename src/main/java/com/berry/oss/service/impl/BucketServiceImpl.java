package com.berry.oss.service.impl;

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
    public boolean checkBucketExist(String bucketId) {
        return bucketInfoDaoService.getById(bucketId) != null;
    }
}
