package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.utils.ObjectId;
import com.berry.oss.core.entity.ShardInfo;
import com.berry.oss.core.service.IShardInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
import com.berry.oss.service.IDataSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ReedSolomonEncoderService reedSolomonEncoderService;

    @Resource
    private ReedSolomonDecoderService reedSolomonDecoderService;

    private final IShardInfoDaoService shardInfoDaoService;

    public DataSaveServiceImpl(IShardInfoDaoService shardInfoDaoService) {
        this.shardInfoDaoService = shardInfoDaoService;
    }

    @Override
    public String saveObject(InputStream inputStream, long size, String hash, String fileName, String bucketName, String username) throws IOException {
        String json = reedSolomonEncoderService.writeData(inputStream, fileName, bucketName, username);
        String fileId = ObjectId.get();
        // 保存对象信息
        ShardInfo shardInfo = new ShardInfo();
        shardInfo.setFileId(fileId);
        shardInfo.setHash(hash);
        shardInfo.setFileName(fileName);
        shardInfo.setShardJson(json);
        shardInfo.setSize(size);
        shardInfoDaoService.save(shardInfo);
        return fileId;

    }

    @Override
    public ObjectResource getObject(String objectId) {
        ShardInfo shardInfo = shardInfoDaoService.getOne(new QueryWrapper<ShardInfo>().eq("file_id", objectId));
        if (shardInfo == null) {
            logger.error("文件不存在：{}", objectId);
            return null;
        }
        String shardJson = shardInfo.getShardJson();
        InputStream inputStream = reedSolomonDecoderService.readData(shardJson);
        if (inputStream == null) {
            logger.error("文件损坏或丢失:{}", objectId);
            return null;
        }
        return new ObjectResource()
                .setCreateTime(shardInfo.getCreateTime())
                .setFileId(shardInfo.getFileId())
                .setFileName(shardInfo.getFileName())
                .setFileSize(shardInfo.getSize())
                .setHash(shardInfo.getHash())
                .setInputStream(inputStream);
    }
}
