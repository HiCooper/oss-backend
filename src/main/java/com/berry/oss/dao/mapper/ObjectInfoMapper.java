package com.berry.oss.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.berry.oss.dao.entity.ObjectInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-04
 */
public interface ObjectInfoMapper extends BaseMapper<ObjectInfo> {

    /**
     * 插入数据，如遇唯一索引等限制插入失败则会忽略跳过
     *
     * @param objectInfo 实体信息
     */
    void insertIgnore(@Param("objectInfo") ObjectInfo objectInfo);
}
