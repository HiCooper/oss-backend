package com.berry.oss.config;

import com.berry.oss.security.interceptor.AccessInterceptor;
import com.berry.oss.security.interceptor.AccessProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2018-05-03 15:42
 * fileName：WebMvcConfig
 * Use：
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessProvider accessProvider;

    public WebMvcConfig(AccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessInterceptor(accessProvider))
                .addPathPatterns("/ajax/**")
                .excludePathPatterns("/index.html", "/", "/auth/login", "/swagger-ui.html");
    }
}