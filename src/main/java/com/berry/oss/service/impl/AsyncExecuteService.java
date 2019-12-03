package com.berry.oss.service.impl;

import com.berry.oss.dao.entity.LoginLogInfo;
import com.berry.oss.dao.service.ILoginLogInfoDaoService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/12/3 17:56
 * fileName：AsyncExcuteService
 * Use：
 */
@Service
public class AsyncExecuteService {

    private final ILoginLogInfoDaoService loginLogInfoDaoService;

    public AsyncExecuteService(ILoginLogInfoDaoService loginLogInfoDaoService) {
        this.loginLogInfoDaoService = loginLogInfoDaoService;
    }

    @Async
    public void recordLoginLog(String username, String ip, String userAgent) {
        LoginLogInfo info = new LoginLogInfo();
        info.setUsername(username);
        info.setIp(ip);
        info.setUserAgent(userAgent);
        info.setLoginTime(new Date());
        loginLogInfoDaoService.save(info);
    }
}
