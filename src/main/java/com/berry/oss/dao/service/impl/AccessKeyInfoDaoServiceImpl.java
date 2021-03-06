package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.dao.entity.AccessKeyInfo;
import com.berry.oss.dao.mapper.AccessKeyInfoMapper;
import com.berry.oss.dao.service.IAccessKeyInfoDaoService;
import com.berry.oss.security.dto.UserInfoDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-26
 */
@Service
public class AccessKeyInfoDaoServiceImpl extends ServiceImpl<AccessKeyInfoMapper, AccessKeyInfo> implements IAccessKeyInfoDaoService {

    @Resource
    private AccessKeyInfoMapper accessKeyInfoMapper;

    @Override
    public UserInfoDTO getUserInfoDTO(String accessKeyId) {
        return accessKeyInfoMapper.getUserInfoDTO(accessKeyId);
    }
}
