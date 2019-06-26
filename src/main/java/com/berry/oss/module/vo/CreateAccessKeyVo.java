package com.berry.oss.module.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Title CreateAccessKeyVo
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/26 16:12
 */
@Data
@Accessors(chain = true)
public class CreateAccessKeyVo {

    private String accessKeyId;

    private String accessKeySecret;
}
