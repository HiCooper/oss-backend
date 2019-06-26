package com.berry.oss.core.entity;

import java.time.LocalDateTime;
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
 * @since 2019-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AccessKeyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 密钥id(AK)
     */
    private String accessKeyId;

    /**
     * SK
     */
    private String accessKeySecret;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 密钥状态，启用，禁用
     */
    private Boolean state;


}
