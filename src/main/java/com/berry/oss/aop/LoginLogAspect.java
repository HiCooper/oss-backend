package com.berry.oss.aop;

import com.berry.oss.security.vm.LoginVM;
import com.berry.oss.service.impl.AsyncExecuteService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/12/3 17:18
 * fileName：LoginLog
 * Use：
 */
@Aspect
@Component
public class LoginLogAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private AsyncExecuteService asyncExecuteService;

    @Before("execution(* com.berry.oss.api.AuthController.authorize(..))")
    public void getObjectStatistics(JoinPoint joinPoint) {
        LoginVM args = (LoginVM) joinPoint.getArgs()[0];
        logger.debug("record login log info ...");
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("user-agent");
            String username = args.getUsername();
            asyncExecuteService.recordLoginLog(username, ip, userAgent);
        }
    }
}
