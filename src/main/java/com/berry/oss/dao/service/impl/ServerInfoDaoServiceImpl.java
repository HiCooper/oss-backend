package com.berry.oss.dao.service.impl;

import com.berry.oss.dao.entity.ServerInfo;
import com.berry.oss.dao.mapper.ServerInfoMapper;
import com.berry.oss.dao.service.IServerInfoDaoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务器信息 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2020-06-09
 */
@Service
public class ServerInfoDaoServiceImpl extends ServiceImpl<ServerInfoMapper, ServerInfo> implements IServerInfoDaoService {

    @Resource
    private ServerInfoMapper mapper;

    @Override
    public List<ServerInfo> listServerListByRegion(String regionId) {

        return null;
    }
}


