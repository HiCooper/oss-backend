package com.berry.oss.service.impl;

import com.berry.oss.service.IDataSaveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 23:25
 * fileName：DataSaveServiceImpl
 * Use：数据存储服务
 */
@Service
public class DataSaveServiceImpl implements IDataSaveService {

    @Resource
    private ReedSolomonEncoderService reedSolomonEncoderService;

    @Resource
    private ReedSolomonDecoderService reedSolomonDecoderService;

    @Override
    public String saveObject(InputStream inputStream, String fileName) throws IOException {
        reedSolomonEncoderService.writeData(inputStream);
        return null;
    }

    @Override
    public InputStream getObject(String objectId) throws IOException {
        return reedSolomonDecoderService.readData(objectId);
    }
}
