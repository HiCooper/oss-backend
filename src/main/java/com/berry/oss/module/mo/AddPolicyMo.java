package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Title AddPolicyMo
 * Description
 * Copyright (c) 2019
 * Company  上海思贤信息技术股份有限公司
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/7/15 14:13
 */
@Data
public class AddPolicyMo {

    /**
     * bucket name
     */
    private String bucket;

    /**
     * 授权类型
     */
    @NotNull
    private Integer actionType;

    /**
     * 授权对象 列表
     */
    @NotEmpty
    private List<String> principal;

    /**
     * 授权资源 列表
     */
    @NotEmpty
    private List<String> resource;
}
