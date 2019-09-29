package com.berry.oss.task;

import com.berry.oss.service.IObjectHashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/9/29 9:20
 * fileName：NonReferenceObjectCleanTask
 * Use：空引用对象数据清理
 */
@EnableScheduling
@Component
public class NonReferenceObjectCleanTask {
    private static final Logger logger = LoggerFactory.getLogger(NonReferenceObjectCleanTask.class);


    @Autowired
    private IObjectHashService hashService;


    /**
     * 每周一 0 点 开始任务
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    private void configureTasks() {
        logger.info("空引用对象数据清理定时任务开始执行...");
        hashService.scanNonReferenceObjectThenClean();
    }
}
