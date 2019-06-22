package com.berry.oss.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.module.vo.BucketInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-04
 */
public interface BucketInfoMapper extends BaseMapper<BucketInfo> {

    List<BucketInfoVo> listBucket(@Param("userId") Integer userId, @Param("name") String name);

}
