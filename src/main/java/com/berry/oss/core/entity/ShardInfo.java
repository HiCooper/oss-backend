package com.berry.oss.core.entity;

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
 * @since 2019-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShardInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 对象id
     */
    private String fileId;

    /**
     * hash
     */
    private String hash;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 分片json
     */
    private String shardJson;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 单机模式
     */
    private Boolean singleton;

    /**
     * 创建时间
     */
    private Date createTime;


}
