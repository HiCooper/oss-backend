package com.berry.oss.module.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/12/20 17:26
 * fileName：BucketStatisticsInfoVo
 * Use：概览统计 bucket object 等统计信息
 */
@Data
public class BucketStatisticsInfoDto {

    /**
     * bucket name
     */
    private String bucketName;

    /**
     * object 个数
     */
    private Integer objectCount;

    /**
     * 最大 object size
     */
    private Long objectMaxSize;

    /**
     * 已使用空间
     */
    private Long usedCapacity;

    /**
     * 最小 object size
     */
    private Long objectMinSize;

    /**
     * object 平均大小（去掉一个最大值和最小值）
     */
    private Long objectAverageSize;
}
