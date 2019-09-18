package com.berry.oss.security.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.security.dao.entity.UserRole;
import com.berry.oss.security.dao.mapper.UserRoleMapper;
import com.berry.oss.security.dao.service.IUserRoleDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联关系 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-09-11
 */
@Service
public class UserRoleDaoServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleDaoService {

}
