package com.berry.oss.service;

import com.berry.oss.core.entity.AccessKeyInfo;
import com.berry.oss.module.vo.CreateAccessKeyVo;

import java.util.List;

/**
 * Title IAccessKeyService
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/28 9:28
 */
public interface IAccessKeyService {

    /**
     * 获取 密钥对
     *
     * @return 列表
     */
    List<AccessKeyInfo> listAccessKey();

    /**
     * 生成 密钥对
     *
     * @param password 账户登录密码
     * @return 新的密钥对
     */
    CreateAccessKeyVo generateAccessKey(String password);

    /**
     * 禁用 密钥对
     *
     * @param accessKeyId 密钥 keyId
     */
    void disableAccessKey(String accessKeyId);

    /**
     * 启用 密钥对
     *
     * @param accessKeyId 密钥 keyId
     */
    void enableAccessKey(String accessKeyId);

    /**
     * 删除 密钥对
     *
     * @param accessKeyId 密钥 keyId
     */
    void deleteAccessKey(String accessKeyId);
}
