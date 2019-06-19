package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

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
    private String objectName;
}
