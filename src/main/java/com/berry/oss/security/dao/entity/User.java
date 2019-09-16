package com.berry.oss.security.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author HiCooper
 * @since 2018-12-02
 */
@Accessors(chain = true)
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
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 激活状态，默认未激活
     */
    private boolean activated = false;

    /**
     * 启用状态，默认启用
     */
    private boolean enabled = true;

    /**
     * 锁定状态，默认未锁定
     */
    private boolean locked = false;

    /**
     * 过期时间, null 表示不过期
     */
    private Date expired;


    /**
     * 创建时间（数据库自动更新）
     */
    private Date createTime;

    /**
     * 上次修改时间（数据库自动更新）
     */
    private Date updateTime;

}
