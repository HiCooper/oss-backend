package com.berry.oss.security.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.berry.oss.security.core.entity.Role;
import com.berry.oss.security.core.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HiCooper
 * @since 2018-12-02
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户id获取角色
     *
     * @param userId 用户id
     * @return
     */
    Set<Role> getRolesByUserId(@Param("userId") Integer userId);
}
