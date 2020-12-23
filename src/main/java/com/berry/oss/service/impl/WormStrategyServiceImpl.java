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
import com.berry.oss.service.IBucketService;
import com.berry.oss.service.IWormStrategyService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private IBucketService bucketService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WormStrategy initWormStrategy(String bucket, Integer days) {
        // 检查 bucket 是否存在
        bucketService.checkUserHaveBucket(bucket);
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

    @Override
    public void completeWormStrategy(String id) {
        WormStrategy wormStrategy = wormStrategyDaoService.getById(id);
        if (wormStrategy == null) {
            throw new BaseException("404", "策略不存在");
        }
        bucketService.checkUserHaveBucket(wormStrategy.getBucket());
        wormStrategy.setWormState(CommonConstant.WormState.Locked.name());
        try {
            quartzJobManagement.removeJob(wormStrategy.getBucket());
            wormStrategyDaoService.updateById(wormStrategy);
        } catch (Exception e) {
            logger.error("error msg: {}", e.getMessage());
            throw new BaseException("500", "操作失败");
        }
    }

    @Override
    public WormStrategy detailWormStrategy(String id) {
        WormStrategy wormStrategy = wormStrategyDaoService.getById(id);
        if (wormStrategy == null) {
            throw new BaseException("404", "策略不存在");
        }
        bucketService.checkUserHaveBucket(wormStrategy.getBucket());
        return wormStrategy;
    }

    @Override
    public void extendWormStrategyDays(String id, int days) {
        if (days == 0) {
            return;
        }
        WormStrategy wormStrategy = wormStrategyDaoService.getById(id);
        if (wormStrategy == null) {
            throw new BaseException("404", "策略不存在");
        }
        bucketService.checkUserHaveBucket(wormStrategy.getBucket());
        if (!CommonConstant.WormState.Locked.name().equals(wormStrategy.getWormState())) {
            throw new BaseException("409", "策略尚未生效不可延期");
        }
        wormStrategy.setRetentionPeriodValue(wormStrategy.getRetentionPeriodValue() + days);
        String extendDesc = DateUtils.getDateTime() + "延期" + days + "天";
        wormStrategy.setRetentionPeriodDesc(wormStrategy.getRetentionPeriodDesc() + "," + extendDesc);
        wormStrategy.setUpdateTime(new Date());
        wormStrategyDaoService.updateById(wormStrategy);
    }

    @Override
    public boolean isLocked(String bucket, Date objectCreateTime) {
        WormStrategy wormStrategy = wormStrategyDaoService.getOne(
                new QueryWrapper<WormStrategy>()
                        .eq("bucket", bucket)
                        .eq("worm_state", CommonConstant.WormState.Locked.name())
        );
        if (wormStrategy == null) {
            return false;
        }
        DateTime dateTime = new DateTime(objectCreateTime);
        return dateTime.plusDays(wormStrategy.getRetentionPeriodValue()).isAfter(DateTime.now());
    }
}
