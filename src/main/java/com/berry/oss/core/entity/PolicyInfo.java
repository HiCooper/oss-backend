package com.berry.oss.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 
 * </p>
 *
 * @author HiCooper
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PolicyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 授权类型 1-只读，2-读写，3-完全控制，4-拒绝访问
     */
    private Integer actionType;

    /**
     * 授权资源
     */
    private String resource;

    /**
     * 授权用户
     */
    private String principal;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * bucket 名称
     */
    private String bucket;

    /**
     * 用户id
     */
    private Integer userId;


}
