package com.berry.oss.service;

import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.module.vo.GenerateUrlWithSignedVo;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:33
 * fileName：IObjectService
 * Use：
 */
public interface IObjectService {

    List<ObjectInfo> list(String bucket, String path, String search);


    /**
     * 上传 Object
     *
     * @param bucket   bucket name
     * @param file     待上传 file
     * @param acl      文件acl
     * @param filePath 相对路径
     * @return 响应结果
     * @throws IOException 获取文件流IO异常
     */
    String create(String bucket, MultipartFile file, String acl, String filePath) throws IOException;

    /**
     * 新建目录
     *
     * @param bucket     bucket name
     * @param objectName 目录全路径（可能是多级路径）
     */
    void createFolder(String bucket, String objectName);

    /**
     * 获取 Object
     *
     * @param bucket         bucket name
     * @param expiresTime    过期时间
     * @param ossAccessKeyId 临时访问 keyId
     * @param signature      url签名
     * @param download       是否下载
     * @param response       响应对象
     * @param servletRequest servlet请求对象
     * @param request        web 请求对象
     * @throws IOException 获取文件流IO异常
     */
    void getObject(
            String bucket,
            String expiresTime,
            String ossAccessKeyId,
            String signature,
            Boolean download,
            HttpServletResponse response, HttpServletRequest servletRequest, WebRequest request) throws IOException;

    /**
     * 获取对象 响应头信息
     *
     * @param bucket     bucket name
     * @param path       对象路径
     * @param objectName 对象名
     * @return map
     */
    Map<String, Object> getObjectHead(String bucket, String path, String objectName);

    /**
     * 根据过期时间 生成对象零时访问url
     *
     * @param bucket     bucket name
     * @param objectPath 对象全路径
     * @param timeout    过期时间
     * @return url信息
     * @throws Exception 签名异常 或 URL编码异常
     */
    GenerateUrlWithSignedVo generateUrlWithSigned(String bucket, String objectPath, Integer timeout) throws Exception;

    /**
     * 删除 object
     *
     * @param bucket  bucket name
     * @param objects 对象全路径，多个用英文逗号隔开
     */
    void delete(String bucket, String objects);

    /**
     * 更新 对象 ACL
     *
     * @param bucket     bucket name
     * @param objectPath 对象路径
     * @param objectName 对象名
     * @param acl        文件ACL
     * @return 成功与否
     */
    Boolean updateObjectAcl(String bucket, String objectPath, String objectName, String acl);
}
