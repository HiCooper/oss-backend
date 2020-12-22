package com.berry.oss.quartz.dynamic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.dao.entity.WormStrategy;
import com.berry.oss.dao.service.IWormStrategyDaoService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
public class WormCheckJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(WormCheckJob.class);

    @Autowired
    private IWormStrategyDaoService wormStrategyDaoService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
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
        wormStrategyDaoService.updateById(wormStrategy);
        logger.info("策略：【{}】使失效成功", jobName);
    }
}
