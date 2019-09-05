package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.security.core.entity.User;
import com.berry.oss.security.core.service.IUserDaoService;
import com.berry.oss.security.filter.AuthFilter;
import com.berry.oss.security.filter.TokenProvider;
import com.berry.oss.security.vm.LoginVM;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("auth")
@Api(value = "授权", tags = "授权")
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUserDaoService userDaoService;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    @ApiOperation("登录")
    public ResponseEntity<LoginSuccessVo> authorize(@Valid @RequestBody LoginVM loginVm, HttpServletResponse response) {
        // 用户是否存在
        User user = userDaoService.getOne(new QueryWrapper<User>().eq("username", loginVm.getUsername()));
        if (user == null) {
            throw new BaseException(ResultCode.ACCOUNT_NOT_EXIST);
        }
        // 密码是否正确
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVm.getUsername(), loginVm.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVm.getRememberMe() == null) ? false : loginVm.getRememberMe();
            String jwt = this.tokenProvider.createAndSignToken(authentication, user.getId(), rememberMe);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AuthFilter.AUTHORIZATION_HEADER, jwt);
            long expires = TokenProvider.TOKEN_VALIDITY_IN_MILLISECONDS / 1000;
            if (rememberMe) {
                expires = TokenProvider.TOKEN_VALIDITY_IN_MILLISECONDS_FOR_REMEMBER_ME / 1000;
            }
            Cookie cookie = new Cookie(AuthFilter.AUTHORIZATION_HEADER, jwt);
            cookie.setMaxAge(Integer.parseInt(String.valueOf(expires)));
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            httpHeaders.add("expires", String.valueOf(expires));
            return new ResponseEntity<>(new LoginSuccessVo(jwt, expires, new UserInfo(user.getId(), user.getUsername())), httpHeaders, HttpStatus.OK);
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

    /**
     * Object to return as body in JWT Authentication.
     */
    @Data
    static class LoginSuccessVo {

        private String token;

        private long expires;

        private UserInfo userInfo;

        LoginSuccessVo(String token, long expires, UserInfo userInfo) {
            this.token = token;
            this.expires = expires;
            this.userInfo = userInfo;
        }

    }

    @Data
    private static class UserInfo {
        private Integer id;
        private String username;

        UserInfo(Integer id, String username) {
            this.id = id;
            this.username = username;
        }
    }
}
