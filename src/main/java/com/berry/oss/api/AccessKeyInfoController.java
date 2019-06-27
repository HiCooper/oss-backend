package com.berry.oss.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.core.entity.AccessKeyInfo;
import com.berry.oss.core.service.IAccessKeyInfoDaoService;
import com.berry.oss.module.mo.CreateAccessKeyMo;
import com.berry.oss.module.mo.UpdateAccessKeyMo;
import com.berry.oss.module.vo.CreateAccessKeyVo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.core.entity.User;
import com.berry.oss.security.core.service.IUserDaoService;
import com.berry.oss.security.vm.UserInfoDTO;
import io.swagger.annotations.ApiOperation;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Security;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-26
 */
@RestController
@RequestMapping("ajax/access_key")
public class AccessKeyInfoController {

    @Resource
    private IUserDaoService userDaoService;

    @Autowired
    private IAccessKeyInfoDaoService accessKeyInfoDaoService;

    @PostMapping("create_access_key.json")
    @ApiOperation("生成 accessKey 密钥对")
    public Result generateAccessKey(@Validated @RequestBody CreateAccessKeyMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        User user = userDaoService.findOneByUsername(currentUser.getUsername()).orElse(null);
        if (user == null) {
            throw new BaseException(ResultCode.ACCOUNT_NOT_EXIST);
        }
        // 密码比对
        if (!BCrypt.checkpw(mo.getPassword(), user.getPassword())) {
            throw new BaseException(ResultCode.BAD_PASSWORD);
        }
        // 密码校验通过
        String salt = BCrypt.gensalt();
        String accessKeyId = salt.substring(7);
        String hash = BCrypt.hashpw(user.getUsername(), salt);
        String accessKeySecret = hash.substring(29);

        AccessKeyInfo accessKeyInfo = new AccessKeyInfo()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setState(true)
                .setUserId(user.getId());

        accessKeyInfoDaoService.save(accessKeyInfo);
        return ResultFactory.wrapper(new CreateAccessKeyVo(accessKeyId, accessKeySecret));
    }

    @GetMapping("list.json")
    @ApiOperation("获取用户 accessKey 密钥对列表")
    public Result listAccessKey() {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        List<AccessKeyInfo> list = accessKeyInfoDaoService.list(new QueryWrapper<AccessKeyInfo>().eq("user_id", currentUser.getId()));
        return ResultFactory.wrapper(list);
    }

    @PostMapping("disable_access_key.json")
    @ApiOperation("禁用 accessKey 密钥对")
    public Result disableAccessKey(@RequestBody UpdateAccessKeyMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        AccessKeyInfo accessKeyInfo = accessKeyInfoDaoService.getOne(new QueryWrapper<AccessKeyInfo>()
                .eq("user_id", currentUser.getId())
                .eq("access_key_id", mo.getAccessKeyId()));
        if (accessKeyInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        if (accessKeyInfo.getState()){
            accessKeyInfo.setState(false);
            accessKeyInfoDaoService.updateById(accessKeyInfo);
        }
        return ResultFactory.wrapper();
    }

    @PostMapping("enable_access_key.json")
    @ApiOperation("启用 accessKey 密钥对")
    public Result enableAccessKey(@RequestBody UpdateAccessKeyMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        AccessKeyInfo accessKeyInfo = accessKeyInfoDaoService.getOne(new QueryWrapper<AccessKeyInfo>()
                .eq("user_id", currentUser.getId())
                .eq("access_key_id", mo.getAccessKeyId()));
        if (accessKeyInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        if (!accessKeyInfo.getState()) {
            accessKeyInfo.setState(true);
            accessKeyInfoDaoService.updateById(accessKeyInfo);
        }
        return ResultFactory.wrapper();
    }

    @PostMapping("delete_access_key.json")
    @ApiOperation("删除 accessKey 密钥对")
    public Result deleteAccessKey(@RequestBody UpdateAccessKeyMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        AccessKeyInfo accessKeyInfo = accessKeyInfoDaoService.getOne(new QueryWrapper<AccessKeyInfo>()
                .eq("user_id", currentUser.getId())
                .eq("access_key_id", mo.getAccessKeyId()));
        if (accessKeyInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        return ResultFactory.wrapper(accessKeyInfoDaoService.removeById(accessKeyInfo.getAccessKeyId()));
    }
}
