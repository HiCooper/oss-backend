package com.berry.oss.security.interceptor;

import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-30 14:54
 * fileName：AccessInterceptor
 * Use：
 */
public class AccessInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    private final AccessProvider accessProvider;

    public AccessInterceptor(AccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IllegalAccessException, UnsupportedEncodingException {
        String requestUrl = request.getRequestURI();
        if (Constants.WRITE_LIST.stream().noneMatch(requestUrl::matches)) {
            // sdk 通用请求拦截器,sdk token 验证后将不验证 upload token
            String ossAuth = getOssAuthToken(request);
            String ip = NetworkUtils.getRequestIpAddress(request);
            if (isNotBlank(ossAuth)) {
                // 验证 token 合法性
                this.accessProvider.validateSdkAuthentication(request);
                logger.info("IP:{} 通过 sdk 授权认证", ip);
            } else {
                // 上传 token 拦截器
                String accessToken = request.getHeader(Constants.ACCESS_TOKEN_KEY);
                if (isNotBlank(accessToken)) {
                    // 验证 token 合法性
                    this.accessProvider.validateUploadAuthentication(requestUrl);
                    logger.info("IP:{} 通过 upload token 授权认证", ip);
                }
            }
        }
        return true;
    }

    private String getOssAuthToken(HttpServletRequest request) {
        // 优先从url参数获取，再从请求头获取
        String token = request.getParameter("token");
        if (isNotBlank(token)) {
            return token;
        }
        return request.getHeader(Constants.OSS_SDK_AUTH_HEAD_NAME);
    }
}
