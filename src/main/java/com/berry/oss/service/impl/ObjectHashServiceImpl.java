package com.berry.oss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.HttpClient;
import com.berry.oss.dao.entity.ObjectHash;
import com.berry.oss.dao.entity.ObjectInfo;
import com.berry.oss.dao.entity.ShardInfo;
import com.berry.oss.dao.mapper.ObjectHashMapper;
import com.berry.oss.dao.service.IObjectHashDaoService;
import com.berry.oss.dao.service.IObjectInfoDaoService;
import com.berry.oss.dao.service.IShardInfoDaoService;
import com.berry.oss.service.IObjectHashService;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    private final IObjectHashDaoService objectHashDaoService;
    private final IShardInfoDaoService shardInfoDaoService;
    private final IObjectInfoDaoService objectInfoDaoService;


    ObjectHashServiceImpl(IObjectHashDaoService objectHashDaoService, IShardInfoDaoService shardInfoDaoService, IObjectInfoDaoService objectInfoDaoService) {
        this.objectHashDaoService = objectHashDaoService;
        this.shardInfoDaoService = shardInfoDaoService;
        this.objectInfoDaoService = objectInfoDaoService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String checkExist(String hash) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .eq("hash", hash)
                .eq("locked", false);
        int count = objectHashDaoService.count(queryWrapper);
        if (count == 0) {
            return null;
        }
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        return one.getFileId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void increaseRefCountByHash(String hash, String fileId, Long size) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .eq("hash", hash)
                .eq("locked", false);
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
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .eq("hash", hash)
                .eq("locked", false);
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        if (one == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        int newCount = Math.max(one.getReferenceCount() - 1, 0);
        one.setReferenceCount(newCount);
        // 引用为 0  的索引，由定时任务程序去扫描整理删除 对应的数据和引用
        objectHashDaoService.updateById(one);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @Async("taskExecutor")
    public void batchDecreaseRefCountByHash(List<String> hashList) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .in("hash", hashList)
                .eq("locked", false);
        List<ObjectHash> list = objectHashDaoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        try (SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false)) {
            ObjectHashMapper mapper = session.getMapper(ObjectHashMapper.class);
            list.forEach(item -> {
                int newCount = Math.max(item.getReferenceCount() - 1, 0);
                item.setReferenceCount(newCount);
                mapper.updateById(item);
            });
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
    }

    @Override
    public void scanNonReferenceObjectThenClean() {
        // 1. 查出所有空引用对象,将其锁定
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .eq("reference_count", 0)
                .eq("locked", false);
        List<ObjectHash> nonRefObjectHashList = objectHashDaoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(nonRefObjectHashList)) {
            logger.info("暂无需要清理的数据");
            return;
        }
        // 2. 再次根据 hash 进行对象引用查询确认
        nonRefObjectHashList.forEach(item -> {
            int count = objectInfoDaoService.count(new QueryWrapper<ObjectInfo>().eq("hash", item.getHash()));
            if (count != 0) {
                item.setReferenceCount(count);
            } else {
                item.setLocked(true);
            }
        });
        objectHashDaoService.updateBatchById(nonRefObjectHashList);
        // 3.过滤步骤2中更新引用大于0的hash对象
        nonRefObjectHashList = nonRefObjectHashList.stream().filter(s -> s.getReferenceCount() == 0).collect(Collectors.toList());
        // 4. 查出这些对象的数据位置
        List<String> fileIdList = nonRefObjectHashList.stream().map(ObjectHash::getFileId).collect(Collectors.toList());
        QueryWrapper<ShardInfo> dealShardQuery = new QueryWrapper<ShardInfo>().in("file_id", fileIdList);
        List<ShardInfo> shardInfos = shardInfoDaoService.list(dealShardQuery);
        // 5. 删除数据
        shardInfos.forEach(item -> {
            String shardJson = item.getShardJson();
            Boolean singleton = item.getSingleton();
            if (singleton != null && singleton) {
                // 单机模式
                File tempFile = new File(shardJson);
                if (tempFile.exists() && tempFile.isFile()) {
                    FileUtils.deleteQuietly(tempFile);
                    logger.info("删除文件成功：{}", tempFile.getPath());
                } else {
                    logger.info("文件不存在或非文件：{}", tempFile.getPath());
                }
            } else {
                // 分布式多机模式
                JSONArray jsonArray = JSON.parseArray(shardJson);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject shard = jsonArray.getJSONObject(i);
                    String url = shard.getString("url");
                    String path = shard.getString("path");
                    String removeUrl = url.replace("read", "delete");
                    Map<String, Object> params = new HashMap<>(16);
                    try {
                        params.put("path", URLEncoder.encode(path, StandardCharsets.UTF_8.name()));
                        HttpClient.doPost(removeUrl, params);
                        logger.info("删除分片文件成功：{} , {}", url, path);
                    } catch (UnsupportedEncodingException e) {
                        logger.info("删除分片文件失败：{} , {}, msg:{}", url, path, e.getMessage());
                    }
                }
            }
        });
        // 4. 删除 hash 记录
        objectHashDaoService.removeByIds(nonRefObjectHashList.stream().map(ObjectHash::getId).collect(Collectors.toList()));
        // 5. 删除位置信息
        shardInfoDaoService.remove(dealShardQuery);
    }
}
