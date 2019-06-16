package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.exceptions.UploadException;
import com.berry.oss.common.utils.*;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
import com.berry.oss.module.mo.FastUploadCheck;
import com.berry.oss.module.mo.GenerateUrlWithSignedMo;
import com.berry.oss.module.mo.UpdateObjectAclMo;
import com.berry.oss.module.vo.GenerateUrlWithSignedVo;
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
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern FILE_PATH_PATTERN = Pattern.compile("(\\w+\\/?)+$");

    private static final String defaultFilePath = "/";

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
                       @RequestParam(value = "path", defaultValue = "/") String path,
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
                         @RequestParam(value = "acl") String acl,
                         @RequestParam(value = "filePath", defaultValue = defaultFilePath) String filePath,
                         @RequestHeader(value = "fileSize") Long fileSize,
                         @RequestHeader(value = "Digest") String digest) throws IOException {

        Matcher matcher = FILE_PATH_PATTERN.matcher(filePath);
        if (!defaultFilePath.equals(filePath) && !matcher.find()) {
            throw new UploadException("403", "当前上传文件目录不正确！");
        }
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

        // 校验通过
        String fileName = file.getOriginalFilename();

        // 检查该 bucket 及 path 下 同名文件是否存在
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.eq("bucket_id", bucketInfo.getId());
        queryWrapper.eq("file_path", "/" + filePath);
        queryWrapper.eq("file_name", fileName);
        ObjectInfo one = objectInfoDaoService.getOne(queryWrapper);
        if (one != null) {
            // 同名文件存在，覆盖旧的（旧记录删除）
            objectInfoDaoService.removeById(one.getId());
            // 相关数据文件引用 -1
            objectHashService.decreaseRefCountByHash(one.getHash());
        }

        // 检查文件路径，非 / 则需要创建目录
        if (!defaultFilePath.equals(filePath)) {
            List<ObjectInfo> dirs = new ArrayList<>(16);
            String[] split = filePath.split("/");
            String path = "/";
            ObjectInfo objectInfo;
            for (String dirName : split) {
                objectInfo = new ObjectInfo();
                objectInfo.setId(ObjectId.get());
                objectInfo.setIsDir(true);
                objectInfo.setFileName(dirName);
                objectInfo.setFilePath(path);
                objectInfo.setAcl(acl);
                objectInfo.setUserId(currentUser.getId());
                objectInfo.setBucketId(bucketId);
                dirs.add(objectInfo);
                if ("/".equals(path)) {
                    path = path + dirName;
                } else {
                    path = path + "/" + dirName;
                }
            }
            objectInfoDaoService.saveBatch(dirs);
        }

        String msg = "极速上传成功!";
        // 尝试快速上传
        String fileId = objectHashService.checkExist(hash, fileSize);
        if (StringUtils.isBlank(fileId)) {
            msg = "上传成功";
            // 快速上传失败，
            // 调用存储数据服务，保存对象，返回24位对象id,
            fileId = dataSaveService.saveObject(file.getInputStream(), size, hash, fileName, bucketInfo.getName(), currentUser.getUsername());
        }
        // 保存上传信息
        objectService.saveObjectInfo(bucketId, acl, hash, fileSize, fileName, "/" + filePath, fileId);
        return ResultFactory.wrapper(msg);
    }

    @GetMapping("detail")
    @ApiOperation("获取对象描述")
    public Result detail(@RequestParam String objectId) {
        return ResultFactory.wrapper(objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>().eq("file_id", objectId)));
    }

    /**
     * 获取文件头部信息
     *
     * @param path       文件路径 可选
     * @param bucket     存储空间名称
     * @param objectName 文件名 必填
     * @return
     */
    @GetMapping("head_object")
    public Result getObjectHead(@RequestParam(value = "path", required = false) String path,
                                @RequestParam(value = "bucket") String bucket,
                                @RequestParam(value = "objectName") String objectName) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketService.checkBucketExist(currentUser.getId(), bucket);
        String eTag = DigestUtils.md5DigestAsHex(objectName.getBytes());
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<ObjectInfo>()
                .eq("bucket_id", bucketInfo.getId())
                .eq("user_id", currentUser.getId())
                .eq("file_name", objectName);
        if (StringUtils.isNotBlank(path)) {
            queryWrapper.eq("file_path", path);
        }
        ObjectInfo objectInfo = objectInfoDaoService.getOne(queryWrapper);
        if (objectInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        Map<String, Object> response = new HashMap<>(4);
        response.put("contentLength", objectInfo.getSize());
        response.put("contentType", objectInfo.getCategory());
        response.put("eTag", eTag);
        response.put("lastModified", objectInfo.getUpdateTime());
        return ResultFactory.wrapper(response);
    }

    /**
     * 生成附带签名的临时访问资源的url
     * 前半部分包含过期时间进行签名，保证有效时间不被篡改，且保证签名由服务器签发，不被假冒
     * 最后对前部分包含临时访问 accessKeyId 进行 base64(md5(str)) 签名，保证不被篡改,
     * <p>
     * 请求url时，先验证签名，后验证 accessKeyId 由服务器签发，再验证过期时间是否有效，有效则返回对象数据
     *
     * @param mo
     * @return
     * @throws Exception
     */
    @PostMapping("generate_url_with_signed")
    public Result generateUrlWithSigned(@RequestBody GenerateUrlWithSignedMo mo) throws Exception {

        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        // 将用户id 计算如签名，作为临时 ossAccessKeyId,解密时获取用户id
        String ossAccessKeyId = "TMP." + RSAUtil.encryptByPrivateKey(currentUser.getId().toString());

        String url = "http://" + NetworkUtils.getIpAddress() + ":8077/api/object/" + mo.getObjectName();

        String urlExpiresAccessKeyId = "Expires=" + (System.currentTimeMillis() + mo.getTimeout() * 1000) / 1000 + "&OSSAccessKeyId=" + URLEncoder.encode(ossAccessKeyId, "UTF-8");

        // 对 参数部分 进行md5签名计算,并 base64编码
        String sign = new BASE64Encoder().encodeBuffer(MD5.md5Encode(urlExpiresAccessKeyId).getBytes());

        // 拼接签名到url
        String signature = urlExpiresAccessKeyId + "&Signature=" + URLEncoder.encode(sign, "UTF-8");

        // 没有域名地址表，这里手动配置ip和端口
        GenerateUrlWithSignedVo vo = new GenerateUrlWithSignedVo()
                .setUrl(url)
                .setSignature(signature);
        return ResultFactory.wrapper(vo);
    }

    @GetMapping(value = "{fileName}")
    @ApiOperation("获取对象")
    public String getPic(@PathVariable("fileName") String fileName,
                         @RequestParam("Expires") String expiresTime,
                         @RequestParam("OSSAccessKeyId") String ossAccessKeyId,
                         @RequestParam("Signature") String signature,
                         HttpServletResponse response, WebRequest request) throws Exception {
        String url = "Expires=" + expiresTime + "&OSSAccessKeyId=" + URLEncoder.encode(ossAccessKeyId, "UTF-8");

        // 1. 签名验证
        String sign = new BASE64Encoder().encodeBuffer(MD5.md5Encode(url).getBytes());
        if (!signature.equals(sign)) {
            return "签名校验错误";
        }

        // 2. 过期验证
        if (StringUtils.isNumeric(expiresTime)) {
            // 时间戳字符串转时间,expiresTime 是秒单位
            Date date = new Date(Long.valueOf(expiresTime) * 1000);
            if (date.before(new Date())) {
                return "链接已过期";
            }
        }

        // 3. 身份验证
        String userId;
        try {
            String userIdEncodePart = ossAccessKeyId.substring(4);
            System.out.println(userIdEncodePart);
            userId = RSAUtil.decryptByPublicKey(userIdEncodePart);
        } catch (Exception e) {
            return "身份校验错误";
        }

        ObjectInfo objectInfo = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>()
                .eq("user_id", userId)
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
