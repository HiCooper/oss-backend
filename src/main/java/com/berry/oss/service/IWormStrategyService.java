package com.berry.oss.service;

import com.berry.oss.dao.entity.WormStrategy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/21 11:10
 * fileName：IWormStrategyService
 * Use：
 */
public interface IWormStrategyService {
    /**
     * 初始化创建
     * @param bucket bucket name
     * @param days 期限
     * @return detail
     */
    WormStrategy initWormStrategy(String bucket, Integer days);
}
