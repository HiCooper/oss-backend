package com.berry.oss.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author HiCooper
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WormStrategy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

    /**
     * 策略目标对象ID
     */
    private String bucket;

    /**
     * 保存期限值, 单位天
     */
    private Integer retentionPeriodValue;

    /**
     * 保存期限描述（1天到70年）
     */
    private String retentionPeriodDesc;

    /**
     * 策略生效开始时间
     */
    private Date activeTime;

    /**
     * 策略状态(InProgress/Locked/Expired)
     */
    private String wormState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
