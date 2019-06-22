package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.exceptions.UploadException;
import com.berry.oss.common.exceptions.XmlResponseException;
import com.berry.oss.common.exceptions.xml.AccessDenied;
import com.berry.oss.common.exceptions.xml.NotFound;
import com.berry.oss.common.exceptions.xml.SignatureDoesNotMatch;
import com.berry.oss.common.utils.*;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.mapper.ObjectInfoMapper;
import com.berry.oss.core.service.IBucketInfoDaoService;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
import com.berry.oss.module.mo.CreateFolderMo;
import com.berry.oss.module.mo.DeleteObjectsMo;
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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
@RequestMapping("ajax/bucket/file")
@Api(tags = "对象管理")
public class ObjectController {

    private static final Pattern FILE_PATH_PATTERN = Pattern.compile("(\\w+/?)+$");

    private static final String DEFAULT_FILE_PATH = "/";

    private final IBucketService bucketService;

    private final IObjectInfoDaoService objectInfoDaoService;

    private final IObjectService objectService;

    private final IObjectHashService objectHashService;

    private final IDataSaveService dataSaveService;

    private final IBucketInfoDaoService bucketInfoDaoService;

    @Autowired
    public ObjectController(IBucketService bucketService,
                            IObjectInfoDaoService objectInfoDaoService,
                            IObjectService objectService,
                            IObjectHashService objectHashService,
                            IDataSaveService dataSaveService,
                            IBucketInfoDaoService bucketInfoDaoService) {
        this.bucketService = bucketService;
        this.objectInfoDaoService = objectInfoDaoService;
        this.objectService = objectService;
        this.objectHashService = objectHashService;
        this.dataSaveService = dataSaveService;
        this.bucketInfoDaoService = bucketInfoDaoService;
    }

