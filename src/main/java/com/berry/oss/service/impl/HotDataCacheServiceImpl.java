package com.berry.oss.service.impl;

import com.berry.oss.common.utils.ConvertTools;
import com.berry.oss.service.IHotDataCacheService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2019/11/29 14:41
 * fileName：HotDataCacheServiceImpl
 * Use：热点数据缓存，实际测试效果不佳（速度低于硬盘读取），暂不用
 */
@Service
public class HotDataCacheServiceImpl implements IHotDataCacheService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 保存热点数据 key，且 保证热点数据 不会 超过 RANK_KEEP_SIZE
     * 自身设置长度为 RANK_KEEP_SIZE * 4
     */
    @Resource
    private ZSetOperations<String, String> zSetOperations;

    /**
     * 保存热点数据内容
     */
    @Resource
    private HashOperations<String, String, String> hashOperations;

    private static final String HOT_DATA_KEY = "hot_data:";
    private static final String Z_SET_KEY = "hot_data_rank:";
    private static final int RANK_DATA_KEEP_SIZE = 20;
    private static final int RANK_KEY_KEEP_SIZE = 4 * RANK_DATA_KEEP_SIZE;

    @Override
    public InputStream getObjectIsByObjectId(String objectId) {
        long start = System.currentTimeMillis();
        String data = hashOperations.get(HOT_DATA_KEY, objectId);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        logger.debug("get cache data from redis spend time: [{}] ms ", (System.currentTimeMillis() - start));
        return new ByteArrayInputStream(ConvertTools.hexStringToByte(data));
    }

    /**
     * 尝试更新缓存，保证 hashOperations 缓存的数据 始终是 zSetOperations 的前  RANK_DATA_KEEP_SIZE 个
     * todo not finished
     * @param objectId    objectId
     * @param inputStream 输入流
     * @throws IOException
     */
    @Override
    public void trySetObject(String objectId, InputStream inputStream) throws IOException {
        if (inputStream.available() > 0) {
            long start = System.currentTimeMillis();
            byte[] bytes = StreamUtils.copyToByteArray(inputStream);
            String data = ConvertTools.bytesToHexString(bytes);
            Set<String> ids = zSetOperations.reverseRange(Z_SET_KEY, 0L, -1L);

            // 1. zset size < RANK_DATA_KEEP_SIZE 直接 put key and data
            if (ids == null || ids.size() < RANK_DATA_KEEP_SIZE) {
                zSetOperations.incrementScore(Z_SET_KEY, objectId, 1D);
                hashOperations.put(HOT_DATA_KEY, objectId, data);
            } else {
                boolean exist = ids.stream().filter(s -> s.equals(objectId)).findAny().orElse(null) != null;
                if (exist) {
                    // 存在于 zset 已有的集合中，直接更新 score，检查前十是否有变动，变动则更新到 hashOperations
                } else {
                    // 不存在与 zset 已有的集合中
                    if (ids.size() == RANK_KEY_KEEP_SIZE) {
                        // 2. zset size = RANK_KEY_KEEP_SIZE , 先删除 score 最小的一个，score 相等则删除最后一个，然后将新的 key put
                    } else if (ids.size() < RANK_KEY_KEEP_SIZE) {
                        // 直接put key
                    }
                }
            }
            zSetOperations.incrementScore(Z_SET_KEY, objectId, 1D);
            Long size = zSetOperations.zCard(Z_SET_KEY);
            if (size != null && size > RANK_DATA_KEEP_SIZE) {
                // 默认是 升序排，所以移除的是前部份
                Set<String> delRangeObjectIds = zSetOperations.range(Z_SET_KEY, 0, size - RANK_DATA_KEEP_SIZE - 1);
                hashOperations.delete(HOT_DATA_KEY, delRangeObjectIds);
                zSetOperations.remove(Z_SET_KEY, delRangeObjectIds);
            }
            logger.debug("set cache data to redis spend time: [{}] ms ", (System.currentTimeMillis() - start));
        }
    }
}
