package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.DateUtils;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.entity.WormStrategy;
import com.berry.oss.dao.service.IBucketInfoDaoService;
import com.berry.oss.dao.service.IWormStrategyDaoService;
import com.berry.oss.quartz.QuartzJobManagement;
import com.berry.oss.quartz.dynamic.WormCheckJob;
import com.berry.oss.security.filter.TokenProvider;
import com.berry.oss.service.IWormStrategyService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/21 11:10
 * fileName：WormStrategyServiceImpl
 * Use：
 */
@Service
public class WormStrategyServiceImpl implements IWormStrategyService {
    private final static Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Resource
    private QuartzJobManagement quartzJobManagement;

    @Autowired
    private IBucketInfoDaoService bucketInfoDaoService;

    @Autowired
    private IWormStrategyDaoService wormStrategyDaoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WormStrategy initWormStrategy(String bucket, Integer days) {
        // 检查 bucket 是否存在
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>().eq("name", bucket));
        if (bucketInfo == null) {
            throw new BaseException("404", "bucket does not exist!");
        }
        // 检查 bucket 是否已设置 worm
        Set<String> wormState = new HashSet<>();
        wormState.add("InProgress");
        wormState.add("Locked");
        int wormStrategyCount = wormStrategyDaoService.count(
                new QueryWrapper<WormStrategy>()
                        .eq("bucket", bucket)
                        .in("worm_state", wormState)
        );
        if (wormStrategyCount != 0) {
            throw new BaseException("403", "存在正在生效或未提交锁定的策略");
        }
        // 设置 worm
        WormStrategy wormStrategy = new WormStrategy();
        wormStrategy.setId(IdWorker.getIdStr());
        wormStrategy.setBucket(bucket);
        wormStrategy.setRetentionPeriodValue(days);
        wormStrategy.setRetentionPeriodDesc(days + "天");
        // 初始化状态：InProgress （等待提交锁定）
        wormStrategy.setWormState(CommonConstant.WormState.InProgress.name());
        wormStrategy.setCreateTime(new Date());
        // 策略生效开始时间（在此之前 状态未提交锁定(Locked)，策略自动失效：Expired）
        Date activeTime = DateTime.now().plusHours(24).toDate();
        wormStrategy.setActiveTime(activeTime);
        wormStrategyDaoService.save(wormStrategy);
        // 启动一个定时任务， 在 activeTime 检查状态， 如果不为 Locked ，则设置为 Expired

        Map<String, String> dataMap = new HashMap<>(16);
        dataMap.put("bucket", bucket);
        quartzJobManagement.addJob(bucket, WormCheckJob.class, DateUtils.getCron(activeTime), dataMap);
        logger.info("策略：FOR【{}】初始化完成", bucket);
        return wormStrategy;
    }
}
