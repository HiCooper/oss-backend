package com.berry.oss.security.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.security.core.entity.Role;
import com.berry.oss.security.core.entity.User;
import com.berry.oss.security.core.mapper.UserMapper;
import com.berry.oss.security.core.service.IUserDaoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2018-12-02
 */
@Service
public class UserDaoServiceImpl extends ServiceImpl<UserMapper, User> implements IUserDaoService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Optional<User> findOneByUsername(String lowercaseLogin) {
        return Optional.ofNullable(userMapper.selectOne(new QueryWrapper<User>().eq("username", lowercaseLogin)));
    }

    @Override
    public Set<Role> findRoleListByUserId(Integer userId) {
        return userMapper.getRolesByUserId(userId);
    }
}
