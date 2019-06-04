package com.berry.oss.module;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Title CreateBucketMo
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/4 15:07
 */
@Data
public class CreateBucketMo {

    /**
     * bucket name
     */
    @NotBlank
    String name;

    /**
     * bucket 读写权限
     */
    @NotBlank
    String acl;
}
