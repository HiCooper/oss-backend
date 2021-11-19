package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 存储模式调整
 * @author HiCooper
 * @since 2021-11-18
 */
@RestController
@RequestMapping("/ajax/store_mode")
@Api(tags = "存储模式")
public class StoreModelController {
    
    @ApiOperation("新建存储模式")
    @PostMapping("/init")
    public Result initStoreModel() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("取消未锁定的存储模式")
    @PostMapping("/abort")
    public Result abortStoreModel() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("锁定存储模式")
    @PostMapping("/complete")
    public Result completeStoreModel() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("获取存储模式")
    @GetMapping("/detail")
    public Result detailStoreModel(@RequestParam("id") String id) {
        return ResultFactory.wrapper();
    }

    @ApiOperation("延长Object的保留天数")
    @PostMapping("/extend_days")
    public Result extendStoreModelDays() {
        return ResultFactory.wrapper();
    }

}
