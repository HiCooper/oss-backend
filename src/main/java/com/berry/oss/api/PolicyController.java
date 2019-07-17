package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.module.mo.AddPolicyMo;
import com.berry.oss.module.vo.PolicyListVo;
import com.berry.oss.service.IPolicyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Title PolicyController
 * Description
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/15 14:09
 */
@RestController
@RequestMapping("ajax/bucket/policy")
@Api(tags = "Bucket 授权策略")
public class PolicyController {

    private final IPolicyService policyService;

    public PolicyController(IPolicyService policyService) {
        this.policyService = policyService;
    }

    @ApiOperation("新增授权")
    @PostMapping("add_policy.json")
    public Result addPolicy(@Validated @RequestBody AddPolicyMo mo) {
        boolean result = policyService.addPolicy(mo.getBucket(), mo.getActionType(), mo.getPrincipal(), mo.getResource());
        return ResultFactory.wrapper(result);
    }

    @ApiOperation("获取授权列表")
    @GetMapping("get_policy.json")
    public Result getPolicy(@RequestParam String bucket) {
        List<PolicyListVo> vos = policyService.getPolicy(bucket);
        return ResultFactory.wrapper(vos);
    }

    @ApiOperation("获取授权列表")
    @PostMapping("delete_policy.json")
    public Result deletePolicy(@RequestParam String bucket, @RequestParam String policyIds) {
        Boolean result = policyService.deletePolicy(bucket, policyIds);
        return ResultFactory.wrapper(result);
    }
}
