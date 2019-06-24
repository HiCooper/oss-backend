package com.berry.oss.core.mapper;

import com.berry.oss.core.entity.RegionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.berry.oss.module.dto.ServerListDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-24
 */
public interface RegionInfoMapper extends BaseMapper<RegionInfo> {

    List<ServerListDTO> getServerListByRegionId(@Param("regionId") String regionId);
}
