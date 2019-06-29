package com.berry.oss.api;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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


    @GetMapping("overview.json")
    @ApiOperation("获取首页概览数据")
    public Result overviewHome() {
        // 磁盘容量，使用量
        // bucket 数量
        // 对象数量
        // 最大对象大小，最小对象大小，去掉最大最小后的平均大小
        // 最近30天的对象引用次数分布
        // 最近30天热点数据排行前10
        Map<String, Object> result = new HashMap<>(16);
        result.put("totalCapacity", 60);
        result.put("usedCapacity", 1);
        result.put("bucketCount", 1);
        result.put("objectCount", 4);
        result.put("objectMaxSize", "12.32MB");
        result.put("objectMinSize", "11KB");
        result.put("objectAverageSize", "432.12KB");

        Map<String, Integer> lastMonthObjectReferenceData = new HashMap<>(16);
        lastMonthObjectReferenceData.put("2019-05-28", 100);
        lastMonthObjectReferenceData.put("2019-05-29", 67);
        lastMonthObjectReferenceData.put("2019-05-30", 10);
        lastMonthObjectReferenceData.put("2019-05-31", 89);
        lastMonthObjectReferenceData.put("2019-06-01", 55);
        lastMonthObjectReferenceData.put("2019-06-02", 75);
        lastMonthObjectReferenceData.put("2019-06-03", 25);
        lastMonthObjectReferenceData.put("2019-06-04", 209);
        lastMonthObjectReferenceData.put("2019-06-05", 189);
        lastMonthObjectReferenceData.put("2019-06-06", 109);
        lastMonthObjectReferenceData.put("2019-06-07", 79);
        lastMonthObjectReferenceData.put("2019-06-08", 121);
        lastMonthObjectReferenceData.put("2019-06-09", 50);
        lastMonthObjectReferenceData.put("2019-06-10", 20);
        lastMonthObjectReferenceData.put("2019-06-11", 67);
        result.put("lastMonthObjectReferenceData", lastMonthObjectReferenceData);

        Map<String, Integer> lastMonthHotObjectRanking = new HashMap<>(16);
        lastMonthHotObjectRanking.put("test.jpg", 519);
        lastMonthHotObjectRanking.put("main.jpg", 500);
        lastMonthHotObjectRanking.put("bg.jpg", 482);
        lastMonthHotObjectRanking.put("time.jpg", 460);
        lastMonthHotObjectRanking.put("size.jpg", 428);
        lastMonthHotObjectRanking.put("a.ppt", 100);
        lastMonthHotObjectRanking.put("b.doc", 89);
        lastMonthHotObjectRanking.put("c.xlsx", 70);
        lastMonthHotObjectRanking.put("d.pdf", 64);
        lastMonthHotObjectRanking.put("e.svg", 50);
        result.put("lastMonthHotObjectRanking", lastMonthHotObjectRanking);

        return ResultFactory.wrapper(result);
    }
}
