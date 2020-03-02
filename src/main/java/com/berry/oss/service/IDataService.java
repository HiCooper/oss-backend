package com.berry.oss.service;

import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.module.dto.ObjectResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 23:25
 * fileName：IDataService
 * Use：数据存储服务，该接口整存整取
 */
public interface IDataService {

    /**
     * 保存对象
     *
     * @param filePath    保存路径
     * @param inputStream 输入流
     * @param size        size
     * @param hash        hash
     * @param fileName    文件名
     * @param bucketInfo  存储空间
     * @return fileId 对象唯一id
     * @throws IOException
     */
    String saveObject(String filePath, InputStream inputStream, long size, String hash, String fileName, BucketInfo bucketInfo) throws IOException;

    /**
     * 保存对象（字节数组）
     *
     * @param filePath   保存路径
     * @param data       data
     * @param size       size
     * @param hash       hash
     * @param fileName   文件名
     * @param bucketInfo 存储空间
     * @return fileId 对象唯一id
     * @throws IOException IO
     */
    String saveObject(String filePath, byte[] data, long size, String hash, String fileName, BucketInfo bucketInfo) throws IOException;

    /**
     * 获取对象,优先尝试从 redis 缓存读取，缓存击中失败，从File system 或者 net 获取
     *
     * @param bucket       name
     * @param objectFileId 对象id
     * @return 资源对象
     * @throws IOException IO
     */
    ObjectResource getObject(String bucket, String objectFileId) throws IOException;

    /**
     * 根据 fileId， 补充保存文件
     *
     * @param fileName   文件名
     * @param filePath   路径
     * @param fileId     文件id
     * @param file       文件
     * @param fileUrl fileUrl
     * @param bucketInfo bucketInfo
     * @throws IOException IO
     */
    void makeUpForLostData(String fileName, String filePath, String fileId, MultipartFile file, String fileUrl, BucketInfo bucketInfo) throws IOException;
}
