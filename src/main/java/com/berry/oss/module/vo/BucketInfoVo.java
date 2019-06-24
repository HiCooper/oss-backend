package com.berry.oss.module.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Title BucketInfoVo
 * Description
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/14 15:41
 */
@Data
public class BucketInfoVo {
    /**
     * Bucket名称
     */
    private String name;

    /**
     * 读写权限
     */
    private String acl;


    /**
     *  区域id
     */
    private String regionCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
