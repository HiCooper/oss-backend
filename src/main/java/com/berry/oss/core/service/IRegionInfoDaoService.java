package com.berry.oss.core.service;

import com.berry.oss.core.entity.RegionInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.berry.oss.module.dto.ServerListDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-24
 */
public interface IRegionInfoDaoService extends IService<RegionInfo> {

    /**
     * 根据 regionId 后去6个可用服务器列表
     * @param regionId
     * @return
     */
    List<ServerListDTO> getServerListByRegionId(String regionId);
}
