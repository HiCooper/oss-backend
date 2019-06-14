package com.berry.oss.module.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Title BucketListVo
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/14 15:41
 */
@Data
public class BucketListVo {
    /**
     * id主键
     */
    private String id;

    /**
     * Bucket名称
     */
    private String name;

    /**
     * 读写权限
     */
    private String acl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
