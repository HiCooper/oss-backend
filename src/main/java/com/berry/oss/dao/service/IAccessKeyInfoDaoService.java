package com.berry.oss.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.berry.oss.dao.entity.AccessKeyInfo;
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
