package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.core.entity.PolicyInfo;
import com.berry.oss.core.service.IPolicyInfoDaoService;
import com.berry.oss.module.vo.PolicyListVo;
import com.berry.oss.service.IBucketService;
import com.berry.oss.service.IPolicyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Title PolicyServiceImpl
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/16 15:05
 */
@Service
public class PolicyServiceImpl implements IPolicyService {

    private final IBucketService bucketService;
    private final IPolicyInfoDaoService policyInfoDaoService;

    PolicyServiceImpl(IBucketService bucketService, IPolicyInfoDaoService policyInfoDaoService) {
        this.bucketService = bucketService;
        this.policyInfoDaoService = policyInfoDaoService;
    }

    @Override
    public boolean addPolicy(String bucket, Integer actionType, List<String> principal, List<String> resource) {
        if (!CommonConstant.ActionType.checkByCode(actionType)) {
            throw new BaseException("403", "非法授权类型!");
        }
        bucketService.checkUserHaveBucket(bucket);
        String effect = "Allow";
        if (actionType == CommonConstant.ActionType.DENY.getCode()) {
            effect = "Deny";
        }
        PolicyInfo policyInfo = new PolicyInfo()
                .setBucket(bucket)
                .setActionType(actionType)
                .setPrincipal(String.join(",", principal))
                .setResource(String.join(",", resource))
                .setEffect(effect);
        return policyInfoDaoService.save(policyInfo);
    }

    @Override
    public List<PolicyListVo> getPolicy(String bucket) {
        List<PolicyInfo> policyInfoList = policyInfoDaoService.list(new QueryWrapper<PolicyInfo>().eq("bucket", bucket));
        List<PolicyListVo> voList = new ArrayList<>();
        policyInfoList.forEach(policy -> {
            PolicyListVo vo = new PolicyListVo();
            BeanUtils.copyProperties(policy, vo);
            vo.setPrincipal(Arrays.asList(policy.getPrincipal().split(",")));
            vo.setResource(Arrays.asList(policy.getResource().split(",")));
            voList.add(vo);
        });
        return voList;
    }

    @Override
    public Boolean deletePolicy(String bucket, String policyIds) {
        bucketService.checkUserHaveBucket(bucket);
        return policyInfoDaoService.remove(new QueryWrapper<PolicyInfo>().in("id", Arrays.asList(policyIds.split(","))));
    }
}
