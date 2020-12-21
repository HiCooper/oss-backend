package com.berry.oss.service.impl;

import com.berry.oss.service.IQuartzService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/21 12:19
 * fileName：QuartzServiceImpl
 * Use：
 */
@Service
public class QuartzServiceImpl implements IQuartzService {
    private static final String JOB_GROUP_NAME = "WORM_CHECK_JOB_GROUP";
    private static final String TRIGGER_GROUP_NAME = "WORM_CHECK_TRIGGER_GROUP";
    /**
     * Quartz调度器
     */
    private Scheduler scheduler;

    /**
     * 初始化调度器
     * @throws SchedulerException e
     */
    @PostConstruct
    public void init() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
    }

    /**
     * 添加一个定时任务，使用默认的任务组名、触发器组名，触发器的名称跟任务的一样
     */
    @Override
    public void addJob(String jobName, Class<? extends Job> cls, String cron, Map<String, String> dataMap) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(cls)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .storeDurably().build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, TRIGGER_GROUP_NAME)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            dataMap.forEach((key, val) -> {
                jobDetail.getJobDataMap().put(key, val);
            });
            scheduler.scheduleJob(jobDetail, trigger);
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个定时任务 使用默认的任务组名、触发器组名，触发器名跟任务名一致
     *
     * @param jobName 任务名称
     */
    @Override
    public void removeJob(String jobName) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
            // 暂停触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
