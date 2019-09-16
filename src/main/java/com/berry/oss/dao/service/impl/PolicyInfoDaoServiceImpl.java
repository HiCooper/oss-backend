package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.dao.entity.PolicyInfo;
import com.berry.oss.dao.mapper.PolicyInfoMapper;
import com.berry.oss.dao.service.IPolicyInfoDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-07-15
 */
@Service
public class PolicyInfoDaoServiceImpl extends ServiceImpl<PolicyInfoMapper, PolicyInfo> implements IPolicyInfoDaoService {

}
