package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Title CreateAccessKeyMo
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/26 16:03
 */
@Data
public class CreateAccessKeyMo {
    @NotBlank
    private String password;
}
