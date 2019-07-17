package com.berry.oss.security;

import com.berry.oss.security.filter.AuthFilter;
import com.berry.oss.security.filter.TokenProvider;
import com.berry.oss.security.interceptor.AccessProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author xueancao
 */
public class FilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    private AccessProvider accessProvider;

    public FilterConfigurer(TokenProvider tokenProvider, AccessProvider accessProvider) {
        this.tokenProvider = tokenProvider;
        this.accessProvider = accessProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthFilter customFilter = new AuthFilter(tokenProvider, accessProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
