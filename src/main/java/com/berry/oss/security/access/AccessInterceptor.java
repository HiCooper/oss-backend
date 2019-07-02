package com.berry.oss.security.access;

import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.utils.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-30 14:54
 * fileName：AccessInterceptor
 * Use：
 */
public class AccessInterceptor implements HandlerInterceptor {

    private final AccessProvider accessProvider;

    public AccessInterceptor(AccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IllegalAccessException {
        String requestUrl = request.getRequestURI();
        if (!requestUrl.equals(Constants.HEALTH_CHECK_URL) && !requestUrl.equals(Constants.ERROR_STATE_URL)) {
            // sdk 通用请求拦截器,sdk token 验证后将不验证 upload token
            String ossAuth = request.getHeader(Constants.OSS_SDK_AUTH_HEAD_NAME);
            if (StringUtils.isNotBlank(ossAuth)) {
                Authentication authentication = this.accessProvider.getSdkAuthentication(ossAuth, request);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                // 上传 token 拦截器
                String accessToken = request.getHeader(Constants.ACCESS_TOKEN_KEY);
                if (StringUtils.isNotBlank(accessToken)) {
                    Authentication authentication = this.accessProvider.getUploadAuthentication(accessToken, requestUrl);
                    if (authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        return true;
    }
}
