package com.berry.oss.service.impl;

import com.berry.oss.common.utils.DateUtils;
import com.berry.oss.module.dto.HotObjectStatisVo;
import com.berry.oss.service.IBucketService;
import com.berry.oss.service.IStatisticsService;
import org.joda.time.LocalDate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/10/27 14:27
 * fileName：StatisticsServiceImpl
 * Use：
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {

    private static final String PREFIX = "daily-statistics:";

    private final IBucketService bucketService;

    @Resource
    private HashOperations<String, String, Object> hashOperations;

    public StatisticsServiceImpl(IBucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Override
    public void updateDailyStatistics(String bucket, String fileFullPath) {
        // 存储空间-时间-对象全路径-访问次数 30天有效期
        String toDay = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        String key = PREFIX + bucket + ":" + toDay;
        hashOperations.increment(key, fileFullPath, 1);
        hashOperations.getOperations().expireAt(key, LocalDate.now().plusDays(30).toDate());
    }

    @Override
    public Map<String, Long> lastThirtyDaysObjectQuery(String bucket) {
        String keyPart = PREFIX + bucket + ":";
        LocalDate now = LocalDate.now();
        Map<String, Long> result = new HashMap<>(40);
        for (int i = 0; i < 30; i++) {
            LocalDate localDate = now.minusDays(i);
            String date = DateUtils.formatDate(localDate.toDate(), "yyyy-MM-dd");
            String key = keyPart + date;
            List<Object> values = hashOperations.values(key);
            long count = 0;
            if (!CollectionUtils.isEmpty(values)) {
                for (Object value : values) {
                    count += Long.parseLong(value.toString());
                }
            }
            result.put(date, count);
        }
        return result;
    }

    @Override
    public List<HotObjectStatisVo> lastThirtyDaysHotObjectRanking(String bucket) {
        String keyPart = PREFIX + bucket + ":";
        LocalDate now = LocalDate.now();
        Map<String, Long> result = new HashMap<>(40);
        for (int i = 0; i < 30; i++) {
            LocalDate localDate = now.minusDays(i);
            String date = DateUtils.formatDate(localDate.toDate(), "yyyy-MM-dd");
            String key = keyPart + date;
            Map<String, Object> entries = hashOperations.entries(key);
            if (!entries.isEmpty()) {
                for (Map.Entry<String, Object> entryMap : entries.entrySet()) {
                    String key1 = entryMap.getKey();
                    Long value = Long.valueOf(entryMap.getValue().toString());
                    Long aLong = result.get(key1);
                    if (aLong != null) {
                        value += aLong;
                    }
                    result.put(key1, value);
                }
            }
        }
        List<HotObjectStatisVo> voList = new ArrayList<>();
        result.forEach((key, value) -> voList.add(new HotObjectStatisVo(key, value)));
        voList.sort(Comparator.comparing(HotObjectStatisVo::getCount).reversed());
        if (!CollectionUtils.isEmpty(voList) && voList.size() > 10) {
            return voList.subList(0, 10);
        }
        return voList;
    }
}
