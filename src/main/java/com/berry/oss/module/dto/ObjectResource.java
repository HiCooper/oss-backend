package com.berry.oss.module.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Title ObjectResource
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/13 16:00
 */
@Data
@Accessors(chain = true)
public class ObjectResource {

    private String fileName;

    private Long fileSize;

    private String fileId;

    private InputStream inputStream;

    private String hash;

    private LocalDateTime createTime;
}
