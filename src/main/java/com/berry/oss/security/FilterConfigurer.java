package com.berry.oss.security;

import com.berry.oss.security.access.AccessFilter;
import com.berry.oss.security.access.AccessProvider;
import com.berry.oss.security.jwt.JwtFilter;
import com.berry.oss.security.jwt.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author xueancao
 */
public class FilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final AccessProvider accessProvider;

    public FilterConfigurer(TokenProvider tokenProvider, AccessProvider accessProvider) {
        this.tokenProvider = tokenProvider;
        this.accessProvider = accessProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        AccessFilter accessFilter = new AccessFilter(accessProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(accessFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
