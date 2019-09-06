package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Title DeleteObjectsMo
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/19 18:17
 */
@Data
public class DeleteObjectsMo {
    @NotBlank
    String bucket;
    /**
     * 对象id,多个用 英文逗号隔开
     */
    @NotBlank
    String objectIds;
}
