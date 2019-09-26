package com.berry.oss.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 防盗链，http referer 白名单设置
 * </p>
 *
 * @author HiCooper
 * @since 2019-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RefererInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * bucket id
     */
    private String bucketId;

    /**
     * 白名单
     */
    private String whiteList;
    /**
     * 黑名单
     */
    private String blackList;

    /**
     * 允许空 Referer
     */
    private Boolean allowEmpty;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
