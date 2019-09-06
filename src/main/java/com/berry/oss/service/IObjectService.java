package com.berry.oss.service;

import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.module.vo.GenerateUrlWithSignedVo;
import com.berry.oss.module.vo.ObjectInfoVo;
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
     * @param file    待上传 file
     * @param acl      文件acl
     * @param filePath 相对路径
     * @return obj
     * @throws Exception 获取文件流IO异常
     */
    ObjectInfoVo create(String bucket, MultipartFile file, String acl, String filePath) throws Exception;

    /**
     * 新建目录
     *
     * @param bucket bucket name
     * @param folder 目录全路径（可能是多级路径）
     */
    void createFolder(String bucket, String folder);

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
     * 根据过期时间 生成对象临时访问url
     * 请求url时，先验证签名，后验证 accessKeyId 由服务器签发，再验证过期时间是否有效，有效则返回对象数据
     *
     * @param bucket     bucket name
     * @param objectPath 对象全路径
     * @param timeout    过期时间
     * @return ossAccessKeyId： 私钥加密的用户id，保证由服务器签发，不被假冒
     * Signature：Expires 和 OSSAccessKeyId 参数进行 base64(md5(str)) 签名，保证不被篡改,
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

    /**
     * 生成批量下载url
     *
     * @param bucket     bucket name
     * @param objectPath 对象全路径
     * @return url list
     * @throws Exception 编码异常，密钥生成异常
     */
    List<String> generateDownloadUrl(String bucket, List<String> objectPath) throws Exception;

    /**
     * byte 类型对象创建
     *
     * @param bucket   bucket name
     * @param filePath 保存路径
     * @param fileName 对象名
     * @param data     对象数据体
     * @param acl      ACL
     * @return object info
     * @throws IOException 编码异常，密钥生成异常
     */
    ObjectInfoVo uploadByte(String bucket, String filePath, String fileName, byte[] data, String acl) throws Exception;

    /**
     * base64 字符串类型 创建对象
     *
     * @param bucket   bucket name
     * @param filePath 保存路径
     * @param fileName 对象名
     * @param data     对象数据体
     * @param acl      ACL
     * @return object info
     * @throws IOException 编码异常，密钥生成异常
     */
    ObjectInfoVo uploadByBase64Str(String bucket, String filePath, String fileName, String data, String acl) throws Exception;
}
