package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.dao.entity.LoginLogInfo;
import com.berry.oss.dao.mapper.LoginLogInfoMapper;
import com.berry.oss.dao.service.ILoginLogInfoDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-12-03
 */
@Service
public class LoginLogInfoDaoServiceImpl extends ServiceImpl<LoginLogInfoMapper, LoginLogInfo> implements ILoginLogInfoDaoService {

}