    @GetMapping("list_objects.json")
    @ApiOperation("获取 Object 列表")
    public Result list(@RequestParam("bucket") String bucket,
                       @RequestParam(value = "path", defaultValue = "/") String path,
                       @RequestParam(value = "search", defaultValue = "") String search) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketService.checkBucketExist(bucket);
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.eq("bucket_id", bucketInfo.getId());
        queryWrapper.orderByDesc("is_dir");
        if (StringUtils.isNotBlank(search)) {
            queryWrapper.likeRight("file_name", search);
        } else {
            queryWrapper.eq("file_path", path);
        }
        return ResultFactory.wrapper(objectInfoDaoService.list(queryWrapper));
    }

    @PostMapping("create")
    @ApiOperation("创建对象")
    public Result create(
            @RequestParam("bucket") String bucket,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "acl") String acl,
            @RequestParam(value = "filePath", defaultValue = DEFAULT_FILE_PATH) String filePath) throws IOException {

        Matcher matcher = FILE_PATH_PATTERN.matcher(filePath);
        if (!DEFAULT_FILE_PATH.equals(filePath) && !matcher.find()) {
            throw new UploadException("403", "当前上传文件目录不正确！");
        }
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>().eq("name", bucket));
        if (bucketInfo == null) {
            throw new UploadException("404", "bucket not exist");
        }

        // 计算文件 hash，获取文件大小
        String hash = SHA256.hash(file.getBytes());
        long fileSize = file.getSize();
        // 1. 获取请求头中，文件大小，文件hash
        if (fileSize > Integer.MAX_VALUE) {
            throw new UploadException("403", "文件大小不能超过2G");
        }

        // 校验通过
        String fileName = file.getOriginalFilename();

        // 检查该 bucket 及 path 下 同名文件是否存在
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        queryWrapper.eq("bucket_id", bucketInfo.getId());
        queryWrapper.eq("file_path", filePath);
        queryWrapper.eq("file_name", fileName);
        ObjectInfo one = objectInfoDaoService.getOne(queryWrapper);
        if (one != null) {
            // 同名文件存在，覆盖旧的（旧记录删除）
            objectInfoDaoService.removeById(one.getId());
            // 相关数据文件引用 -1
            objectHashService.decreaseRefCountByHash(one.getHash());
        }

        // 检查文件路径，非 / 则需要创建目录
        if (!DEFAULT_FILE_PATH.equals(filePath)) {
            String[] split = filePath.split("/");
            createFolderIgnore(currentUser, bucketInfo, split);
        }

        String msg = "极速上传成功!";
        // 尝试快速上传
        String fileId = objectHashService.checkExist(hash, fileSize);
        if (StringUtils.isBlank(fileId)) {
            msg = "上传成功";
            // 快速上传失败，
            // 调用存储数据服务，保存对象，返回24位对象id,
            fileId = dataSaveService.saveObject(file.getInputStream(), fileSize, hash, fileName, bucketInfo.getName(), currentUser.getUsername());
        }
        // 保存上传信息

        objectService.saveObjectInfo(bucketInfo.getId(), acl, hash, fileSize, fileName, filePath, fileId);
        return ResultFactory.wrapper(msg);
    }

    @GetMapping("detail.json")
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
     * @return 头部信息
     */
    @GetMapping("head_object.json")
    public Result getObjectHead(
            @RequestParam(value = "bucket") String bucket,
            @RequestParam(value = "path", required = false) String path,
            @RequestParam(value = "objectName") String objectName) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketService.checkBucketExist(bucket);
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
     * ossAccessKeyId： 私钥加密的用户id，保证由服务器签发，不被假冒
     * Signature：Expires 和 OSSAccessKeyId 参数进行 base64(md5(str)) 签名，保证不被篡改,
     * <p>
     * 请求url时，先验证签名，后验证 accessKeyId 由服务器签发，再验证过期时间是否有效，有效则返回对象数据
     *
     * @param mo 请求参数
     * @return 访问对象url
     * @throws Exception 编码异常，签名异常
     */
    @PostMapping("generate_url_with_signed.json")
    public Result generateUrlWithSigned(@RequestBody GenerateUrlWithSignedMo mo) throws Exception {

        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        // 将用户id 计算如签名，作为临时 ossAccessKeyId,解密时获取用户id
        String ossAccessKeyId = "TMP." + RSAUtil.encryptByPrivateKey(currentUser.getId().toString());

        String url = "http://" + NetworkUtils.INTERNET_IP + ":8077/ajax/bucket/file/" + mo.getBucket() + mo.getObjectPath();

        String urlExpiresAccessKeyId = "Expires=" + (System.currentTimeMillis() + mo.getTimeout() * 1000) / 1000 + "&OSSAccessKeyId=" + URLEncoder.encode(ossAccessKeyId, "UTF-8");

        // 对 参数部分 进行md5签名计算,并 base64编码
        String sign = new String(Base64.getEncoder().encode(MD5.md5Encode(urlExpiresAccessKeyId).getBytes()));

        // 拼接签名到url
        String signature = urlExpiresAccessKeyId + "&Signature=" + URLEncoder.encode(sign, "UTF-8");

        // 没有域名地址表，这里手动配置ip和端口
        GenerateUrlWithSignedVo vo = new GenerateUrlWithSignedVo()
                .setUrl(url)
                .setSignature(signature);
        return ResultFactory.wrapper(vo);
    }

    /**
     * 把指定URL后的字符串全部截断当成参数
     *
     * @param request
     * @return
     */
    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    @GetMapping(value = "{bucket}/**")
    @ApiOperation("获取对象(私有对象，需要临时口令，且限时访问；公开对象，直接访问)")
    public String getObject(
            @PathVariable("bucket") String bucket,
            @RequestParam(value = "Expires", required = false) String expiresTime,
            @RequestParam(value = "OSSAccessKeyId", required = false) String ossAccessKeyId,
            @RequestParam(value = "Signature", required = false) String signature,
            HttpServletResponse response, HttpServletRequest servletRequest, WebRequest request) throws Exception {
        String objectPath = extractPathFromPattern(servletRequest);
        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkBucketExist(bucket);

        String fileName = objectPath;
        String filePath = DEFAULT_FILE_PATH;
        if (objectPath.contains(DEFAULT_FILE_PATH)) {
            fileName = objectPath.substring(objectPath.lastIndexOf("/") + 1);
            filePath = "/" + objectPath.substring(0, objectPath.lastIndexOf("/"));
        }
        ObjectInfo objectInfo = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>()
                .eq("file_name", fileName)
                .eq("file_path", filePath)
                .eq("bucket_id", bucketInfo.getId())
        );
        if (objectInfo == null) {
//             资源不存在;
            throw new XmlResponseException(new AccessDenied());
        }

        if (!objectInfo.getAcl().startsWith("PUBLIC")) {
            // 非公开资源，需要验证身份及签名
            String url = "Expires=" + expiresTime + "&OSSAccessKeyId=" + URLEncoder.encode(ossAccessKeyId, "UTF-8");

            // 1. 签名验证
            String sign = new String(Base64.getEncoder().encode(MD5.md5Encode(url).getBytes()));
            if (!signature.equals(sign)) {
                throw new XmlResponseException(new SignatureDoesNotMatch(ossAccessKeyId, signature, servletRequest.getMethod() + " " + expiresTime + " " + objectPath));
            }

            // 2. 过期验证
            if (StringUtils.isNumeric(expiresTime)) {
                // 时间戳字符串转时间,expiresTime 是秒单位
                Date date = new Date(Long.valueOf(expiresTime) * 1000);
                if (date.before(new Date())) {
                    throw new XmlResponseException(new AccessDenied("Request has expired."));
                }
            }

            // 3. 身份验证
            try {
                String userIdEncodePart = ossAccessKeyId.substring(4);
                System.out.println(userIdEncodePart);
                RSAUtil.decryptByPublicKey(userIdEncodePart);
            } catch (Exception e) {
                throw new XmlResponseException(new AccessDenied("identity check fail."));
            }
        }

        return handlerResponse(objectPath, response, request, objectInfo);
    }

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @PostMapping("create_folder.json")
    public Result createFolder(@Validated @RequestBody CreateFolderMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkBucketExist(mo.getBucket());

        String[] split = mo.getObjectName().split("/");
        createFolderIgnore(currentUser, bucketInfo, split);
        return ResultFactory.wrapper();
    }

    /**
     * 创建目录，存在则忽略
     *
     * @param currentUser
     * @param bucketInfo
     * @param split
     */
    private void createFolderIgnore(UserInfoDTO currentUser, BucketInfo bucketInfo, String[] split) {
        try (SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false)) {
            ObjectInfoMapper mapper = session.getMapper(ObjectInfoMapper.class);
            ObjectInfo objectInfo;
            StringBuilder path = new StringBuilder("/");
            for (String dirName : split) {
                if (StringUtils.isNotBlank(dirName)) {
                    objectInfo = new ObjectInfo();
                    objectInfo.setId(ObjectId.get());
                    objectInfo.setIsDir(true);
                    objectInfo.setFileName(dirName);
                    objectInfo.setFilePath(path.toString());
                    objectInfo.setUserId(currentUser.getId());
                    objectInfo.setBucketId(bucketInfo.getId());
                    if ("/".equals(path.toString())) {
                        path.append(dirName);
                    } else {
                        path.append("/").append(dirName);
                    }
                    mapper.insertIgnore(objectInfo);
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
    }

    @PostMapping("delete_objects.json")
    @ApiOperation("删除对象")
    public Result delete(@Validated @RequestBody DeleteObjectsMo mo) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkBucketExist(mo.getBucket());

        String objects = mo.getObjects();
        String[] objectArray = objects.split(",");

        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_id", bucketInfo.getId());
        queryWrapper.eq("user_id", currentUser.getId());
        try (SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false)) {
            ObjectInfoMapper mapper = session.getMapper(ObjectInfoMapper.class);
            for (String item : objectArray) {
                String fileName = item;
                String path = DEFAULT_FILE_PATH;
                if (item.contains("/")) {
                    path = item.substring(0, item.lastIndexOf("/"));
                    fileName = item.substring(item.lastIndexOf("/") + 1);
                }
                queryWrapper.eq("file_path", path);
                queryWrapper.eq("file_name", fileName);
                ObjectInfo objectInfo = mapper.selectOne(queryWrapper);
                if (objectInfo != null) {
                    // 删除该对象引用关系，减少哈希引用
                    mapper.deleteById(objectInfo.getId());
                    System.out.println();
                    if (!objectInfo.getIsDir()) {
                        objectHashService.decreaseRefCountByHash(objectInfo.getHash());
                    }
                }
            }
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        return ResultFactory.wrapper();
    }

    @PostMapping("set_object_acl.json")
    @ApiOperation("更新对象读写权限")
    public Result updateObjectAcl(@RequestBody UpdateObjectAclMo mo) {
        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkBucketExist(mo.getBucket());

        // 检查该对象是否存在
        ObjectInfo objectInfo = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>()
                .eq("file_name", mo.getObjectName())
                .eq("bucket_id", bucketInfo.getId())
        );
        if (objectInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        objectInfo.setAcl(mo.getAcl());
        objectInfoDaoService.updateById(objectInfo);
        return ResultFactory.wrapper();
    }

    /**
     * 处理对象读取响应
     *
     * @param objectName 对象全路径
     * @param response   响应
     * @param request    请求
     * @param objectInfo 对象信息
     * @throws IOException IO 异常
     */
    private String handlerResponse(String objectName, HttpServletResponse response, WebRequest request, ObjectInfo objectInfo) throws IOException {
        long lastModified = objectInfo.getUpdateTime().toEpochSecond(OffsetDateTime.now().getOffset()) * 1000;
        String eTag = "\"" + DigestUtils.md5DigestAsHex(objectName.getBytes()) + "\"";
        if (request.checkNotModified(eTag, lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            ObjectResource object = dataSaveService.getObject(objectInfo.getFileId());
            if (object == null) {
                throw new XmlResponseException(new NotFound());
            }
            String contentType = StringUtils.getContentType(object.getFileName());
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.ETAG, eTag);
            ZonedDateTime expiresDate = ZonedDateTime.now().with(LocalTime.MAX);
            String expires = expiresDate.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            response.setHeader(HttpHeaders.EXPIRES, expires);
            StreamUtils.copy(object.getInputStream(), response.getOutputStream());
            response.flushBuffer();
        }
        return null;
    }

}
