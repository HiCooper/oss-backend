package com.berry.oss.quartz.job;

import com.berry.oss.service.IObjectHashService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Title NonsupportRuleSaveJob
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/25 16:57
 */
public class NonReferenceObjectCleanJob extends QuartzJobBean {

    private static Logger logger = LoggerFactory.getLogger(NonReferenceObjectCleanJob.class);

    @Autowired
    private IObjectHashService hashService;

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("空引用对象数据清理定时任务开始执行...");
        hashService.scanNonReferenceObjectThenClean();
        logger.info("空引用对象数据清理定时任务结束.");
    }
}
