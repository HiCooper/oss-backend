package com.berry.oss.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.module.dto.BucketStatisticsInfoDto;
import com.berry.oss.module.vo.BucketInfoVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-04
 */
public interface IBucketInfoDaoService extends IService<BucketInfo> {

    /**
     * 获取bucket 列表
     *
     * @param userId 用户id
     * @param name   名称（非必填）
     * @return
     */
    List<BucketInfoVo> listBucket(Long userId, String name);

    List<BucketStatisticsInfoDto> getBucketUseInfo(Long userId);
}
