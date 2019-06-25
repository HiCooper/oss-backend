package com.berry.oss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.utils.HttpClient;
import com.berry.oss.common.utils.ObjectId;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.config.GlobalProperties;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.entity.ShardInfo;
import com.berry.oss.core.service.IShardInfoDaoService;
import com.berry.oss.module.dto.ObjectResource;
import com.berry.oss.remote.WriteShardResponse;
import com.berry.oss.service.IDataSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    private final GlobalProperties globalProperties;

    public DataSaveServiceImpl(IShardInfoDaoService shardInfoDaoService, GlobalProperties globalProperties) {
        this.shardInfoDaoService = shardInfoDaoService;
        this.globalProperties = globalProperties;
    }

    @Override
    public String saveObject(InputStream inputStream, long size, String hash, String fileName, BucketInfo bucketInfo, String username) throws IOException {
        String json;
        boolean singletonMode = false;
        GlobalProperties.Singleton singleton = globalProperties.getSingleton();
        if (singleton.isOpen() && !StringUtils.isAnyBlank(singleton.getAddress(), singleton.getRegion())) {
            // 单机模式
            singletonMode = true;
            String address = singleton.getAddress();
            int available = inputStream.available();
            byte[] data = new byte[available];
            int read = inputStream.read(data);
            if (read != available) {
                throw new RuntimeException("数据流读取错误");
            }
            Map<String, Object> params = new HashMap<>(16);
            params.put("username", username);
            params.put("bucketName", bucketInfo.getName());
            params.put("fileName", fileName);
            params.put("data", data);
            String writePath = HttpClient.doPost("http://" + address + "/data/write", params);
            json = JSON.toJSONString(new WriteShardResponse("http://" + address + "/data/read", writePath));
        } else {
            json = reedSolomonEncoderService.writeData(inputStream, fileName, bucketInfo, username);
        }
        String fileId = ObjectId.get();
        // 保存对象信息
        ShardInfo shardInfo = new ShardInfo();
        shardInfo.setSingleton(singletonMode);
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
        InputStream inputStream;
        String shardJson = shardInfo.getShardJson();
        if (shardInfo.getSingleton() != null && shardInfo.getSingleton()) {
            // 单机模式
            JSONObject object = JSONObject.parseObject(shardJson);
            String url = object.getString("url");
            String path = object.getString("path");
            Map<String, Object> params = new HashMap<>(16);
            params.put("path", path);
            byte[] bytes = HttpClient.doGet(url, params);
            if (bytes == null) {
                logger.error("读取错误或数据为空，url:{}, path:{}", url, path);
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                logger.error("构造返回数据流失败");
                return null;
            }
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        } else {
            inputStream = reedSolomonDecoderService.readData(shardJson);
        }

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
