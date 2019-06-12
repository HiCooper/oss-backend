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
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.module.mo.FastUploadCheck;
import com.berry.oss.module.mo.UpdateObjectAclMo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.vm.UserInfoDTO;
import com.berry.oss.service.IBucketService;
import com.berry.oss.service.IDataSaveService;
import com.berry.oss.service.IObjectHashService;
import com.berry.oss.service.IObjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    private final IObjectService objectService;

    private final IObjectHashService objectHashService;

    private final IDataSaveService dataSaveService;

    @Autowired
    public ObjectController(IBucketService bucketService,
                            IObjectInfoDaoService objectInfoDaoService,
                            IObjectService objectService,
                            IObjectHashService objectHashService,
                            IDataSaveService dataSaveService) {
        this.bucketService = bucketService;
        this.objectInfoDaoService = objectInfoDaoService;
        this.objectService = objectService;
        this.objectHashService = objectHashService;
        this.dataSaveService = dataSaveService;
    }

    @GetMapping("list")
    @ApiOperation("获取 Object 列表")
    public Result list(@RequestParam String bucketId,
                       @RequestParam(defaultValue = "/") String path,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        bucketService.checkBucketExist(bucketId);
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.eq("bucket_id", bucketId);
        queryWrapper.eq("file_path", path);
        IPage<ObjectInfo> page = new Page<>(pageNum, pageSize);
        return ResultFactory.wrapper(objectInfoDaoService.page(page, queryWrapper));
    }

    /**
     * 相信接口传输的 hash 和 contentLength 是正确的
     *
     * @param fastUploadCheck 请求体
     * @return
     */
    @PostMapping("tryFastUpload")
    @ApiOperation("检查文件是否已在系统中")
    public Result tryFastUpload(@RequestBody FastUploadCheck fastUploadCheck) {
        // 检查bucket
        String bucketId = fastUploadCheck.getBucketId();
        BucketInfo bucketInfo = bucketService.checkBucketExist(bucketId);

        String fileId = objectHashService.checkExist(fastUploadCheck.getHash(), fastUploadCheck.getContentLength());
        if (StringUtils.isBlank(fileId)) {
            return ResultFactory.wrapper(false);
        }
        Boolean result = objectService.saveObjectInfo(
                bucketId,
                bucketInfo.getAcl(),
                fastUploadCheck.getHash(),
                fastUploadCheck.getContentLength(),
                fastUploadCheck.getFileName(),
                fastUploadCheck.getFilePath(),
                fileId);
        return ResultFactory.wrapper(result);
    }

    @PostMapping("create")
    @ApiOperation("创建对象")
    public Result create(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "bucketId", defaultValue = "6e5Jg3mtsmA9o5tFwLC2") String bucketId,
                         @RequestParam(value = "filePath", defaultValue = "/") String filePath,
                         @RequestHeader(value = "fileSize", defaultValue = "78557") Long fileSize,
                         @RequestHeader(value = "Digest", defaultValue = "A08635506169F7AB8799779411D7D7B074DD70BC9F336DD56DEA9357515EF265") String digest) throws IOException {
        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkBucketExist(bucketId);

        // 1. 获取请求头中，文件大小，文件hash
        if (fileSize > Integer.MAX_VALUE) {
            throw new UploadException("403", "文件大小不能超过2G");
        }
        // 计算文件 hash，获取文件大小
        String hash = SHA256.hash(file.getBytes());
        long size = file.getSize();
        if (!String.valueOf(size).equals(fileSize.toString()) || !digest.equals(hash)) {
            throw new UploadException("403", "文件校验失败");
        }
        // 校验通过，尝试快速上传
        String fileName = file.getOriginalFilename();
        String fileId = objectHashService.checkExist(hash, fileSize);
        if (StringUtils.isBlank(fileId)) {
            // 快速上传失败，
            // 调用存储数据服务，保存对象，返回32位对象id
            fileId = dataSaveService.saveObject(file.getInputStream(), fileName);
        }
        // 保存上传信息
        Boolean result = objectService.saveObjectInfo(bucketId, bucketInfo.getAcl(), hash, fileSize, fileName, filePath, fileId);

        return ResultFactory.wrapper(result);
    }

    @GetMapping("detail")
    @ApiOperation("获取对象详情")
    public Result detail(@RequestParam String objectId) {
        return ResultFactory.wrapper(objectInfoDaoService.getById(objectId));
    }

    @GetMapping("getPic")
    @ApiOperation("获取对象详情")
    public Result getPic(@RequestParam String objectId) throws IOException {
        InputStream object = dataSaveService.getObject(objectId);
        FileOutputStream outputStream= new FileOutputStream("./1.png");
        IOUtils.copy(object, outputStream);
        IOUtils.closeQuietly(object);
        return ResultFactory.wrapper();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除对象")
    public Result delete(@RequestParam String objectId) {
        // 检查该对象是否存在
        ObjectInfo objectInfo = objectInfoDaoService.getById(objectId);
        if (objectInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        // 删除该对象引用关系，减少哈希引用
        objectInfoDaoService.removeById(objectId);
        return ResultFactory.wrapper(objectHashService.decreaseRefCountByHash(objectInfo.getHash()));
    }

    @PutMapping("updateObjectAcl")
    @ApiOperation("更新对象读写权限")
    public Result updateObjectAcl(@RequestBody UpdateObjectAclMo mo) {
        // 检查该对象是否存在
        ObjectInfo objectInfo = objectInfoDaoService.getById(mo.getObjectId());
        if (objectInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        objectInfo.setAcl(mo.getNewAcl());
        objectInfoDaoService.updateById(objectInfo);
        return ResultFactory.wrapper();
    }

}
