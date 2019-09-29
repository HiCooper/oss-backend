package com.berry.oss.quartz;

import com.berry.oss.common.utils.CsvUtils;
import com.berry.oss.quartz.job.NonReferenceObjectCleanJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Title QuartzConfig
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/4/28 16:02
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail nonsupportRuleSaveJob() {
        return JobBuilder.newJob(NonReferenceObjectCleanJob.class).withIdentity("non_ref_clean_job", "sys_job").storeDurably().build();
    }

    @Bean
    public Trigger nonsupportRuleSaveJobTrigger() {
        //cron方式
        return TriggerBuilder.newTrigger().forJob(nonsupportRuleSaveJob())
                .withIdentity("non_ref_clean_job_trigger", "sys_trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(CsvUtils.getCornByName("non_ref_clean_task"))).build();
    }
}
