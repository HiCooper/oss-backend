package com.berry.oss.core.entity;

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
    @TableId(value = "access_key_id")
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
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 密钥状态，启用，禁用
     */
    private Boolean state;


}
