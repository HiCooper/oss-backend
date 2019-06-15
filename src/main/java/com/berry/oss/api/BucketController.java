package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IBucketInfoDaoService;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.module.mo.CreateBucketMo;
import com.berry.oss.module.mo.UpdateBucketAclMo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.vm.UserInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = "Bucket 管理")
public class BucketController {

    private final IBucketInfoDaoService bucketInfoDaoService;

    private final IObjectInfoDaoService objectInfoDaoService;

    @Autowired
    public BucketController(IBucketInfoDaoService bucketInfoDaoService, IObjectInfoDaoService objectInfoDaoService) {
        this.bucketInfoDaoService = bucketInfoDaoService;
        this.objectInfoDaoService = objectInfoDaoService;
    }

    @GetMapping("list")
    @ApiOperation("获取 Bucket 列表")
    public Result list(@RequestParam(required = false) String name) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        return ResultFactory.wrapper(bucketInfoDaoService.listBucket(currentUser.getId(), name));
    }

    @PostMapping("create")
    @ApiOperation("创建 Bucket")
    public Result create(@Validated @RequestBody CreateBucketMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = new BucketInfo();
        BeanUtils.copyProperties(mo, bucketInfo);
        bucketInfo.setUserId(currentUser.getId());
        bucketInfoDaoService.save(bucketInfo);
        return ResultFactory.wrapper();
    }

    @GetMapping("detail")
    @ApiOperation("获取 Bucket 详情")
    public Result detail(@RequestParam("name") String name) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        return ResultFactory.wrapper(bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>().eq("user_id", currentUser.getId()).eq("name", name)));
    }

    @DeleteMapping("delete")
    @ApiOperation("删除 Bucket")
    public Result delete(@RequestParam String bucketId) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        // 检查该 Bucket 是否为空，非空 Bucket 不能删除
        int count = objectInfoDaoService.count(new QueryWrapper<ObjectInfo>().eq("bucket_id", bucketId).eq("user_id", currentUser.getId()));
        if (count != 0) {
            throw new BaseException("403", "Bucket 内容不为空，不能删除");
        }
        return ResultFactory.wrapper(bucketInfoDaoService.removeById(bucketId));
    }

    @PutMapping("updateBucketAcl")
    @ApiOperation("更新 Bucket 读写权限")
    public Result updateBucketAcl(@Validated @RequestBody UpdateBucketAclMo mo) {
        BucketInfo bucketInfo = bucketInfoDaoService.getById(mo.getBucketId());
        if (bucketInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        bucketInfo.setAcl(mo.getNewAcl());
        return ResultFactory.wrapper(bucketInfoDaoService.updateById(bucketInfo));
    }
}
