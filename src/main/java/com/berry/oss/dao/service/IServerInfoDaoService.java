package com.berry.oss.dao.service;

import com.berry.oss.dao.entity.ServerInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务器信息 服务类
 * </p>
 *
 * @author HiCooper
 * @since 2020-06-09
 */
public interface IServerInfoDaoService extends IService<ServerInfo> {

    List<ServerInfo> listServerListByRegion(String regionId);
}
