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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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

    @PostConstruct
    public void init() {
        logger.info("Use HotData mode Successful!");
        cleanKeysTask();
    }

    /**
     * 保存热点数据 key，且 保证热点数据 不会 超过 RANK_KEEP_SIZE
     * 热点 + 准热点key size = 2 * RANK_KEEP_SIZE
     * keys 共保存 4 * RANK_KEEP_SIZE 个，结构如下
     * | -- bigger ----> smaller |
     * |  -- RANK_KEEP_SIZE hot -- | -- RANK_KEEP_SIZE ready hot-- | -- 2 * RANK_KEEP_SIZE wait to remove --|
     * - 前 RANK_KEEP_SIZE 个是热点key
     * - 后续 RANK_KEEP_SIZE 个 是准热点key
     * - 最后 2 * RANK_KEEP_SIZE 个 是待清除的热点key，最大存活时间为：KEY_MAX_ALIVE_TIME
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
    private static final int RANK_KEY_KEEP_SIZE = 2 * RANK_DATA_KEEP_SIZE;
    /**
     * 待清除的热点 key 最大存活时间 单位 milliseconds
     */
    private static final int KEY_MAX_ALIVE_TIME = 60 * 1000;

    private static final Random random = new Random();

    @Override
    public InputStream getObjectIsByObjectId(String objectFileId) {
        long start = System.currentTimeMillis();
        String data = hashOperations.get(HOT_DATA_KEY, objectFileId);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        logger.debug("get cache data from redis spend time: [{}] ms ", (System.currentTimeMillis() - start));
        return new ByteArrayInputStream(ConvertTools.hexStringToByte(data));
    }

    /**
     * 更新 objectFileId score
     *
     * @param objectFileId file id
     */
    @Override
    public void updateRankKeyScore(String objectFileId) {
        Set<String> ids = zSetOperations.reverseRange(Z_SET_KEY, 0L, -1L);
        // zset 不为空 并且 大于 hash 最大容量 RANK_DATA_KEEP_SIZE
        if (!CollectionUtils.isEmpty(ids) && ids.size() >= RANK_DATA_KEEP_SIZE) {
            // key 是否存在于 zset 集合中
            boolean exist = ids.stream().filter(s -> s.equals(objectFileId)).findAny().orElse(null) != null;
            if (!exist && ids.size() >= 2 * RANK_KEY_KEEP_SIZE) {
                // 如果 ids >= 2 * RANK_KEY_KEEP_SIZE, 在  [0, 2 * RANK_KEY_KEEP_SIZE] 随机删除一个
                // key 超过 RANK_KEY_KEEP_SIZE 的部分， 由定时任务clean
                int removeIndex = random.nextInt(RANK_KEY_KEEP_SIZE);
                zSetOperations.removeRange(Z_SET_KEY, removeIndex, removeIndex);
            }
        }
        // zset size < RANK_DATA_KEEP_SIZE 直接 put key and data
        zSetOperations.incrementScore(Z_SET_KEY, objectFileId, 1D);
        Double score = zSetOperations.score(Z_SET_KEY, objectFileId);
        logger.debug("update rank key:{} score: {}", objectFileId, score);

    }

    /**
     * 尝试更新缓存，保证 hashOperations 缓存的数据 始终是 zSetOperations 的前  RANK_DATA_KEEP_SIZE 个
     *
     * @param objectFileId objectFileId
     * @param dataInput    data
     */
    @Override
    public void trySetObject(String objectFileId, byte[] dataInput) {
        if (dataInput.length > 0) {
            long start = System.currentTimeMillis();
            updateRankKeyScore(objectFileId);
            String data = ConvertTools.bytesToHexString(dataInput);
            // 存在于 zset 已有的集合中，如果在前 RANK_DATA_KEEP_SIZE ，更新到 hash
            Set<String> rankIds = zSetOperations.reverseRange(Z_SET_KEY, 0L, RANK_DATA_KEEP_SIZE - 1L);
            if (!CollectionUtils.isEmpty(rankIds) && rankIds.stream().filter(s -> s.equals(objectFileId)).findAny().orElse(null) != null) {
                // 查看 keys 与 rankIds 的差集
                Set<String> keys = hashOperations.keys(HOT_DATA_KEY);
                keys.removeAll(rankIds);
                if (!CollectionUtils.isEmpty(keys)) {
                    keys.forEach(key -> hashOperations.delete(HOT_DATA_KEY, key));
                }
                hashOperations.put(HOT_DATA_KEY, objectFileId, data);
            }
            logger.debug("set cache data to redis spend time: [{}] ms ", (System.currentTimeMillis() - start));
        }
    }

    /**
     * 每 KEY_MAX_ALIVE_TIME 秒 清除  超过 2 * RANK_KEY_KEEP_SIZE 的key
     * 延迟 10s 开始
     */
    public void cleanKeysTask() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Long size = zSetOperations.size(Z_SET_KEY);
                if (size != null && size > 2 * RANK_KEY_KEEP_SIZE) {
                    long deleteSize = size - 2 * RANK_DATA_KEEP_SIZE;
                    zSetOperations.removeRange(Z_SET_KEY, 0, deleteSize - 1);
                    logger.info("hot data cache clean task remove keys: {}", deleteSize);
                }
            }
        }, 10 * 1000, KEY_MAX_ALIVE_TIME);
    }

}
