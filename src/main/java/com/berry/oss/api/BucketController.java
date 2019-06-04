package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.module.CreateBucketMo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Title BucketController
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/4 14:56
 */
@RestController
@RequestMapping("api/bucket")
public class BucketController {

    @GetMapping("list")
    public Result list(){
        return ResultFactory.wrapper();
    }

    @PostMapping("create")
    public Result create(@Validated @RequestBody CreateBucketMo mo){
        System.out.println("创建 Bucket :" + mo.toString());
        return ResultFactory.wrapper();
    }

    @GetMapping("detail")
    public Result detail(@RequestParam String id){
        System.out.println("详情：" + id);
        return ResultFactory.wrapper();
    }

    @DeleteMapping("delete")
    public Result delete(@RequestParam String id){
        System.out.println("删除" + id);
        return ResultFactory.wrapper();
    }
}
