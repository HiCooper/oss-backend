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
 * @since 2019-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ObjectHash implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * hash值
     */
    private String hash;

    /**
     * 文件对象id
     */
    private String fileId;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 被引用次数
     */
    private Integer referenceCount;

    /**
     * 创建时间
     */
    private Date createTime;


}
