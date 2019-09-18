package com.berry.oss.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.berry.oss.dao.entity.ObjectInfo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-04
 */
public interface IObjectInfoDaoService extends IService<ObjectInfo> {

    /**
     * 批量忽略插入
     *
     * @param list list data
     */
    void insertIgnoreBatch(List<ObjectInfo> list);
}
