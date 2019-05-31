package com.berry.oss.aop.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.Arrays;

/**
 * @author xueancao
 */
@Aspect
public class LogMethodAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Environment env;

    /**
     * 请求响应耗时警告阈值
     */
    private static final int RESPONSE_THRESHOLD_VALUE = 100;

    public LogMethodAspect(Environment env) {
        this.env = env;
    }

    /**
     * 默认在注解 @RestController下方法 和 有 @LogMethodExecutionInfo 的方法上有效
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || @annotation(LogMethodExecutionInfo)")
    public void restController() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * 环绕通知
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around(value = "restController()")
    public Object logMethodExecutionInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("请求方法: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            logger.debug("请求参数: {}", Arrays.toString(joinPoint.getArgs()));
        }
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            if (logger.isDebugEnabled()) {
                logger.debug("返回结果: {}", result);
            }
            if (executionTime > RESPONSE_THRESHOLD_VALUE) {
                logger.warn("执行耗时: " + executionTime + " ms");
            }
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }

    }

    /**
     * 通知异常
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "execution(* com.berry.*.*(..))", throwing = "e")
    public void logThrow(JoinPoint joinPoint, Throwable e) {
        if (env.acceptsProfiles(Profiles.of("dev"))) {
            logger.error("Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);

        } else {
            logger.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
        }
    }

}

