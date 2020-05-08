package com.berry.oss.service.impl;

import com.berry.oss.common.utils.ConvertTools;
import com.berry.oss.service.IHotDataCacheService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
 * Use：热点数据缓存，实际测试效果不佳（速度低于硬盘读取）, 主要用于分布式环境，在集中缓存数据上有优势
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

    private static final String HOT_DATA_KEY = "hot_data";
    private static final String Z_SET_KEY = "hot_data_rank";
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
     *
     * @param objectId  objectId
     * @param dataInput data
     * @throws IOException
     */
    @Override
    public void trySetObject(String objectId, byte[] dataInput) throws IOException {
        if (dataInput.length > 0) {
            long start = System.currentTimeMillis();
            String data = ConvertTools.bytesToHexString(dataInput);
            Set<String> ids = zSetOperations.reverseRange(Z_SET_KEY, 0L, -1L);

            // zset 不为空 并且 大于 hash 最大容量 RANK_DATA_KEEP_SIZE
            if (!CollectionUtils.isEmpty(ids) && ids.size() >= RANK_DATA_KEEP_SIZE) {
                // key 是否存在于 zset 集合中
                boolean exist = ids.stream().filter(s -> s.equals(objectId)).findAny().orElse(null) != null;
                if (!exist && ids.size() == RANK_KEY_KEEP_SIZE) {
                    // 不存在 ，并且 zset size = RANK_KEY_KEEP_SIZE , 先删除第一个，然后将新的 key put
                    zSetOperations.removeRange(Z_SET_KEY, 0, 0);
                }
                zSetOperations.incrementScore(Z_SET_KEY, objectId, 1D);
                if (exist) {
                    // 存在于 zset 已有的集合中，如果在前 RANK_DATA_KEEP_SIZE ，更新到 hash
                    Set<String> rankTenIds = zSetOperations.reverseRange(Z_SET_KEY, 0L, RANK_DATA_KEEP_SIZE - 1L);
                    if (!CollectionUtils.isEmpty(rankTenIds) && rankTenIds.stream().filter(s -> s.equals(objectId)).findAny().orElse(null) != null) {
                        // 查看 keys 与 rankTenIds 的差集
                        Set<String> keys = hashOperations.keys(HOT_DATA_KEY);
                        keys.removeAll(rankTenIds);
                        if (!CollectionUtils.isEmpty(keys)) {
                            hashOperations.delete(HOT_DATA_KEY, keys);
                        }
                        hashOperations.put(HOT_DATA_KEY, objectId, data);
                    }
                }
            } else {
                // zset size < RANK_DATA_KEEP_SIZE 直接 put key and data
                zSetOperations.incrementScore(Z_SET_KEY, objectId, 1D);
                hashOperations.put(HOT_DATA_KEY, objectId, data);
            }
            logger.debug("set cache data to redis spend time: [{}] ms ", (System.currentTimeMillis() - start));
        }
    }
}
