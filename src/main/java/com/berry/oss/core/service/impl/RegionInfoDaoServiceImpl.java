package com.berry.oss.core.service.impl;

import com.berry.oss.core.entity.RegionInfo;
import com.berry.oss.core.mapper.RegionInfoMapper;
import com.berry.oss.core.service.IRegionInfoDaoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.module.dto.ServerListDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-24
 */
@Service
public class RegionInfoDaoServiceImpl extends ServiceImpl<RegionInfoMapper, RegionInfo> implements IRegionInfoDaoService {

    @Resource
    private RegionInfoMapper mapper;

    @Override
    public List<ServerListDTO> getServerListByRegionId(String regionId) {
        return mapper.getServerListByRegionId(regionId);
    }
}
