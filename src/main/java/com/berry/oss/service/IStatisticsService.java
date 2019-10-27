package com.berry.oss.service;

import com.berry.oss.module.dto.HotObjectStatisVo;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/10/27 14:27
 * fileName：IStatisticsService
 * Use：
 */
public interface IStatisticsService {

    /**
     * 每日访问统计增加
     *
     * @param bucket bucket name
     * @param bucket fileFullPath fileFullPath
     */
    void updateDailyStatistics(String bucket, String fileFullPath);

    /**
     * 最近30天的对象访问次数
     *
     * @param bucket bucket name
     */
    Map<String, Long> lastThirtyDaysObjectQuery(String bucket);

    /**
     * 最近30天热点数据排行前10
     *
     * @param bucket bucket name
     * @return
     */
    List<HotObjectStatisVo> lastThirtyDaysHotObjectRanking(String bucket);
}
