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

    private static final String DENY = "DENY";

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
        String targetResource = bucket + objectPath;

        // 获取该 bucket 授权策略
        List<PolicyListVo> policy = policyService.getPolicy(bucket);
        boolean result = false;
        for (PolicyListVo item : policy) {
            // 遍历该用户相关的所有策略，DENY 优先级最高
            List<String> principal = item.getPrincipal();
            if (principal.stream().findAny().filter(name -> name.equals(user.getUsername())).orElse(null) != null) {
                List<String> resource = item.getResource();
                String effect = item.getEffect();
                if (effect.equals(DENY)) {
                    if (!checkNotDeny(resource, targetResource)) {
                        // 拒绝访问，直接终止判断，返回 false
                        return false;
                    }
                } else {
                    boolean access = checkHaveAccess(resource, targetResource);
                    if (!result && access) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private static boolean checkNotDeny(List<String> resource, String targetResource) {
        // 任何一个 授权策略都不匹配 该目标对象，否则视为拒绝访问
        return resource.stream().noneMatch(pattern -> {
            if (pattern.endsWith("*")) {
                pattern = pattern.replace("*", ".*");
                return targetResource.matches(pattern);
            } else {
                return pattern.equals(targetResource);
            }
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
        // 任意一个 授权策略匹配 该目标对象即可，视为可访问
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
