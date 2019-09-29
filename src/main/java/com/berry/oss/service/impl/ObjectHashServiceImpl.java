package com.berry.oss.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.HttpClient;
import com.berry.oss.dao.entity.ObjectHash;
import com.berry.oss.dao.entity.ShardInfo;
import com.berry.oss.dao.service.IObjectHashDaoService;
import com.berry.oss.dao.service.IShardInfoDaoService;
import com.berry.oss.service.IObjectHashService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 21:04
 * fileName：ObjectHashServiceImpl
 * Use：
 */
@Service
public class ObjectHashServiceImpl implements IObjectHashService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IObjectHashDaoService objectHashDaoService;
    private final IShardInfoDaoService shardInfoDaoService;

    ObjectHashServiceImpl(IObjectHashDaoService objectHashDaoService, IShardInfoDaoService shardInfoDaoService) {
        this.objectHashDaoService = objectHashDaoService;
        this.shardInfoDaoService = shardInfoDaoService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String checkExist(String hash, Long dataLength) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>().eq("hash", hash).eq("locked", false);
        int count = objectHashDaoService.count(queryWrapper);
        if (count == 0) {
            return null;
        }
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        return one.getFileId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @Async("taskExecutor")
    public void increaseRefCountByHash(String hash, String fileId, Long size) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .eq("hash", hash)
                .eq("locked", false)
                .eq("file_id", fileId);
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        if (one == null) {
            one = new ObjectHash()
                    .setHash(hash)
                    .setFileId(fileId)
                    .setSize(size)
                    .setReferenceCount(0);
        }
        one.setReferenceCount(one.getReferenceCount() + 1);
        objectHashDaoService.saveOrUpdate(one);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @Async("taskExecutor")
    public void decreaseRefCountByHash(String hash) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>().eq("hash", hash).eq("locked", false);
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        if (one == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        int newCount = Math.max(one.getReferenceCount() - 1, 0);
        one.setReferenceCount(newCount);
        // 引用为 0  的索引，由定时任务程序去扫描整理删除 对应的数据和引用
        objectHashDaoService.updateById(one);
    }

    @Override
    public void scanNonReferenceObjectThenClean() {
        // 1. 查出所有空引用对象,将其锁定
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>().eq("reference_count", 0).eq("locked", false);
        List<ObjectHash> nonRefObjectList = objectHashDaoService.list(queryWrapper);
        nonRefObjectList.forEach(item -> item.setLocked(true));
        objectHashDaoService.updateBatchById(nonRefObjectList);
        // 2. 查出这些对象的数据位置
        List<String> fileIdList = nonRefObjectList.stream().map(ObjectHash::getFileId).collect(Collectors.toList());
        List<ShardInfo> shardInfos = shardInfoDaoService.list(new QueryWrapper<ShardInfo>().in("file_id", fileIdList));
        // 3. 删除数据
        shardInfos.forEach(item -> {
            String shardJson = item.getShardJson();
            Boolean singleton = item.getSingleton();
            if (singleton != null && singleton) {
                // 单机模式
                File tempFile = new File(shardJson);
                if (tempFile.exists() && tempFile.isFile()) {
                    FileUtils.deleteQuietly(tempFile);
                }
            } else {
                // 分布式多机模式
                JSONArray jsonArray = JSONArray.parseArray(shardJson);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject shard = jsonArray.getJSONObject(i);
                    String url = shard.getString("url");
                    String path = shard.getString("path");
                    String removeUrl = url.replaceAll("read", "delete");
                    Map<String, Object> params = new HashMap<>(16);
                    try {
                        params.put("path", URLEncoder.encode(path, StandardCharsets.UTF_8.name()));
                        HttpClient.doPost(removeUrl, params);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 4. 删除位置信息
    }
}
