package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-19 22:37
 * fileName：CreateFolderMo
 * Use：
 */
@Data
public class CreateFolderMo {

    @NotBlank
    private String bucket;

    /**
     * 全路径
     */
    @NotBlank
    @Pattern(regexp = "^[^\\/]((?!\\/\\/)[a-zA-Z0-9\\/\\u4E00-\\u9FA5]+)*[^\\/]$", message = "路径不符合要求")
    private String objectName;
}
