package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.service.IBucketInfoDaoService;
import com.berry.oss.module.mo.CreateBucketMo;
import com.berry.oss.module.mo.DeleteBucketMo;
import com.berry.oss.module.mo.UpdateBucketAclMo;
import com.berry.oss.module.mo.UpdateRefererMo;
import com.berry.oss.module.vo.BucketInfoVo;
import com.berry.oss.module.vo.RefererDetailVo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.dto.UserInfoDTO;
import com.berry.oss.service.IBucketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Title BucketController
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/4 14:56
 */
@RestController
@RequestMapping("ajax/bucket")
@Api(tags = "存储空间 Bucket 管理")
public class BucketController {

    private final IBucketInfoDaoService bucketInfoDaoService;

    private final IBucketService bucketService;

    @Autowired
    public BucketController(IBucketInfoDaoService bucketInfoDaoService, IBucketService bucketService) {
        this.bucketInfoDaoService = bucketInfoDaoService;
        this.bucketService = bucketService;
    }

    @GetMapping("list.json")
    @ApiOperation("获取 Bucket 列表")
    public Result<List<BucketInfoVo>> list(@RequestParam(name = "name", required = false) String name) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        return ResultFactory.wrapper(bucketService.listBucket(currentUser.getId(), name));
    }

    @PostMapping("new_create_bucket.json")
    @ApiOperation("创建 Bucket")
    public Result create(@Validated @RequestBody CreateBucketMo mo) {
        String acl = mo.getAcl();
        if (!CommonConstant.AclType.ALL_NAME.contains(acl)) {
            throw new BaseException("403", "非法ACL");
        }
        bucketService.create(mo.getName(), mo.getRegion(), mo.getAcl());
        return ResultFactory.wrapper();
    }

    @PostMapping("delete_bucket.json")
    @ApiOperation("删除 Bucket")
    public Result delete(@Validated @RequestBody DeleteBucketMo mo) {
        // 检查该 Bucket
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(mo.getBucket());
        return ResultFactory.wrapper(bucketInfoDaoService.removeById(bucketInfo.getId()));
    }

    @PostMapping("set_acl.json")
    @ApiOperation("更新 Bucket 读写权限")
    public Result updateBucketAcl(@Validated @RequestBody UpdateBucketAclMo mo) {
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(mo.getBucket());
        bucketInfo.setAcl(mo.getAcl());
        return ResultFactory.wrapper(bucketInfoDaoService.updateById(bucketInfo));
    }

    @GetMapping("get_referer.json")
    @ApiOperation("获取 Bucket 防盗链设置")
    public Result getReferer(@RequestParam("bucket") String bucket) {
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(bucket);
        RefererDetailVo detailVo = bucketService.getReferer(bucketInfo.getId());
        return ResultFactory.wrapper(detailVo);
    }

    @PostMapping("update_referer.json")
    @ApiOperation("更新 Bucket 防盗链设置")
    public Result updateReferer(@Validated @RequestBody UpdateRefererMo updateRefererMo) {
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(updateRefererMo.getBucket());
        bucketService.updateReferer(bucketInfo.getId(), updateRefererMo.getId(), updateRefererMo.getAllowEmpty(), updateRefererMo.getWhiteList());
        return ResultFactory.wrapper();
    }

    @ApiOperation("Bucket 概览")
    @GetMapping("overview.json")
    public Result getBucketOverview() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("30天热点数据")
    @GetMapping("get_last_thirty_day_hot_data.json")
    public Result getLastThirtyDayHotData() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("30天文件访问统计")
    @GetMapping("get_last_thirty_day_file_access_data.json")
    public Result getLastThirtyDayFileAccessData() {
        return ResultFactory.wrapper();
    }
}
