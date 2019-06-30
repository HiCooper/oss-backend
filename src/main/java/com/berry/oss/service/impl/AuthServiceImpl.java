package com.berry.oss.service.impl;

import com.berry.oss.service.IAuthService;
import com.berry.oss.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-30 14:05
 * fileName：AuthServiceImpl
 * Use：
 */
@Service
public class AuthServiceImpl implements IAuthService {

    private final IBucketService bucketService;

    @Autowired
    public AuthServiceImpl(IBucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Override
    public Boolean checkUserHaveAccessToBucket(Integer userId, String bucket) {
        return bucketService.checkUserHaveBucket(userId, bucket);
    }
}
