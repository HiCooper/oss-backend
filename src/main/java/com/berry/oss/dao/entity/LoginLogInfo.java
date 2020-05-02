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
 * @since 2019-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginLogInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 登录ip
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 登出时间
     */
    private Date logoutTime;


}
