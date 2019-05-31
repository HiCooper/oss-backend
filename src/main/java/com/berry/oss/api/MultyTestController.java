package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.CaptchaUtils;
import com.berry.oss.common.utils.ExcelUtil;
import com.berry.oss.security.AuthoritiesConstants;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.jwt.JwtFilter;
import com.berry.oss.security.jwt.TokenProvider;
import com.berry.oss.security.vm.LoginVM;
import com.berry.oss.service.MailService;
import com.berry.oss.service.MailTestUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Berry_Cooper.
 * Description:
 * Date: 2018/05/03
 * fileName MultyTestController
 */
@RestController
@RequestMapping("api")
@Api(value = "测试", tags = "测试接口")
public class MultyTestController {


    @Resource
    private MailService mailService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;


    @GetMapping("sendEmail")
    @ApiOperation(value = "发送邮件测试", httpMethod = "GET")
    public Result sendEmail() {
        MailTestUser user = new MailTestUser();
        user.setEmail("294237781@qq.com");
        user.setActivated(true);
        user.setActivationKey("100keypppp");
        user.setLangKey("zh-ch");
        user.setLogin("admin");
        mailService.sendActivationEmail(user);
        mailService.sendCreationEmail(user);
        return ResultFactory.wrapper();
    }

    @GetMapping("test12")
    public Result test() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserJWT();
        System.out.println(SecurityUtils.isAuthenticated());
        return ResultFactory.wrapper(currentUserLogin);
    }

    @GetMapping("getCurrentUser")
    public Object getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object myUser = (auth != null) ? auth.getPrincipal() : null;

        return myUser;
    }

    @GetMapping("baseException")
    public Result baseException() {
        throw new BaseException("9999", "自定义异常");
    }

    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @PostMapping("importExcel")
    @ApiOperation(value = "导入excel", httpMethod = "POST")
    public Result importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        // 设置excel字段属性名
        Map<String, String> formatJsonMap = new HashMap<>(16);
        formatJsonMap.put("昵称", "nickName");
        formatJsonMap.put("手机号", "phone");
        formatJsonMap.put("性别", "gender");
        // 解析excel
        List<Map<String, Object>> result = ExcelUtil.parseExcel(file, formatJsonMap);

        return ResultFactory.wrapper(result);
    }

    @ApiOperation(value = "图片验证码", httpMethod = "GET")
    @GetMapping("captcha")
    public void getCaptchaImg(HttpServletRequest request, HttpServletResponse response) {
        CaptchaUtils.generate(response, "uy9so1");
    }

    @PostMapping("/authorize")
    @ApiOperation("获取授权")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVM.getRememberMe() == null) ? false : loginVM.getRememberMe();
            String jwt = this.tokenProvider.createAndSignToken(authentication, rememberMe);
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
            return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
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
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
