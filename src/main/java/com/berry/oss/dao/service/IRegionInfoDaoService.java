package com.berry.oss.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.berry.oss.dao.entity.RegionInfo;
import com.berry.oss.module.dto.ServerListDTO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-24
 */
public interface IRegionInfoDaoService extends IService<RegionInfo> {

    /**
     * 根据 regionId 后去6个可用服务器列表
     *
     * @param regionId
     * @param limit
     * @return
     */
    List<ServerListDTO> getServerListByRegionIdLimit(String regionId, Integer limit);
}
