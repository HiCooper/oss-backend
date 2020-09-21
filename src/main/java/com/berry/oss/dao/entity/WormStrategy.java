package com.berry.oss.dao.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author HiCooper
 * @since 2020-09-21
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
    private String targetId;

    /**
     * 策略应用对象类型（bucket/object）
     */
    private String targetType;

    /**
     * 保存期限值
     */
    private Integer retentionPeriodVal;

    /**
     * 保存期限单位（天/月/年）
     */
    private String retentionPeriodUnit;

    /**
     * 保存期限描述（1天到70年）
     */
    private String retentionPeriodDesc;

    /**
     * 策略计算开始时间
     */
    private Date startDate;

    /**
     * 策略失效日期
     */
    private Date deadDate;

    /**
     * 策略状态(InProgress/Locked)
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
