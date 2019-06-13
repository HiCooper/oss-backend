package com.berry.oss.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ObjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * Bucket id
     */
    private String bucketId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件对象id
     */
    private String fileId;

    /**
     * hash值
     */
    private String hash;

    /**
     * 读写权限
     */
    private String acl;

    /**
     * 文件类型
     */
    private String category;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 格式化文件大小
     */
    private String formattedSize;

    /**
     * 是否为文件夹
     */
    private Boolean isDir;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
