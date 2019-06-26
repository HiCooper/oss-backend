package com.berry.oss.api;


import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.module.mo.CreateAccessKeyMo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.vm.UserInfoDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.Security;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-26
 */
@RestController
@RequestMapping("ajax/access_key")
public class AccessKeyInfoController {

    @Resource
    private AuthenticationManager authenticationManager;


    @PostMapping("create_access_key.json")
    public Result generateAccessKey(@Validated @RequestBody CreateAccessKeyMo mo) {
        // 验证当前用户密码是否正确
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser.getUsername(), mo.getPassword());
            this.authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            throw new BaseException(ResultCode.BAD_PASSWORD);
        }
        // 密钥
        String accessKeySecret = StringUtils.getRandomStr(32);
        System.out.println(accessKeySecret);
//        String accessKeyId =
        return ResultFactory.wrapper();
    }

}
