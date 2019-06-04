package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * Title ObjectController
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/4 15:34
 */
@RestController
@RequestMapping("api/object")
public class ObjectController {

    @GetMapping("list")
    @ApiOperation("获取 Object 列表")
    public Result list(){
        return ResultFactory.wrapper();
    }

    @PostMapping("create")
    @ApiOperation("创建对象")
    public Result create(){
        return ResultFactory.wrapper();
    }

    @GetMapping("detail")
    @ApiOperation("获取对象详情")
    public Result detail(){
        return ResultFactory.wrapper();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除对象")
    public Result delete(){
        return ResultFactory.wrapper();
    }

    @PutMapping("updateObjectAcl")
    @ApiOperation("更新对象读写权限")
    public Result updateObjectAcl(){
        return ResultFactory.wrapper();
    }
}
