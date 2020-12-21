package com.berry.oss.quartz.dynamic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.dao.entity.WormStrategy;
import com.berry.oss.dao.service.IWormStrategyDaoService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/21 13:57
 * fileName：WormCheckJob
 * Use：
 */
@Component
public class WormCheckJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(WormCheckJob.class);

    @PostConstruct
    public void init() {
        System.out.println("WormCheckJob init");
    }

    // TODO null
    @Autowired
    private IWormStrategyDaoService wormStrategyDaoService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        String jobName = jobDetail.getKey().getName();
        logger.info("=== WORM 检查任务开始执行【{}】 ===", jobName);

        String bucket = jobDetail.getJobDataMap().getString("bucket");
        WormStrategy wormStrategy = wormStrategyDaoService.getOne(new QueryWrapper<WormStrategy>()
                .eq("bucket", bucket)
                .eq("worm_state", CommonConstant.WormState.InProgress.name())
        );
        if (wormStrategy == null) {
            logger.info("策略：【{}】已提交", jobName);
            return;
        }
        wormStrategy.setWormState(CommonConstant.WormState.Expired.name());
        wormStrategyDaoService.save(wormStrategy);
        logger.info("策略：【{}】使失效成功", jobName);
    }
}
