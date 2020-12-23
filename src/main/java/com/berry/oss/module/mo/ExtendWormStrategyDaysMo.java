package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/23 12:57
 * fileName：ExtendWormStrategyDaysMo
 * Use：
 */
@Data
public class ExtendWormStrategyDaysMo {

    @NotBlank
    private String id;

    @NotNull
    private Integer days;
}
