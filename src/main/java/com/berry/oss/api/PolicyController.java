package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.module.mo.AddPolicyMo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Title PolicyController
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/15 14:09
 */
@RestController
@RequestMapping("ajax/bucket/policy")
@Api(tags = "Bucket 授权策略")
public class PolicyController {

    @ApiOperation("新增授权")
    @PostMapping("add_policy.json")
    public Result addPolicy(@Validated @RequestBody AddPolicyMo mo) {
        Integer actionType = mo.getActionType();
        if (!CommonConstant.ActionType.checkByCode(actionType)) {
            throw new BaseException("403", "非法授权类型!");
        }
        return ResultFactory.wrapper();
    }

    @ApiOperation("获取授权列表")
    @PostMapping("get_policy.json")
    public Result getPolicy(){
        return ResultFactory.wrapper();
    }
}
