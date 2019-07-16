package com.berry.oss.service.impl;

import com.berry.oss.module.vo.PolicyListVo;
import com.berry.oss.security.dto.UserInfoDTO;
import com.berry.oss.service.IAuthService;
import com.berry.oss.service.IBucketService;
import com.berry.oss.service.IPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final IPolicyService policyService;

    @Autowired
    public AuthServiceImpl(IBucketService bucketService, IPolicyService policyService) {
        this.bucketService = bucketService;
        this.policyService = policyService;
    }

    @Override
    public Boolean checkUserHaveAccessToBucketObject(UserInfoDTO user, String bucket, String objectPath) {
        if (bucketService.checkUserHaveBucket(user.getId(), bucket)) {
            // bucket 拥有者
            return true;
        }
        // cooper/test/*
        // cooper/tesat/a.jpg
        String targetResource = bucket + objectPath;

        // 获取该 bucket 授权策略
        List<PolicyListVo> policy = policyService.getPolicy(bucket);
        return policy.stream().anyMatch(item -> {
            List<String> principal = item.getPrincipal();
            if (principal.stream().findAny().filter(name -> name.equals(user.getUsername())).orElse(null) != null) {
                List<String> resource = item.getResource();
                return checkHaveAccess(resource, targetResource);
            }
            return false;
        });
    }

    /**
     * 检查目标资源 是否在授权范围内
     *
     * @param resourcePatterns 授权策略列表
     *                         eg.
     *                         cooper/*
     *                         cooper/test/*
     * @param targetResource   目标资源
     *                         eg.
     *                         cooper/test/timg.png
     * @return true or false
     */
    private static boolean checkHaveAccess(List<String> resourcePatterns, String targetResource) {
        return resourcePatterns.stream().anyMatch(pattern -> {
            if (pattern.endsWith("*")) {
                pattern = pattern.replace("*", ".*");
                return targetResource.matches(pattern);
            } else {
                return pattern.equals(targetResource);
            }
        });
    }
}
