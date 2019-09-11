package com.berry.oss.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 授权类型 1-只读，2-读写，3-完全控制，4-拒绝访问
     */
    private Integer actionType;

    /**
     * 禁止或允许访问，禁止优先级高于允许Deny/Allow
     */
    private String effect;

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
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * bucket 名称
     */
    private String bucket;

}
