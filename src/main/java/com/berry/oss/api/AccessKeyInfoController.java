package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.module.mo.CreateAccessKeyMo;
import com.berry.oss.module.mo.UpdateAccessKeyMo;
import com.berry.oss.service.IAccessKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@Api(tags = "密钥信息")
public class AccessKeyInfoController {

    private final IAccessKeyService accessKeyService;

    public AccessKeyInfoController(IAccessKeyService accessKeyService) {
        this.accessKeyService = accessKeyService;
    }

    @PostMapping("create_access_key.json")
    @ApiOperation("生成 accessKey 密钥对")
    public Result generateAccessKey(@Validated @RequestBody CreateAccessKeyMo mo) {
        return ResultFactory.wrapper(accessKeyService.generateAccessKey(mo.getPassword()));
    }

    @GetMapping("list.json")
    @ApiOperation("获取用户 accessKey 密钥对列表")
    public Result listAccessKey() {
        return ResultFactory.wrapper(accessKeyService.listAccessKey());
    }

    @PostMapping("disable_access_key.json")
    @ApiOperation("禁用 accessKey 密钥对")
    public Result disableAccessKey(@RequestBody UpdateAccessKeyMo mo) {
        accessKeyService.disableAccessKey(mo.getAccessKeyId());
        return ResultFactory.wrapper();
    }

    @PostMapping("enable_access_key.json")
    @ApiOperation("启用 accessKey 密钥对")
    public Result enableAccessKey(@RequestBody UpdateAccessKeyMo mo) {
        accessKeyService.enableAccessKey(mo.getAccessKeyId());
        return ResultFactory.wrapper();
    }

    @PostMapping("delete_access_key.json")
    @ApiOperation("删除 accessKey 密钥对")
    public Result deleteAccessKey(@RequestBody UpdateAccessKeyMo mo) {
        accessKeyService.deleteAccessKey(mo.getAccessKeyId());
        return ResultFactory.wrapper();
    }
}
