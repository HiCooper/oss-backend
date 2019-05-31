package com.berry.oss.security.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * <p>
 *
 * </p>
 *
 * @author HiCooper
 * @since 2018-12-02
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 激活状态
     */
    private boolean activated = false;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 上次修改时间
     */
    private LocalDateTime updateTime;

}
