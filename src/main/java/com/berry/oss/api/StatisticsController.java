package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.module.dto.HotObjectStatisVo;
import com.berry.oss.service.IStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title StatisticsController
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/4 15:43
 */
@RestController
@RequestMapping("ajax/statis")
@Api(tags = "统计信息")
public class StatisticsController {

    @Autowired
    private IStatisticsService statisticsService;

    @GetMapping("overview.json")
    @ApiOperation("获取首页概览数据")
    public Result overviewHome() {
        // 磁盘容量，使用量
        // bucket 数量
        // 对象数量
        // 最大对象大小，最小对象大小，去掉最大最小后的平均大小
        Map<String, Object> result = new HashMap<>(16);
        result.put("totalCapacity", 60);
        result.put("usedCapacity", 1);
        result.put("bucketCount", 1);
        result.put("objectCount", 4);
        result.put("objectMaxSize", "12.32MB");
        result.put("objectMinSize", "11KB");
        result.put("objectAverageSize", "432.12KB");
        return ResultFactory.wrapper(result);
    }

    @GetMapping("daily_query_times.json")
    @ApiOperation("bucket 访问频率")
    public Result dailyQueryTimes(@RequestParam String bucket) {
        Map<String, Long> lastMonthObjectReferenceData = statisticsService.lastThirtyDaysObjectQuery(bucket);
        return ResultFactory.wrapper(lastMonthObjectReferenceData);

    }

    @GetMapping("hot_data.json")
    @ApiOperation("bucket 热点数据")
    public Result hotData(@RequestParam String bucket) {
        List<HotObjectStatisVo> result = statisticsService.lastThirtyDaysHotObjectRanking(bucket);
        return ResultFactory.wrapper(result);
    }
}
