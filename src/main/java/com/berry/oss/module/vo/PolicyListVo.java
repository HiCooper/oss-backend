package com.berry.oss.module.vo;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author HiCooper
 * @since 2019-07-15
 */
@Data
public class PolicyListVo {

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
    private List<String> resource;

    /**
     * 授权用户
     */
    private List<String> principal;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

}
