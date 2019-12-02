package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.utils.ObjectId;
import com.berry.oss.config.GlobalProperties;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.entity.ShardInfo;
import com.berry.oss.dao.service.IShardInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
import com.berry.oss.service.IDataService;
import com.berry.oss.service.IHotDataCacheService;
import com.berry.oss.service.IShardSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 23:25
 * fileName：DataServiceImpl
 * Use：数据存储服务
 */
@Service
public class DataServiceImpl implements IDataService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 缓存对象最大容量  1M
     */
    private static final int MAX_CACHE_FILE_SIZE = 1024 * 1024;


    @Resource
    private ReedSolomonEncoderService reedSolomonEncoderService;

    @Resource
    private ReedSolomonDecoderService reedSolomonDecoderService;

    private final IHotDataCacheService hotDataCacheService;
    private final IShardInfoDaoService shardInfoDaoService;
    private final IShardSaveService shardSaveService;

    private final GlobalProperties globalProperties;

    public DataServiceImpl(IShardInfoDaoService shardInfoDaoService, GlobalProperties globalProperties, IShardSaveService shardSaveService, IHotDataCacheService hotDataCacheService) {
        this.shardInfoDaoService = shardInfoDaoService;
        this.globalProperties = globalProperties;
        this.shardSaveService = shardSaveService;
        this.hotDataCacheService = hotDataCacheService;
    }

    @Override
    public String saveObject(String filePath, InputStream inputStream, long size, String hash, String fileName, BucketInfo bucketInfo) throws IOException {
        String fileId = ObjectId.get();
        boolean singleton = globalProperties.isSingleton();
        String json;
        if (singleton) {
            // 单机模式
            int available = inputStream.available();
            byte[] data = new byte[available];
            int read = inputStream.read(data);
            if (read != available) {
                throw new RuntimeException("数据流读取错误");
            }
            // 单机模式 分片信息仅保存本地路径
            json = shardSaveService.writeShard(filePath, bucketInfo.getName(), fileName, data);
        } else {
            // 分布式模式
            json = reedSolomonEncoderService.writeData(filePath, inputStream, fileName, bucketInfo);
        }
        // 保存对象信息
        saveShardInfo(size, hash, fileName, fileId, singleton, json);
        return fileId;
    }

    @Override
    public String saveObject(String filePath, byte[] data, long size, String hash, String fileName, BucketInfo bucketInfo) throws IOException {
        logger.debug("ready to save object,fileName: {}, filePath:{}", fileName, filePath);
        String fileId = ObjectId.get();
        boolean singleton = globalProperties.isSingleton();
        String json;
        if (singleton) {
            // 单机模式 分片信息仅保存本地路径
            json = shardSaveService.writeShard(filePath, bucketInfo.getName(), fileName, data);
        } else {
            // 分布式模式
            json = reedSolomonEncoderService.writeData(filePath, data, fileName, bucketInfo);
        }
        // 保存对象信息
        saveShardInfo(size, hash, fileName, fileId, singleton, json);
        return fileId;
    }

    @Override
    public ObjectResource getObject(String bucket, String objectId) throws IOException {
        ShardInfo shardInfo = shardInfoDaoService.getOne(new QueryWrapper<ShardInfo>().eq("file_id", objectId));
        if (shardInfo == null) {
            logger.error("文件不存在：{}", objectId);
            return null;
        }
        String shardJson = shardInfo.getShardJson();
        InputStream cacheIs = null;
        boolean useHotDataCache = globalProperties.isHotDataCache() && shardInfo.getSize() <= MAX_CACHE_FILE_SIZE;
        if (useHotDataCache) {
            try {
                // 查询缓存
                cacheIs = hotDataCacheService.getObjectIsByObjectId(objectId);
            } catch (Exception e) {
                logger.error("get object from cache throw exception,msg:[{}] ! ObjectId:[{}]", e.getLocalizedMessage(), objectId);
            }
        }
        if (cacheIs == null) {
            logger.debug("get object from disk or net ...");
            if (shardInfo.getSingleton() != null && shardInfo.getSingleton()) {
                // 单机模式
                byte[] bytes = shardSaveService.readShard(shardJson);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    outputStream.write(bytes);
                } catch (Exception e) {
                    logger.error("构造返回数据流失败");
                    return null;
                }
                cacheIs = new ByteArrayInputStream(outputStream.toByteArray());
            } else {
                // RS 分布式冗余模式
                cacheIs = reedSolomonDecoderService.readData(bucket, shardJson, objectId);
            }
            if (cacheIs == null) {
                logger.error("文件损坏或丢失:{}", objectId);
                return null;
            }

            if (useHotDataCache) {
                // 尝试设置缓存
                byte[] dataInput = StreamUtils.copyToByteArray(cacheIs);
                hotDataCacheService.trySetObject(objectId, dataInput);
                cacheIs = new ByteArrayInputStream(dataInput);
            }
        }
        return new ObjectResource()
                .setCreateTime(shardInfo.getCreateTime())
                .setFileId(shardInfo.getFileId())
                .setFileName(shardInfo.getFileName())
                .setFileSize(shardInfo.getSize())
                .setHash(shardInfo.getHash())
                .setInputStream(cacheIs);
    }

    private void saveShardInfo(long size, String hash, String fileName, String fileId,
                               boolean singleton, String json) {
        ShardInfo shardInfo = new ShardInfo();
        shardInfo.setFileId(fileId);
        shardInfo.setSingleton(singleton);
        shardInfo.setHash(hash);
        shardInfo.setFileName(fileName);
        shardInfo.setShardJson(json);
        shardInfo.setSize(size);
        shardInfoDaoService.save(shardInfo);
    }
}
