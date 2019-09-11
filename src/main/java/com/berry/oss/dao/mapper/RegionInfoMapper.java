package com.berry.oss.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.berry.oss.dao.entity.RegionInfo;
import com.berry.oss.module.dto.ServerListDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-24
 */
public interface RegionInfoMapper extends BaseMapper<RegionInfo> {

    List<ServerListDTO> getServerListByRegionIdLimit(@Param("regionId") String regionId, @Param("limit") Integer limit);
}
