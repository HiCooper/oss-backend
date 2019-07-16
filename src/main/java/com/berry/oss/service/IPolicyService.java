package com.berry.oss.service;

import com.berry.oss.module.vo.PolicyListVo;

import java.util.List;

/**
 * Title IPolicyService
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/16 15:05
 */
public interface IPolicyService {
    /**
     * 新增授权策略
     *
     * @param bucket     bucket name
     * @param actionType 授权类型
     * @param principal  被授权用户
     * @param resource   授权资源
     * @return 成功与否
     */
    boolean addPolicy(String bucket, Integer actionType, List<String> principal, List<String> resource);

    /**
     * 获取 bucket 的 授权策略
     *
     * @param bucket bucket name
     * @return data
     */
    List<PolicyListVo> getPolicy(String bucket);

    /**
     * 删除策略
     *
     * @param bucket   bucket name
     * @param policyIds 策略id
     * @return 成功与否
     */
    Boolean deletePolicy(String bucket, String policyIds);
}
