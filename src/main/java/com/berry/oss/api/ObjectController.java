package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.exceptions.UploadException;
import com.berry.oss.common.utils.SHA256;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.vm.UserInfoDTO;
import com.berry.oss.service.IBucketService;
import io.reactivex.netty.protocol.http.server.HttpRequestHeaders;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
@Api(tags = "对象管理")
public class ObjectController {

    private final IBucketService bucketService;

    private final IObjectInfoDaoService objectInfoDaoService;

    @Autowired
    public ObjectController(IBucketService bucketService, IObjectInfoDaoService objectInfoDaoService) {
        this.bucketService = bucketService;
        this.objectInfoDaoService = objectInfoDaoService;
    }

    @GetMapping("list")
    @ApiOperation("获取 Object 列表")
    public Result list(@RequestParam String bucketId,
                       @RequestParam(defaultValue = "/") String path,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (!bucketService.checkBucketExist(bucketId)) {
            throw new BaseException(ResultCode.BUCKET_NOT_EXIST);
        }
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.eq("bucket_id", bucketId);
        queryWrapper.eq("file_path", path);
        IPage<ObjectInfo> page = new Page<>(pageNum, pageSize);
        return ResultFactory.wrapper(objectInfoDaoService.page(page, queryWrapper));
    }

    @PostMapping("create")
    @ApiOperation("创建对象")
    public Result create(@RequestParam("file") MultipartFile file, String bucketName, HttpServerRequest request) throws IOException {
        // 1. 获取请求头中，文件大小，文件hash
        HttpRequestHeaders headers = request.getHeaders();
        String length = headers.get("Content-Length");
        String digest = headers.get("Digest");
        // 计算文件 hash，获取文件大小
        String hash = SHA256.hash(file.getBytes());
        long size = file.getSize();
        if (!String.valueOf(size).equals(length) || !digest.equals(hash)) {
            throw new UploadException("403", "文件校验失败");
        }
        // 校验通过，调用存储数据服务，将文件按照 设定分块，分布式存储
        return ResultFactory.wrapper();
    }

    @GetMapping("detail")
    @ApiOperation("获取对象详情")
    public Result detail() {
        return ResultFactory.wrapper();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除对象")
    public Result delete() {
        return ResultFactory.wrapper();
    }

    @PutMapping("updateObjectAcl")
    @ApiOperation("更新对象读写权限")
    public Result updateObjectAcl() {
        return ResultFactory.wrapper();
    }
}
