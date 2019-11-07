package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.dao.entity.RefererInfo;
import com.berry.oss.dao.mapper.RefererInfoMapper;
import com.berry.oss.dao.service.IRefererInfoDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 防盗链，http referer 白名单设置 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-09-24
 */
@Service
public class RefererInfoDaoServiceImpl extends ServiceImpl<RefererInfoMapper, RefererInfo> implements IRefererInfoDaoService {

}
