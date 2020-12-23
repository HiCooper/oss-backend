package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/12/23 12:51
 * fileName：CompleteWormStrategyMo
 * Use：
 */
@Data
public class CompleteWormStrategyMo {

    @NotBlank
    private String id;
}
