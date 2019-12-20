package com.berry.oss.module.vo;

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
public class BucketStatisticsInfoVo {

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
    private String objectMaxSize;

    /**
     * 已使用空间
     */
    private String usedCapacity;

    /**
     * 最小 object size
     */
    private String objectMinSize;

    /**
     * object 平均大小（去掉一个最大值和最小值）
     */
    private String objectAverageSize;
}
