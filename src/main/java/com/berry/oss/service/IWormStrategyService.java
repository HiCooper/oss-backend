package com.berry.oss.service;

import com.berry.oss.dao.entity.WormStrategy;

import java.util.Date;

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

    /**
     * 锁定策略
     * @param id 策略id
     */
    void completeWormStrategy(String id);

    /**
     * 策略详情
     * @param id 策略id
     * @return info
     */
    WormStrategy detailWormStrategy(String id);

    /**
     * 策略延期
     * @param id 策略id
     * @param days 延期天数
     */
    void extendWormStrategyDays(String id, int days);

    /**
     * 对象是否锁定(如果在锁定期限， 对象不能被删除， 不能被编辑)
     * @param bucket object 所属 bucket
     * @param objectCreateTime object 创建时间
     */
    boolean isLocked(String bucket, Date objectCreateTime);
}
