package com.berry.oss.security.access;

import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.utils.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-29 16:21
 * fileName：AccessFilter
 * Use：
 */
public class AccessFilter extends GenericFilterBean {

    private final AccessProvider accessProvider;

    public AccessFilter(AccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestUrl = httpServletRequest.getRequestURI();
        if (!requestUrl.equals(Constants.HEALTH_CHECK_URL) && !requestUrl.equals(Constants.ERROR_STATE_URL)) {
            String accessToken = ((HttpServletRequest) request).getHeader(Constants.ACCESS_TOKEN_KEY);
            if (StringUtils.isNotBlank(accessToken)) {
                Authentication authentication = this.accessProvider.getAuthentication(accessToken);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
