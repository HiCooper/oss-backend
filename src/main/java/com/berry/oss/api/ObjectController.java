package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.module.mo.*;
import com.berry.oss.module.vo.GenerateUrlWithSignedVo;
import com.berry.oss.module.vo.ObjectInfoVo;
import com.berry.oss.service.IObjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Title ObjectController
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/4 15:34
 */
@RestController
@RequestMapping("ajax/bucket")
@Api(tags = "对象管理")
public class ObjectController {

    private static final String DEFAULT_FILE_PATH = "/";
    private static final String PRIVATE = "PRIVATE";

    private final IObjectInfoDaoService objectInfoDaoService;

    private final IObjectService objectService;

    @Autowired
    public ObjectController(
            IObjectInfoDaoService objectInfoDaoService,
            IObjectService objectService) {
        this.objectInfoDaoService = objectInfoDaoService;
        this.objectService = objectService;
    }

    @GetMapping("list_objects.json")
    @ApiOperation("获取 Object 列表")
    public Result<List<ObjectInfo>> list(@RequestParam("bucket") String bucket,
                                         @RequestParam(value = "path", defaultValue = "/") String path,
                                         @RequestParam(value = "search", defaultValue = "") String search) {
        return ResultFactory.wrapper(objectService.list(bucket, path, search));
    }

    /**
     * @param bucket   bucket name
     * @param files    file
     * @param acl      acl
     * @param filePath 所在路径  以 / 开头
     * @return msg
     * @throws IOException io exception
     */
    @PostMapping("create")
    @ApiOperation("创建对象")
    public Result create(
            @RequestParam("bucket") String bucket,
            @RequestParam("file") MultipartFile[] files,
            @RequestParam(value = "acl", defaultValue = PRIVATE) String acl,
            @RequestParam(value = "filePath", defaultValue = DEFAULT_FILE_PATH) String filePath) throws Exception {
        List<ObjectInfoVo> objectInfoVos = objectService.create(bucket, files, acl, filePath);
        return ResultFactory.wrapper(objectInfoVos);
    }

    /**
     * 以字节数组格式创建对象，文件名必须
     *
     * @param uploadObjectByteMo 请求对象
     * @return 结果
     */
    @PostMapping("upload_byte.json")
    @ApiOperation("以字节数组格式创建对象")
    public Result uploadByte(@Validated @RequestBody UploadObjectByteMo uploadObjectByteMo) throws Exception {
        ObjectInfoVo objectInfoVo = objectService.uploadByte(
                uploadObjectByteMo.getBucket(),
                uploadObjectByteMo.getFilePath(),
                uploadObjectByteMo.getFileName(),
                uploadObjectByteMo.getData(),
                uploadObjectByteMo.getAcl()
        );
        return ResultFactory.wrapper(objectInfoVo);
    }

    @PostMapping("upload_base64.json")
    @ApiOperation("以base64字符串格式创建对象")
    public Result uploadByBase64Str(@Validated @RequestBody UploadObjectBase64Mo uploadObjectBase64Mo) throws Exception {
        ObjectInfoVo objectInfoVo = objectService.uploadByBase64Str(
                uploadObjectBase64Mo.getBucket(),
                uploadObjectBase64Mo.getFilePath(),
                uploadObjectBase64Mo.getFileName(),
                uploadObjectBase64Mo.getData(),
                uploadObjectBase64Mo.getAcl()
        );
        return ResultFactory.wrapper(objectInfoVo);
    }

    @GetMapping("detail.json")
    @ApiOperation("获取对象描述")
    public Result<ObjectInfo> detail(@RequestParam String objectId) {
        return ResultFactory.wrapper(objectInfoDaoService.getOne(new QueryWrapper<ObjectInfo>().eq("file_id", objectId)));
    }

    /**
     * 获取文件头部信息
     *
     * @param path       文件路径
     * @param bucket     存储空间名称
     * @param objectName 文件名 必填
     * @return 头部信息
     */
    @ApiOperation("获取文件头部信息")
    @GetMapping("head_object.json")
    public Result<Map<String, Object>> getObjectHead(
            @RequestParam(value = "bucket") String bucket,
            @RequestParam(value = "path", defaultValue = DEFAULT_FILE_PATH) String path,
            @RequestParam(value = "objectName") String objectName) {
        return ResultFactory.wrapper(objectService.getObjectHead(bucket, path, objectName));
    }

    /**
     * 根据过期时间 生成对象临时访问url
     *
     * @param mo 请求参数
     * @return 访问对象url
     * @throws Exception 编码异常，签名异常
     */
    @ApiOperation("根据过期时间 生成对象临时访问url")
    @PostMapping("generate_url_with_signed.json")
    public Result<GenerateUrlWithSignedVo> generateUrlWithSigned(@RequestBody GenerateUrlWithSignedMo mo) throws Exception {
        return ResultFactory.wrapper(objectService.generateUrlWithSigned(mo.getBucket(), mo.getObjectPath(), mo.getTimeout()));
    }

    @ApiOperation("生成对象临时下载url 60s 有效")
    @PostMapping("generate_download_url.json")
    public Result generateDownloadUrl(@Validated @RequestBody GenerateDownloadUrlMo mo) throws Exception {
        return ResultFactory.wrapper(objectService.generateDownloadUrl(mo.getBucket(), mo.getObjectPath()));
    }

    @GetMapping(value = "file/{bucket}/**")
    @ApiOperation("获取对象(私有对象，需要临时口令，且限时访问；公开对象，直接访问，** 为对象相对根路径的全路径，包含对象名)")
    public void getObject(
            @PathVariable("bucket") String bucket,
            @RequestParam(value = "Expires", required = false) String expiresTime,
            @RequestParam(value = "OSSAccessKeyId", required = false) String ossAccessKeyId,
            @RequestParam(value = "Signature", required = false) String signature,
            @RequestParam(value = "Download", required = false) Boolean download,
            HttpServletResponse response, HttpServletRequest servletRequest, WebRequest request) throws Exception {
        objectService.getObject(bucket, expiresTime, ossAccessKeyId, signature, download, response, servletRequest, request);
    }

    @PostMapping("create_folder.json")
    @ApiOperation("新建目录，支持同事创建多级目录")
    public Result createFolder(@Validated @RequestBody CreateFolderMo mo) {
        objectService.createFolder(mo.getBucket(), mo.getFolder());
        return ResultFactory.wrapper();
    }

    @PostMapping("delete_objects.json")
    @ApiOperation("删除对象或文件夹")
    public Result delete(@Validated @RequestBody DeleteObjectsMo mo) {
        objectService.delete(mo.getBucket(), mo.getObjects());
        return ResultFactory.wrapper();
    }

    @PostMapping("set_object_acl.json")
    @ApiOperation("更新对象读写权限")
    public Result updateObjectAcl(@Validated @RequestBody UpdateObjectAclMo mo) {
        return ResultFactory.wrapper(objectService.updateObjectAcl(mo.getBucket(), mo.getObjectPath(), mo.getObjectName(), mo.getAcl()));
    }

}
