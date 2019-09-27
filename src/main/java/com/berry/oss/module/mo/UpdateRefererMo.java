package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/9/27 9:49
 * fileName：RefererDetailVo
 * Use：
 */
@Data
public class UpdateRefererMo {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 存储空间
     */
    @NotBlank
    private String bucket;

    private Boolean allowEmpty;
    /**
     * 多个host，支持模式匹配,逗号隔开
     */
    @NotBlank
    private String whiteList;
}
