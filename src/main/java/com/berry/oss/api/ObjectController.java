package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.exceptions.UploadException;
import com.berry.oss.common.utils.MD5;
import com.berry.oss.common.utils.RSAUtil;
import com.berry.oss.common.utils.SHA256;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
    public Result list(@RequestParam("bucketName") String bucketName,
                       @RequestParam(defaultValue = "/") String path,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketService.checkBucketExist(currentUser.getId(), bucketName);
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.eq("bucket_id", bucketInfo.getId());
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
    @ApiOperation("极速上传")
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
                         @RequestParam(value = "bucketId") String bucketId,
                         @RequestParam(value = "filePath", defaultValue = "/") String filePath,
                         @RequestHeader(value = "fileSize") Long fileSize,
                         @RequestHeader(value = "Digest") String digest) throws IOException {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

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
            // 调用存储数据服务，保存对象，返回24位对象id,
            fileId = dataSaveService.saveObject(file.getInputStream(), size, hash, fileName, bucketInfo.getName(), currentUser.getUsername());
        }
        // 保存上传信息
        Boolean result = objectService.saveObjectInfo(bucketId, bucketInfo.getAcl(), hash, fileSize, fileName, filePath, fileId);
        return ResultFactory.wrapper(result);
    }

    @GetMapping("detail")
    @ApiOperation("获取对象描述")
    public Result detail(@RequestParam String objectId) {
        return ResultFactory.wrapper(objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>().eq("file_id", objectId)));
    }

    @GetMapping("headObject")
    public Result getObjectHead(@RequestParam("objectId") String objectId) {
//        contentLength: 6059
//        contentType: "image/png"
//        eTag: "2B20908F33C257C1819665CA8D908E32"
//        lastModified: 1559091197000
        return ResultFactory.wrapper();
    }

    @PostMapping("generate_url_with_signed")
    public Result generateUrlWithSigned() {
        return ResultFactory.wrapper();
    }

    public static void main(String[] args) {
        String os = "Expires=1560602914&OSSAccessKeyId=TMP.AgF8SPD8sHeEw9MyLBKKysDBZ-_Wsp8vwSyUrsyDsx1dMzJece0FuFn03hDIADAtAhUAwyTiGFDL5SgdVgzCnrbxdVSG1KACFCYhhPpG-6C_w27NDNqXqJp4dINI&Signature=mzaj04f%2BQK8i5tqitL5tykTW6vs%3D";
        try {
            String deCode = URLDecoder.decode(os, "UTF-8");
            String[] split = deCode.split("&");
            System.out.println(Arrays.toString(split));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "{fileName}")
    @ApiOperation("获取对象")
    public String getPic(@PathVariable("fileName") String fileName,
                         @RequestParam("Expires") String expiresTime,
                         @RequestParam("OSSAccessKeyId") String ossAccessKeyId,
                         @RequestParam("Signature") String signature,
                         HttpServletResponse response, WebRequest request) throws IOException {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        ObjectInfo objectInfo = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>()
                .eq("user_id", currentUser.getId())
                .eq("file_name", fileName)
        );
        if (objectInfo == null) {
            return "资源不存在";
        }
        long lastModified = objectInfo.getUpdateTime().toEpochSecond(OffsetDateTime.now().getOffset()) * 1000;
        String eTag = "\"" + DigestUtils.md5DigestAsHex(fileName.getBytes()) + "\"";
        if (request.checkNotModified(eTag, lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            ObjectResource object = dataSaveService.getObject(objectInfo.getFileId());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            response.setHeader(HttpHeaders.ETAG, eTag);
            ZonedDateTime expiresDate = ZonedDateTime.now().with(LocalTime.MAX);
            String expires = expiresDate.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            response.setHeader(HttpHeaders.EXPIRES, expires);
            StreamUtils.copy(object.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        }
        return null;
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
