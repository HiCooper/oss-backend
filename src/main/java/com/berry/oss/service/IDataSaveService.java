package com.berry.oss.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 23:25
 * fileName：IDataSaveService
 * Use：
 */
public interface IDataSaveService {

    /**
     * 保存对象
     *
     * @param inputStream 输入流
     * @param fileName 文件名
     * @return
     */
    String saveObject(InputStream inputStream, String fileName) throws IOException;

    /**
     * 获取对象
     *
     * @param objectId 对象di
     * @return
     */
    InputStream getObject(String objectId) throws IOException;
}
