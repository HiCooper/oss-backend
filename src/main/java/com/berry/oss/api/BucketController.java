package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.RegionInfo;
import com.berry.oss.core.service.IBucketInfoDaoService;
import com.berry.oss.core.service.IRegionInfoDaoService;
import com.berry.oss.module.mo.CreateBucketMo;
import com.berry.oss.module.mo.DeleteBucketMo;
import com.berry.oss.module.mo.UpdateBucketAclMo;
import com.berry.oss.module.vo.BucketInfoVo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.vm.UserInfoDTO;
import com.berry.oss.service.IBucketService;
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
@RequestMapping("ajax/bucket")
@Api(tags = "Bucket 管理")
public class BucketController {

    private final IBucketInfoDaoService bucketInfoDaoService;

    private final IBucketService bucketService;

    private final IRegionInfoDaoService regionInfoDaoService;

    @Autowired
    public BucketController(IBucketInfoDaoService bucketInfoDaoService, IBucketService bucketService, IRegionInfoDaoService regionInfoDaoService) {
        this.bucketInfoDaoService = bucketInfoDaoService;
        this.bucketService = bucketService;
        this.regionInfoDaoService = regionInfoDaoService;
    }

    @GetMapping("list.json")
    @ApiOperation("获取 Bucket 列表")
    public Result list(@RequestParam(required = false) String name) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        return ResultFactory.wrapper(bucketInfoDaoService.listBucket(currentUser.getId(), name));
    }

    @PostMapping("new_create_bucket.json")
    @ApiOperation("创建 Bucket")
    public Result create(@Validated @RequestBody CreateBucketMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        // 检查 region
        RegionInfo regionInfo = regionInfoDaoService.getOne(new QueryWrapper<RegionInfo>().eq("code", mo.getRegion()));
        if (regionInfo == null) {
            throw new BaseException("404", "region 不存在");
        }

        // 检查该 bucket 名称是否被占用, 全局 bucket 命名唯一
        Boolean result = bucketService.checkBucketNotExist(mo.getName());
        if (!result) {
            throw new BaseException("403", "该Bucket名字已被占用");
        }
        BucketInfo bucketInfo = new BucketInfo();
        BeanUtils.copyProperties(mo, bucketInfo);
        bucketInfo.setUserId(currentUser.getId());
        bucketInfo.setRegionId(regionInfo.getId());
        bucketInfoDaoService.save(bucketInfo);
        return ResultFactory.wrapper();
    }

    @GetMapping("detail.json")
    @ApiOperation("获取 Bucket 基本信息")
    public Result detail(@RequestParam("name") String name) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>().eq("user_id", currentUser.getId()).eq("name", name));
        if (bucketInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        BucketInfoVo vo = new BucketInfoVo();
        BeanUtils.copyProperties(bucketInfo, vo);
        return ResultFactory.wrapper(vo);
    }

    @ApiOperation("获取 Bucket 基本设置")
    @GetMapping("get_basic_setting.json")
    public Result getBasicSetting() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("获取 Bucket 基本监控数据")
    @GetMapping("get_bucket_basic_monitor_data.json")
    public Result getBucketBasicMonitorData() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("获取 Bucket 对象数和文件碎片")
    @GetMapping("get_object_and_multipart_count.json")
    public Result getObjectAndMultipartCount() {
        return ResultFactory.wrapper();
    }

    @PostMapping("delete_bucket.json")
    @ApiOperation("删除 Bucket")
    public Result delete(@Validated @RequestBody DeleteBucketMo mo) {
        // 检查该 Bucket
        BucketInfo bucketInfo = bucketService.checkBucketExist(mo.getBucket());
        return ResultFactory.wrapper(bucketInfoDaoService.removeById(bucketInfo.getId()));
    }

    @PostMapping("set_acl.json")
    @ApiOperation("更新 Bucket 读写权限")
    public Result updateBucketAcl(@Validated @RequestBody UpdateBucketAclMo mo) {
        BucketInfo bucketInfo = bucketService.checkBucketExist(mo.getBucket());
        bucketInfo.setAcl(mo.getAcl());
        return ResultFactory.wrapper(bucketInfoDaoService.updateById(bucketInfo));
    }

    @GetMapping("get_referer.json")
    @ApiOperation("获取 Bucket 防盗链设置")
    public Result getReferer(@RequestParam("bucket") String bucket) {
        BucketInfo bucketInfo = bucketService.checkBucketExist(bucket);
        // 获取防盗链设置
        //allowEmpty: true //是否允许空 referer
        //list: [] //白名单
        return ResultFactory.wrapper();
    }
}
