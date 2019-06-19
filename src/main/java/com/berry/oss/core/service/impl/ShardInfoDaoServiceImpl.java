package com.berry.oss.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.core.entity.ShardInfo;
import com.berry.oss.core.mapper.ShardInfoMapper;
import com.berry.oss.core.service.IShardInfoDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-13
 */
@Service
public class ShardInfoDaoServiceImpl extends ServiceImpl<ShardInfoMapper, ShardInfo> implements IShardInfoDaoService {

}
