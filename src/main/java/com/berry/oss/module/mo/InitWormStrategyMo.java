package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/21 11:05
 * fileName：InitWormStrategyMo
 * Use：新建合规保留策略 请求参数
 */
@Data
public class InitWormStrategyMo {

    /**
     * bucket name
     */
    @NotBlank
    private String bucket;

    /**
     * 保留期限
     */
    @NotNull
    private Integer days;
}
