package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.security.AuthoritiesConstants;
import com.berry.oss.security.core.entity.User;
import com.berry.oss.security.core.service.IUserDaoService;
import com.berry.oss.security.jwt.JwtFilter;
import com.berry.oss.security.jwt.TokenProvider;
import com.berry.oss.security.vm.LoginVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Berry_Cooper.
 * Description:
 * Date: 2018/05/03
 * fileName MultyTestController
 */
@RestController
@RequestMapping("api")
@Api(value = "授权", tags = "授权")
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUserDaoService userDaoService;

    @Autowired
    private TokenProvider tokenProvider;

    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @GetMapping("getCurrentUserLogin")
    public Object getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @PostMapping("/login")
    @ApiOperation("登录")
    public ResponseEntity<JwtToken> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletResponse response) {
        // 用户是否存在
        User user = userDaoService.getOne(new QueryWrapper<User>().eq("username", loginVM.getUsername()));
        if (user == null) {
            throw new BaseException(ResultCode.ACCOUNT_NOT_EXIST);
        }
        // 密码是否正确
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVM.getRememberMe() == null) ? false : loginVM.getRememberMe();
            String jwt = this.tokenProvider.createAndSignToken(authentication, user.getId(), rememberMe);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, jwt);
            Cookie cookie = new Cookie(JwtFilter.AUTHORIZATION_HEADER, jwt);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            long expires = TokenProvider.TOKEN_VALIDITY_IN_MILLISECONDS / 1000;
            if (rememberMe) {
                expires = TokenProvider.TOKEN_VALIDITY_IN_MILLISECONDS_FOR_REMEMBER_ME / 1000;
            }
            httpHeaders.add("expires", String.valueOf(expires));
            return new ResponseEntity<>(new JwtToken(jwt, expires), httpHeaders, HttpStatus.OK);
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                throw new BaseException(ResultCode.ACCOUNT_DISABLE);
            } else if (e instanceof LockedException) {
                throw new BaseException(ResultCode.ACCOUNT_LOCKED);
            } else if (e instanceof BadCredentialsException) {
                throw new BaseException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
            }

        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    // 创建用户使用此方式设置密码
//    public static void main(String[] args) {
//        System.out.println(new BCryptPasswordEncoder().encode("123456"));
//    }

    /**
     * Object to return as body in JWT Authentication.
     */
    @Data
    static class JwtToken {

        private String idToken;

        private long expires;

        JwtToken(String idToken, long expires) {
            this.idToken = idToken;
            this.expires = expires;
        }
    }
}
