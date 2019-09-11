package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.dao.entity.RegionInfo;
import com.berry.oss.dao.mapper.RegionInfoMapper;
import com.berry.oss.dao.service.IRegionInfoDaoService;
import com.berry.oss.module.dto.ServerListDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
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
    public List<ServerListDTO> getServerListByRegionIdLimit(String regionId, Integer limit) {
        return mapper.getServerListByRegionIdLimit(regionId, limit);
    }
}
