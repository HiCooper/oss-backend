package com.berry.oss.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.berry.oss.dao.entity.AccessKeyInfo;
import com.berry.oss.security.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-26
 */
public interface AccessKeyInfoMapper extends BaseMapper<AccessKeyInfo> {

    UserInfoDTO getUserInfoDTO(@Param("accessKeyId") String accessKeyId);
}
