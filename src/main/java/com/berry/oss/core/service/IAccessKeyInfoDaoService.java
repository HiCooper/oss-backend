package com.berry.oss.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.berry.oss.core.entity.AccessKeyInfo;
import com.berry.oss.security.dto.UserInfoDTO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-26
 */
public interface IAccessKeyInfoDaoService extends IService<AccessKeyInfo> {

    UserInfoDTO getUserInfoDTO(String accessKeyId);
}
