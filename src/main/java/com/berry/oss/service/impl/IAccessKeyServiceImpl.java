package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.core.entity.AccessKeyInfo;
import com.berry.oss.core.service.IAccessKeyInfoDaoService;
import com.berry.oss.module.vo.CreateAccessKeyVo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.core.entity.User;
import com.berry.oss.security.core.service.IUserDaoService;
import com.berry.oss.security.dto.UserInfoDTO;
import com.berry.oss.service.IAccessKeyService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Title IAccessKeyServiceImpl
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/28 9:28
 */
@Service
public class IAccessKeyServiceImpl implements IAccessKeyService {

    @Resource
    private IUserDaoService userDaoService;

    private final IAccessKeyInfoDaoService accessKeyInfoDaoService;

    public IAccessKeyServiceImpl(IAccessKeyInfoDaoService accessKeyInfoDaoService) {
        this.accessKeyInfoDaoService = accessKeyInfoDaoService;
    }

    @Override
    public List<AccessKeyInfo> listAccessKey() {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        return accessKeyInfoDaoService.list(new QueryWrapper<AccessKeyInfo>().eq("user_id", currentUser.getId()));
    }

    @Override
    public CreateAccessKeyVo generateAccessKey(String password) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        User user = userDaoService.findOneByUsername(currentUser.getUsername()).orElse(null);
        if (user == null) {
            throw new BaseException(ResultCode.ACCOUNT_NOT_EXIST);
        }
        // 密码比对
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BaseException(ResultCode.BAD_PASSWORD);
        }

        // 校验该用户已创建的密钥对是否已经超过3个
        int accessKeyCount = accessKeyInfoDaoService.count(new QueryWrapper<AccessKeyInfo>().eq("user_id", user.getId()));
        if (accessKeyCount >= Constants.SINGLE_ACCOUNT_ACCESS_KEY_PAIR_MAX) {
            throw new BaseException(ResultCode.ACCESS_KEY_LIMIT_THREE);
        }

        // 密码校验通过
        String accessKeyId = StringUtils.getRandomStr(22);
        String accessKeySecret = StringUtils.getRandomStr(31);

        AccessKeyInfo accessKeyInfo = new AccessKeyInfo()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setState(true)
                .setUserId(user.getId());

        accessKeyInfoDaoService.save(accessKeyInfo);
        return new CreateAccessKeyVo(accessKeyId, accessKeySecret);
    }

    @Override
    public void disableAccessKey(String accessKeyId) {
        AccessKeyInfo accessKeyInfo = checkAccessKeyExist(accessKeyId);
        if (accessKeyInfo.getState()) {
            accessKeyInfo.setState(false);
            accessKeyInfoDaoService.updateById(accessKeyInfo);
        }
    }

    @Override
    public void enableAccessKey(String accessKeyId) {
        AccessKeyInfo accessKeyInfo = checkAccessKeyExist(accessKeyId);
        if (!accessKeyInfo.getState()) {
            accessKeyInfo.setState(true);
            accessKeyInfoDaoService.updateById(accessKeyInfo);
        }
    }

    @Override
    public void deleteAccessKey(String accessKeyId) {
        AccessKeyInfo accessKeyInfo = checkAccessKeyExist(accessKeyId);
        accessKeyInfoDaoService.removeById(accessKeyInfo.getAccessKeyId());
    }

    private AccessKeyInfo checkAccessKeyExist(String accessKeyId) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        AccessKeyInfo accessKeyInfo = accessKeyInfoDaoService.getOne(new QueryWrapper<AccessKeyInfo>()
                .eq("user_id", currentUser.getId())
                .eq("access_key_id", accessKeyId));
        if (accessKeyInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        return accessKeyInfo;
    }
}
