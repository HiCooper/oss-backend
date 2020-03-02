package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @version 1.0
 * @date 2020/3/2 21:27
 * fileName：MakeUpForLostDataMo
 * Use：
 */
@Data
public class MakeUpForLostDataMo {

    @NotBlank
    private String fileName;

    @NotBlank
    private String filePath;
}
