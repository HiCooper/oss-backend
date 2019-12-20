package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.module.dto.HotObjectStatisVo;
import com.berry.oss.module.vo.BucketStatisticsInfoVo;
import com.berry.oss.service.IBucketService;
import com.berry.oss.service.IStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private final IStatisticsService statisticsService;
    private final IBucketService bucketService;

    public StatisticsController(IStatisticsService statisticsService, IBucketService bucketService) {
        this.statisticsService = statisticsService;
        this.bucketService = bucketService;
    }

    @GetMapping("overview.json")
    @ApiOperation("获取首页概览数据")
    public Result<List<BucketStatisticsInfoVo>> overviewHome() {
        List<BucketStatisticsInfoVo> bucketUseInfo = bucketService.getBucketUseInfo();
        return ResultFactory.wrapper(bucketUseInfo);
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
