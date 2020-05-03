package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.constant.CommonConstant;
import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.exceptions.UploadException;
import com.berry.oss.common.exceptions.XmlResponseException;
import com.berry.oss.common.exceptions.xml.AccessDenied;
import com.berry.oss.common.exceptions.xml.NotFound;
import com.berry.oss.common.exceptions.xml.SignatureDoesNotMatch;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.common.utils.*;
import com.berry.oss.config.GlobalProperties;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.entity.ObjectInfo;
import com.berry.oss.dao.entity.RefererInfo;
import com.berry.oss.dao.service.IBucketInfoDaoService;
import com.berry.oss.dao.service.IObjectInfoDaoService;
import com.berry.oss.dao.service.IRefererInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
import com.berry.oss.module.vo.GenerateUrlWithSignedVo;
import com.berry.oss.module.vo.ObjectInfoVo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.dto.UserInfoDTO;
import com.berry.oss.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.berry.oss.common.constant.Constants.DEFAULT_FILE_PATH;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:33
 * fileName：ObjectServiceImpl
 * Use：
 */
@Service
public class ObjectServiceImpl implements IObjectService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int UPLOAD_PER_SIZE_LIMIT = 200;
    private static final String BASE64_DATA_START_PATTERN = "data:image/[a-z];";

    private static final String USER_ID_COLUMN = "user_id";
    private static final String BUCKET_ID_COLUMN = "bucket_id";
    private static final String FILE_PATH_COLUMN = "file_path";
    private static final String FILE_NAME_COLUMN = "file_name";

    private static final String CHART_SET = "UTF-8";

    private final IObjectInfoDaoService objectInfoDaoService;
    private final IObjectHashService objectHashService;
    private final IBucketService bucketService;
    private final IDataService dataService;
    private final IBucketInfoDaoService bucketInfoDaoService;
    private final GlobalProperties globalProperties;
    private final IAuthService authService;
    private final IRefererInfoDaoService refererInfoDaoService;

    ObjectServiceImpl(IObjectInfoDaoService objectInfoDaoService,
                      IObjectHashService objectHashService,
                      IBucketService bucketService,
                      IDataService dataService,
                      IBucketInfoDaoService bucketInfoDaoService,
                      GlobalProperties globalProperties,
                      IRefererInfoDaoService refererInfoDaoService,
                      IAuthService authService) {
        this.objectInfoDaoService = objectInfoDaoService;
        this.objectHashService = objectHashService;
        this.bucketService = bucketService;
        this.dataService = dataService;
        this.bucketInfoDaoService = bucketInfoDaoService;
        this.globalProperties = globalProperties;
        this.refererInfoDaoService = refererInfoDaoService;
        this.authService = authService;
    }

    @Override
    public List<ObjectInfo> list(String bucket, String path, String search) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(bucket);
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(USER_ID_COLUMN, currentUser.getId());
        queryWrapper.eq(BUCKET_ID_COLUMN, bucketInfo.getId());
        queryWrapper.orderByDesc("is_dir");
        if (isNotBlank(search)) {
            queryWrapper.likeRight(FILE_NAME_COLUMN, search);
            if (!path.equalsIgnoreCase(DEFAULT_FILE_PATH)) {
                // 仅在非 首页启用当前路径匹配
                queryWrapper.eq(FILE_PATH_COLUMN, path);
            }
        } else {
            queryWrapper.eq(FILE_PATH_COLUMN, path);
        }
        return objectInfoDaoService.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ObjectInfoVo> create(String bucket, MultipartFile[] files, String acl, String filePath) throws Exception {

        if (files.length > UPLOAD_PER_SIZE_LIMIT) {
            throw new UploadException("403", "最多同时上传数量为200");
        }

        // 验证acl 规范
        if (!CommonConstant.AclType.ALL_NAME.contains(acl)) {
            throw new UploadException("403", "不支持的ACL 可选值 [PRIVATE, PUBLIC_READ, PUBLIC_READ_WRITE]");
        }
        // 校验path 规范
        checkPath(filePath);

        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = getBucketInfo(bucket, currentUser);

        List<ObjectInfoVo> vos = new ArrayList<>();
        InputStream inputStream = null;
        for (MultipartFile file : files) {
            // 计算文件 hash，获取文件大小
            String hash = SHA256.hash(file.getBytes());
            long fileSize = file.getSize();
            // 1. 获取请求头中，文件大小，文件hash
            if (fileSize > Integer.MAX_VALUE) {
                throw new UploadException("403", "文件大小不能超过2G");
            }

            // 校验通过
            String fileName = file.getOriginalFilename();
            if (isNotBlank(fileName)) {
                // 过滤替换文件名特殊字符
                fileName = StringUtils.filterUnsafeUrlCharts(fileName);
            }

            ObjectInfoVo vo = new ObjectInfoVo();
            inputStream = file.getInputStream();
            int available = inputStream.available();
            byte[] data = new byte[available];
            int readSize = inputStream.read(data);
            if (readSize != available) {
                vo.setAcl(acl);
                vo.setFileName(fileName);
                vo.setSuccess(false);
                continue;
            }
            // 保存或更新改对象信息
            saveOrUpdateObject(filePath, data, acl, currentUser.getId(), bucketInfo, hash, fileSize, vo, fileName);

            buildResponse(bucket, filePath, fileName, acl, "", fileSize, vo);
            vos.add(vo);
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjectInfoVo uploadByte(String bucket, String filePath, String fileName, byte[] data, String acl) throws IOException {
        // 检查文件名，验证acl 规范
        fileName = checkFilenameAndPath(filePath, fileName, acl);

        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = getBucketInfo(bucket, currentUser);

        // 计算数据hash
        String hash = SHA256.hash(data);
        // 大小
        long size = data.length;

        // 返回对象
        ObjectInfoVo vo = new ObjectInfoVo();

        // 保存或更新改对象信息
        saveOrUpdateObject(filePath, data, acl, currentUser.getId(), bucketInfo, hash, size, vo, fileName);

        buildResponse(bucket, filePath, fileName, acl, "", size, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjectInfoVo uploadByBase64Str(String bucket, String filePath, String fileName, String data, String acl) throws IOException {
        fileName = checkFilenameAndPath(filePath, fileName, acl);

        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = getBucketInfo(bucket, currentUser);

        // 检查数据格式
        String[] dataArr = data.split("base64,");
        if (dataArr.length != 2 || dataArr[0].matches(BASE64_DATA_START_PATTERN)) {
            throw new UploadException("403", "非法base64数据");
        }

        String fileType = getFileType(dataArr[0]);
        // 计算数据hash
        byte[] byteData = Base64Utils.decodeFromString(dataArr[1]);
        String hash = SHA256.hash(byteData);
        long size = dataArr[1].length();

        // 返回对象
        ObjectInfoVo vo = new ObjectInfoVo();

        // 保存或更新改对象信息
        saveOrUpdateObject(filePath, byteData, acl, currentUser.getId(), bucketInfo, hash, size, vo, fileName + fileType);

        buildResponse(bucket, filePath, fileName, acl, fileType, size, vo);
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createFolder(String bucket, String folder) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(bucket);

        List<ObjectInfo> infos = getFolderInfoList(currentUser.getId(), bucketInfo.getId(), folder);
        if (!CollectionUtils.isEmpty(infos)) {
            objectInfoDaoService.insertIgnoreBatch(infos);
        }
    }

    @Override
    public void getObject(String bucket, String expiresTime, String ossAccessKeyId, String signature, Boolean download, HttpServletResponse response, HttpServletRequest servletRequest, WebRequest request) throws IOException {

        String objectPath = extractPathFromPattern(servletRequest);

        // 检查bucket
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>().eq("name", bucket));
        if (null == bucketInfo) {
            throw new XmlResponseException(new AccessDenied("bucket does not exist"));
        }

        boolean skipCheckAuth = false;
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        boolean anonymous = currentUser == null || currentUser.getId() == null;

        if (!anonymous) {
            // 用户请求头中带有密钥口令，无需url验证
            // 1. 检查当前用户 是否拥有对 所请求bucket的访问权限，通过后 可获取对该bucket的完全权限,跳过 url 校验
            skipCheckAuth = authService.checkUserHaveAccessToBucketObject(currentUser, bucket, "/" + objectPath);
        }

        String fileName = objectPath;
        String filePath = DEFAULT_FILE_PATH;
        if (objectPath.contains(DEFAULT_FILE_PATH)) {
            fileName = objectPath.substring(objectPath.lastIndexOf(DEFAULT_FILE_PATH) + 1);
            filePath = DEFAULT_FILE_PATH + objectPath.substring(0, objectPath.lastIndexOf(DEFAULT_FILE_PATH));
        }
        ObjectInfo objectInfo = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>()
                .eq(FILE_NAME_COLUMN, fileName)
                .eq(FILE_PATH_COLUMN, filePath)
                .eq(BUCKET_ID_COLUMN, bucketInfo.getId())
        );
        if (objectInfo == null) {
            // 资源不存在
            throw new XmlResponseException(new NotFound());
        }

        String objectAcl = objectInfo.getAcl();
        boolean extendAclPass = objectAcl.equals(CommonConstant.AclType.EXTEND_BUCKET.name()) && bucketInfo.getAcl().startsWith("PUBLIC");

        if (!skipCheckAuth && anonymous && (objectAcl.startsWith("PUBLIC") || extendAclPass)) {
            // 匿名访问 公开资源，检查 referer
            checkReferer(request, bucketInfo);
        }

        // 继承bucket acl,且为public，放行
        // 自身acl 以 PUBLIC 开头 放行
        // 跳过 skipCheckAuth 放行
        if (!objectAcl.startsWith("PUBLIC") && !skipCheckAuth && !extendAclPass) {
            if (isAnyBlank(expiresTime, ossAccessKeyId, signature)) {
                throw new XmlResponseException(new AccessDenied("illegal url"));
            }

            // 非公开资源，需要验证身份及签名
            String url = "Expires=" + expiresTime + "&OSSAccessKeyId=" + URLEncoder.encode(ossAccessKeyId, CHART_SET);

            // 1. 签名验证
            String sign = new String(Base64.getEncoder().encode(MD5.md5Encode(url).getBytes()));
            if (!signature.equals(sign)) {
                throw new XmlResponseException(new SignatureDoesNotMatch(ossAccessKeyId, signature, servletRequest.getMethod() + " " + expiresTime + " " + objectPath));
            }

            // 2. 过期验证
            if (isNumeric(expiresTime)) {
                // 时间戳字符串转时间,expiresTime 是秒单位
                Date date = new Date(Long.parseLong(expiresTime) * 1000);
                if (date.before(new Date())) {
                    throw new XmlResponseException(new AccessDenied("Request has expired."));
                }
            }

            // 3. 身份验证,这里采用私钥加密，公钥解密，而没有采用 私钥签名，后续可能对账户信息进行控制，故而采用私钥加密 用户id，备解密时需要
            try {
                String userIdEncodePart = ossAccessKeyId.substring(4);
                RSAUtil.decryptByPublicKey(userIdEncodePart);
            } catch (Exception e) {
                throw new XmlResponseException(new AccessDenied("identity check fail."));
            }
        }

        handlerResponse(bucket, objectPath, response, request, objectInfo, download);
    }

    @Override
    public void makeUpForLostData(String fileName, String filePath, MultipartFile file, String fileUrl) throws IOException {
        // 根据文件名 文件路径 获取对象 fileId
        ObjectInfo one = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>().eq("file_name", fileName).eq("file_path", filePath));
        if (one == null) {
            throw new BaseException("404", "对象不存在");
        }
        BucketInfo bucketInfo = bucketInfoDaoService.getById(one.getBucketId());
        dataService.makeUpForLostData(fileName, filePath, one.getFileId(), file, fileUrl, bucketInfo);
    }

    private void checkReferer(WebRequest request, BucketInfo bucketInfo) {
        // 匿名访问，检查 referer
        String headReferer = request.getHeader("Referer");
        RefererInfo refererInfo = refererInfoDaoService.getOne(new QueryWrapper<RefererInfo>().eq(BUCKET_ID_COLUMN, bucketInfo.getId()));
        if (refererInfo != null) {
            // 是否 允许空 Referer
            Boolean allowEmpty = refererInfo.getAllowEmpty();
            String whiteList = refererInfo.getWhiteList();
            // 同时设置了 ‘允许空 Referer’(非 null) 和 ‘白名单’ 两者方可生效
            if (allowEmpty != null && isNotBlank(whiteList)) {
                // 1.允许为空
                boolean allowEmptyPrimitive = allowEmpty;
                if (allowEmptyPrimitive) {
                    return;
                }
                // 2.不允许 空 referer，请求 头中 没有 referer，则deny
                if (isBlank(headReferer)) {
                    throw new XmlResponseException(new AccessDenied("referer deny"));
                }
                // 3. 白名单，pass
                String[] whiteArr = whiteList.split(",");
                boolean match = false;
                for (String white : whiteArr) {
                    if (headReferer.matches(white)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    throw new XmlResponseException(new AccessDenied("referer deny"));
                }
            }
        }
    }

    @Override
    public Map<String, Object> getObjectHead(String bucket, String path, String objectName) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(bucket);
        String fullPath = path.equals("/") ? (path + objectName) : (path + "/" + objectName);
        String eTag = DigestUtils.md5DigestAsHex(fullPath.getBytes());
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<ObjectInfo>()
                .eq(BUCKET_ID_COLUMN, bucketInfo.getId())
                .eq(USER_ID_COLUMN, currentUser.getId())
                .eq(FILE_PATH_COLUMN, path)
                .eq(FILE_NAME_COLUMN, objectName);
        ObjectInfo objectInfo = objectInfoDaoService.getOne(queryWrapper);
        if (objectInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        Map<String, Object> response = new HashMap<>(4);
        response.put("contentLength", objectInfo.getSize());
        response.put("contentType", objectInfo.getCategory());
        response.put("eTag", eTag);
        response.put("lastModified", objectInfo.getUpdateTime());
        return response;
    }

    @Override
    public GenerateUrlWithSignedVo generateUrlWithSigned(String bucket, String objectPath, Integer timeout) throws Exception {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        if (!bucketService.checkUserHaveBucket(currentUser.getId(), bucket)) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }

        // 将用户id 计算如签名，作为临时 ossAccessKeyId,解密时获取用户id
        String ossAccessKeyId = "TMP." + RSAUtil.encryptByPrivateKey(currentUser.getId().toString());

        if (!objectPath.startsWith(DEFAULT_FILE_PATH)) {
            objectPath = DEFAULT_FILE_PATH + objectPath;
        }
        String url = globalProperties.getServerAddress() + "/ajax/bucket/file/" + bucket + objectPath;

        long expires = (System.currentTimeMillis() + timeout * 1000) / 1000;
        String tempAccessKeyId = URLEncoder.encode(ossAccessKeyId, CHART_SET);

        Map<String, Object> paramsMap = new HashMap<>(3);
        paramsMap.put("Expires", expires);
        paramsMap.put("OSSAccessKeyId", tempAccessKeyId);
        String urlExpiresAccessKeyId = StringUtils.sortMap(paramsMap);

        // 对 参数部分 进行md5签名计算,并 base64编码
        String sign = new String(Base64.getEncoder().encode(MD5.md5Encode(urlExpiresAccessKeyId).getBytes()));

        // 拼接签名到url
        String signature = urlExpiresAccessKeyId + "&Signature=" + URLEncoder.encode(sign, CHART_SET);

        // 没有域名地址表，这里手动配置ip和端口
        return new GenerateUrlWithSignedVo()
                .setUrl(url)
                .setSignature(signature);
    }

    @Override
    public List<String> generateDownloadUrl(String bucket, List<String> objectPath) throws Exception {
        List<String> url = new ArrayList<>();
        for (String object : objectPath) {
            GenerateUrlWithSignedVo generateUrlWithSignedVo = generateUrlWithSigned(bucket, object, 60);
            url.add(generateUrlWithSignedVo.getUrl() + "?" + generateUrlWithSignedVo.getSignature() + "&Download=true");
        }
        return url;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String bucket, String objectIds) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();

        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(bucket);

        String[] objectIdArray = objectIds.split(",");

        List<ObjectInfo> objectInfos = new ArrayList<>(objectInfoDaoService.listByIds(Arrays.asList(objectIdArray)));

        if (!CollectionUtils.isEmpty(objectInfos)) {
            List<ObjectInfo> files = objectInfos.stream().filter(info -> !info.getIsDir()).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(files)) {
                // 1.对象hash引用 计数 -1 (注意这里 hash 可能相等， 所以不能使用set)
                objectHashService.batchDecreaseRefCountByHash(files.stream().map(ObjectInfo::getHash).collect(Collectors.toList()));
                // 2. 删除 文件
                objectInfoDaoService.removeByIds(files.stream().map(ObjectInfo::getId).collect(Collectors.toList()));
            }

            List<ObjectInfo> dirs = objectInfos.stream().filter(ObjectInfo::getIsDir).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(dirs)) {
                // 删除文件夹本身
                objectInfoDaoService.removeByIds(dirs.stream().map(ObjectInfo::getId).collect(Collectors.toList()));
                // 删除文件夹的子目录(如果是文件夹，则删除该文件夹下所有的子项)
                dirs.forEach(dir -> {
                    String filePath = dir.getFilePath();
                    String dirFullPath = filePath.equalsIgnoreCase(DEFAULT_FILE_PATH) ? filePath + dir.getFileName() : filePath + "/" + dir.getFileName();
                    QueryWrapper<ObjectInfo> objectInfoQueryWrapper = new QueryWrapper<ObjectInfo>()
                            .eq(BUCKET_ID_COLUMN, bucketInfo.getId())
                            .eq(USER_ID_COLUMN, currentUser.getId())
                            .likeRight(FILE_PATH_COLUMN, dirFullPath);
                    List<ObjectInfo> infos = objectInfoDaoService.list(objectInfoQueryWrapper);
                    if (!CollectionUtils.isEmpty(infos)){
                        // 1. 对应文件的 引用计数 -1
                        objectHashService.batchDecreaseRefCountByHash(infos.stream().map(ObjectInfo::getHash).collect(Collectors.toList()));
                        // 2. 删除文件和子项
                        objectInfoDaoService.removeByIds(infos.stream().map(ObjectInfo::getId).collect(Collectors.toList()));
                    }
                });
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateObjectAcl(String bucket, String objectPath, String objectName, String acl) {
        // 检查bucket
        BucketInfo bucketInfo = bucketService.checkUserHaveBucket(bucket);

        // 检查该对象是否存在
        ObjectInfo objectInfo = objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>()
                .eq(FILE_PATH_COLUMN, objectPath)
                .eq(FILE_NAME_COLUMN, objectName)
                .eq(BUCKET_ID_COLUMN, bucketInfo.getId())
        );
        if (objectInfo == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        objectInfo.setAcl(acl);
        return objectInfoDaoService.updateById(objectInfo);
    }

    public void saveObjectInfo(String bucketId, String acl, String hash, Long contentLength, String fileName, String filePath, String fileId) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        List<ObjectInfo> newObject = new ArrayList<>();
        // 添加 该账号 该文件记录
        ObjectInfo objectInfo = new ObjectInfo()
                .setId(ObjectId.get())
                .setBucketId(bucketId)
                .setCategory(StringUtils.getExtName(fileName))
                .setFileId(fileId)
                .setSize(contentLength)
                .setFileName(fileName)
                .setFilePath(filePath)
                .setIsDir(false)
                .setAcl(acl)
                .setHash(hash)
                .setUserId(currentUser.getId())
                .setFormattedSize(StringUtils.getFormattedSize(contentLength));
        newObject.add(objectInfo);
        // 检查文件路径，非 / 则需要创建目录
        if (!DEFAULT_FILE_PATH.equals(filePath)) {
            List<ObjectInfo> infos = getFolderInfoList(currentUser.getId(), bucketId, filePath);
            if (!CollectionUtils.isEmpty(infos)) {
                // 添加目录信息
                newObject.addAll(infos);
            }
        }
        // 文件信息 和 目录信息同时 insert
        objectInfoDaoService.insertIgnoreBatch(newObject);

        // 引用+1
        objectHashService.increaseRefCountByHash(hash, fileId, contentLength);
    }

    private void saveOrUpdateObject(String filePath, byte[] data, String acl, Long userId,
                                    BucketInfo bucketInfo, String hash, long size,
                                    ObjectInfoVo vo, String fullFileName) throws IOException {
        // 检查 该用户 同目录 同名 同bucket 下 文件是否已经存在（只检查文件路径和名称，不检查文件内容）
        ObjectInfo objectInfo = getObjectInfo(filePath, userId, bucketInfo.getId(), fullFileName);
        boolean exist = objectInfo != null;

        vo.setReplace(exist);

        if (exist) {
            String oldHash = objectInfo.getHash();
            if (!oldHash.equals(hash)) {
                // 存在，但文件内容有变动, 尝试快速上传
                String fileId = objectHashService.checkExist(hash);
                if (isBlank(fileId)) {
                    vo.setUploadType(false);
                    // 快速上传失败 调用存储数据服务，保存对象，返回24位对象id,
                    fileId = dataService.saveObject(filePath, data, size, hash, fullFileName, bucketInfo);
                }
                objectInfo.setFileId(fileId);
                objectInfo.setHash(hash);
                objectInfo.setSize(size);
                objectInfo.setFormattedSize(StringUtils.getFormattedSize(size));
                objectHashService.increaseRefCountByHash(hash, fileId, size);
                objectHashService.decreaseRefCountByHash(oldHash);
            }
            objectInfo.setUpdateTime(new Date());
            objectInfoDaoService.updateById(objectInfo);
        } else {
            // 尝试快速上传
            String fileId = objectHashService.checkExist(hash);
            if (isBlank(fileId)) {
                // 快速上传失败，
                vo.setUploadType(false);
                // 调用存储数据服务，保存对象，返回24位对象id,
                fileId = dataService.saveObject(filePath, data, size, hash, fullFileName, bucketInfo);
            }
            // 保存上传信息
            saveObjectInfo(bucketInfo.getId(), acl, hash, size, fullFileName, filePath, fileId);
        }
    }

    private String checkFilenameAndPath(String filePath, String fileName, String acl) {
        // 验证acl 规范
        if (!CommonConstant.AclType.ALL_NAME.contains(acl)) {
            throw new UploadException("403", "不支持的ACL 可选值 [PRIVATE, PUBLIC_READ, PUBLIC_READ_WRITE]");
        }

        // 过滤替换文件名特殊字符
        fileName = StringUtils.filterUnsafeUrlCharts(fileName);

        // 校验path 规范
        checkPath(filePath);
        return fileName;
    }

    private void buildResponse(String bucket, String filePath, String fileName, String acl, String fileType, long size, ObjectInfoVo vo) {
        vo.setAcl(acl);
        vo.setFileName(fileName);
        vo.setFilePath(filePath);
        vo.setSize(size);
        vo.setFormattedSize(StringUtils.getFormattedSize(size));
        String url = getPublicObjectUrl(bucket, filePath, fileName);
        vo.setUrl(url + fileType);
    }

    private BucketInfo getBucketInfo(String bucket, UserInfoDTO currentUser) {
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>()
                .eq("name", bucket)
                .eq(USER_ID_COLUMN, currentUser.getId())
        );
        if (bucketInfo == null) {
            throw new UploadException("404", "bucket not exist");
        }
        return bucketInfo;
    }

    private static String getFileType(String dataPrefix) {
        return "." + dataPrefix.substring(dataPrefix.lastIndexOf(DEFAULT_FILE_PATH) + 1, dataPrefix.length() - 1);
    }

    private static void checkPath(String filePath) {
        // 校验path 规范
        if (!DEFAULT_FILE_PATH.equals(filePath)) {
            boolean matches = filePath.substring(1).matches(Constants.FILE_PATH_PATTERN);
            if (!filePath.startsWith(DEFAULT_FILE_PATH) || !matches) {
                throw new UploadException("403", "当前上传文件目录不正确！filePath:" + filePath);
            }
        }
    }

    /**
     * 检查文件是否存在 返回是否替换
     *
     * @return true or false
     */
    private ObjectInfo getObjectInfo(String filePath, Long userId, String bucketId, String fileName) {
        // 检查该 bucket 及 path 下 同名文件是否存在
        QueryWrapper<ObjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(USER_ID_COLUMN, userId);
        queryWrapper.eq(BUCKET_ID_COLUMN, bucketId);
        queryWrapper.eq(FILE_PATH_COLUMN, filePath);
        queryWrapper.eq(FILE_NAME_COLUMN, fileName);
        return objectInfoDaoService.getOne(queryWrapper);
    }

    /**
     * 组装需要创建目录对象
     *
     * @param userId   用户id
     * @param bucketId bucketId
     * @param filePath 文件夹全路径
     */
    private List<ObjectInfo> getFolderInfoList(Long userId, String bucketId, String filePath) {
        // 1. 检查路径是否存在
        String folder = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        // 不存在则 进行创建
        String[] objectArr = folder.split("/");
        List<ObjectInfo> list = new ArrayList<>();
        ObjectInfo objectInfo;
        StringBuilder path = new StringBuilder("/");
        for (String dirName : objectArr) {
            if (isNotBlank(dirName)) {
                objectInfo = new ObjectInfo();
                objectInfo.setId(ObjectId.get());
                objectInfo.setIsDir(true);
                objectInfo.setFileName(dirName);
                objectInfo.setFilePath(path.toString());
                objectInfo.setUserId(userId);
                objectInfo.setBucketId(bucketId);
                if ("/".equals(path.toString())) {
                    path.append(dirName);
                } else {
                    path.append("/").append(dirName);
                }
                list.add(objectInfo);
            }
        }
        return list;
    }

    /**
     * 把指定URL后的字符串全部截断当成参数
     *
     * @param request request
     * @return 参数字符串
     */
    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    /**
     * 处理对象读取响应
     *
     * @param bucket     bucket name
     * @param objectPath 对象全路径 如：/test.jpg
     * @param response   响应
     * @param request    请求
     * @param objectInfo 对象信息
     * @param download   是否是下载
     * @throws IOException IO 异常
     */
    private void handlerResponse(String bucket, String objectPath, HttpServletResponse response, WebRequest request, ObjectInfo objectInfo, Boolean download) throws IOException {
        ObjectResource object = null;
        if (download != null && download) {
            object = dataService.getObject(bucket, objectInfo.getFileId());
            if (object == null || object.getInputStream() == null) {
                throw new XmlResponseException(new NotFound());
            }
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            copyStream(0L, object.getFileSize() - 1, response, object.getInputStream());
            return;
        }

        long lastModified = objectInfo.getUpdateTime().getTime();
        String eTag = "\"" + DigestUtils.md5DigestAsHex(objectPath.getBytes()) + "\"";
        if (request.checkNotModified(eTag, lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            object = dataService.getObject(bucket, objectInfo.getFileId());
            if (object == null) {
                throw new XmlResponseException(new NotFound());
            }
            String contentType = StringUtils.getContentType(object.getFileName());
            InputStream inputStream = object.getInputStream();
            // 针对 safari 浏览器处理
            Map<String, Long> res = handleForSafari(request, object.getFileSize(), response);
            // 非 safari 浏览器常规处理
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.ETAG, eTag);
            ZonedDateTime expiresDate = ZonedDateTime.now().with(LocalTime.MAX);
            String expires = expiresDate.format(DateTimeFormatter.RFC_1123_DATE_TIME);
            response.setHeader(HttpHeaders.EXPIRES, expires);
            copyStream(res.get("start"), res.get("end"), response, inputStream);
        }
        if (object != null && object.getInputStream() != null) {
            object.getInputStream().close();
        }
    }

    private void copyStream(Long start, Long end, HttpServletResponse response, InputStream inputStream) throws IOException {
        try {
            StreamUtils.copyRange(inputStream, response.getOutputStream(), start, end);
        } catch (Exception e) {
            logger.error("Something went wrong when copy stream, msg: {}", e.getMessage());
        } finally {
            response.getOutputStream().close();
        }
        response.flushBuffer();
    }

    private Map<String, Long> handleForSafari(WebRequest request, long size, HttpServletResponse response) {
        Map<String, Long> map = new HashMap<>(8);
        map.put("start", 0L);
        map.put("end", size - 1);
        long length = size;
        // bytes=0-1
        String range = request.getHeader("Range");
        if (isNotBlank(range)) {
            //206
            String[] split = range.split("=");
            String s = split[1];
            if (isNotBlank(s)) {
                String[] split1 = s.split("-");
                if (split1.length > 1) {
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    String startStr = split1[0];
                    String endStr = split1[1];
                    long start = 0L;
                    long end = size - 1L;
                    if (isNotBlank(startStr) && isNumeric(startStr)) {
                        start = Long.parseLong(startStr);
                    }
                    if (isNotBlank(endStr) && isNumeric(endStr)) {
                        end = Long.parseLong(endStr);
                    }
                    length = end - start + 1;
                    response.setHeader("Accept-Ranges", "bytes");
                    response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + size);
                    map.put("start", start);
                    map.put("end", end);
                }
            }
        }
        response.setHeader("Content-length", length + "");
        return map;
    }

    private String getPublicObjectUrl(String bucket, String filePath, String fileName) {
        String serverAddress = globalProperties.getServerAddress();
        String objectPath = filePath + DEFAULT_FILE_PATH + fileName;
        if (filePath.equals(DEFAULT_FILE_PATH)) {
            objectPath = DEFAULT_FILE_PATH + fileName;
        }
        return serverAddress + "/ajax/bucket/file/" + bucket + objectPath;
    }
}
