package com.berry.oss.security.filter;

import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.utils.NetworkUtils;
import com.berry.oss.security.interceptor.AccessProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static io.micrometer.core.instrument.util.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * 请求过滤器，如果请求头信息包含 'authorization' 验证token通过后 添加 安全凭证
 *
 * @author xueancao
 */
public class AuthFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    public static final String AUTHORIZATION_HEADER = "authorization";

    private TokenProvider tokenProvider;

    private AccessProvider accessProvider;

    public AuthFilter(TokenProvider tokenProvider, AccessProvider accessProvider) {
        this.tokenProvider = tokenProvider;
        this.accessProvider = accessProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestUrl = httpServletRequest.getRequestURI();
        if (Constants.WRITE_LIST.stream().noneMatch(requestUrl::matches)) {
            String jwt = resolveToken(httpServletRequest);
            if (isNotBlank(jwt) && this.tokenProvider.validateToken(jwt)) {
                // 验证jwt 设置授权信息到该线程上下文
                Authentication authentication = this.tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                String ip = NetworkUtils.getRequestIpAddress(httpServletRequest);
                String ossAuth = getOssAuthToken(httpServletRequest);
                if (isNotBlank(ossAuth)) {
                    // 只验证 token 格式 尝试设置用户信息
                    Authentication authentication = this.accessProvider.getSdkAuthentication(ossAuth);
                    if (authentication != null) {
                        log.info("IP:{} 通过 sdk 授权初步验证", ip);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } else {
                    // 上传 token 拦截器
                    String accessToken = httpServletRequest.getHeader(Constants.ACCESS_TOKEN_KEY);
                    if (isNotBlank(accessToken)) {
                        // 只验证 token 格式 尝试设置用户信息
                        Authentication authentication = this.accessProvider.getUploadAuthentication(accessToken);
                        if (authentication != null) {
                            log.info("IP:{} 通过 upload token 授权初步验证", ip);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getOssAuthToken(HttpServletRequest request) {
        // 优先从url参数获取，再从请求头获取
        String token = request.getParameter("token");
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        return request.getHeader(Constants.OSS_SDK_AUTH_HEAD_NAME);
    }

    /**
     * 获取token，如果请求头中没有，则在cookie中获取
     *
     * @param request request
     * @return token or null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (isNotBlank(bearerToken)) {
            return bearerToken;
        } else {
            Cookie cookie = getCookie(request);
            if (cookie != null) {
                // Validate that the cookie is used at the correct place.
                String path = trimToNull(cookie.getPath());
                if (path != null && !pathMatches(path, request.getRequestURI())) {
                    log.warn("Found '{}' cookie at path '{}', but should be only used for '{}'", AUTHORIZATION_HEADER, request.getRequestURI(), path);
                } else {
                    bearerToken = cookie.getValue();
                    log.debug("Found '{}' cookie value [{}]", AUTHORIZATION_HEADER, bearerToken);
                    return bearerToken;
                }
            } else {
                log.debug("No '{}' cookie value", AUTHORIZATION_HEADER);
            }
        }

        return null;
    }

    /**
     * 获取cookie
     *
     * @param request request
     * @return Cookie
     */
    private static Cookie getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * Check whether the given {@code cookiePath} matches the {@code requestPath}
     *
     * @param cookiePath  cookie路径
     * @param requestPath 请求路径
     * @return boolean
     * @see <a href="https://tools.ietf.org/html/rfc6265#section-5.1.4">RFC 6265, Section 5.1.4 "Paths and Path-Match"</a>
     */
    private boolean pathMatches(String cookiePath, String requestPath) {
        if (!requestPath.startsWith(cookiePath)) {
            return false;
        }

        return requestPath.length() == cookiePath.length()
                || cookiePath.charAt(cookiePath.length() - 1) == '/'
                || requestPath.charAt(cookiePath.length()) == '/';
    }
}
