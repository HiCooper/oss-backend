package com.berry.oss.service;

import com.berry.oss.module.dto.ObjectResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 23:25
 * fileName：IDataSaveService
 * Use：数据存储服务，该接口整存整取
 */
public interface IDataSaveService {

    /**
     * 保存对象
     *
     * @param inputStream 输入流
     * @param size        size
     * @param hash        hash
     * @param fileName    文件名
     * @param bucketName  存储空间名
     * @param username    用户名
     * @return fileId 对象唯一id
     * @throws IOException
     */
    String saveObject(InputStream inputStream, long size, String hash, String fileName, String bucketName, String username) throws IOException;

    /**
     * 获取对象
     *
     * @param objectId 对象id
     * @return 资源对象
     * @throws IOException
     */
    ObjectResource getObject(String objectId) throws IOException;
}
