package com.berry.oss.aop.statis;

import com.berry.oss.service.IStatisticsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/10/27 15:19
 * fileName：StatisticsAspect
 * Use：
 */
@Aspect
@Component
public class StatisticsAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private IStatisticsService statisticsService;


    @Around("execution(* com.berry.oss.api.ObjectController.getObject(..))")
    public Object getObjectStatistics(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.debug("update bucket query statistics");
        Object result = joinPoint.proceed();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String url = request.getRequestURL().toString();
            String apiPrefix = "/ajax/bucket/file/";
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            int index = url.indexOf(apiPrefix) + apiPrefix.length();
            String bucket = url.substring(index, url.lastIndexOf(fileName) - 1);
            String fileFullPath = url.substring(url.lastIndexOf(bucket) + bucket.length() + 1);
            statisticsService.updateDailyStatistics(bucket, fileFullPath);
        }
        return result;
    }
}
