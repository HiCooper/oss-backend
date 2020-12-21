package com.berry.oss.service;

import org.quartz.Job;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/21 12:18
 * fileName：IQuartzService
 * Use：
 */
public interface IQuartzService {

    /**
     * 新增定时任务
     *
     * @param jobName 任务名称
     * @param cls     任务执行类
     * @param cron    cron 表达式
     * @param data    JobDetail -> JobDataMap
     */
    void addJob(String jobName, Class<? extends Job> cls, String cron, Map<String, String> data);

    /**
     * 删除定时任务
     *
     * @param jobName 任务名称
     */
    void removeJob(String jobName);
}
