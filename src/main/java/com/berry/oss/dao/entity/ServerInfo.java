package com.berry.oss.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 服务器信息
 * </p>
 *
 * @author HiCooper
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * ip
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 机器可用容量，单位GB
     */
    private Integer capacity;

    /**
     * 备注信息，255
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
