package com.berry.oss.quartz;

import com.berry.oss.common.utils.CsvUtils;
import com.berry.oss.quartz.job.NonReferenceObjectCleanJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Title QuartzConfig
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/4/28 16:02
 */
@Configuration
public class QuartzConfig {

    @Autowired
    private JobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }


    @Bean
    public JobDetail nonRefObjectCleanJob() {
        return JobBuilder.newJob(NonReferenceObjectCleanJob.class).withIdentity("non_ref_clean_job", "sys_job").storeDurably().build();
    }

    @Bean
    public Trigger nonRefObjectCleanJobTrigger() {
        //cron方式 每周末 0点0分开始
        return TriggerBuilder.newTrigger().forJob(nonRefObjectCleanJob())
                .withIdentity("non_ref_clean_job_trigger", "sys_trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(CsvUtils.getCornByName("non_ref_clean_task"))).build();
    }
}
