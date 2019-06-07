package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 20:35
 * fileName：FastUploadCheck
 * Use：
 */
@Data
public class FastUploadCheck {

    @NotBlank
    private String fileName;

    @NotBlank
    private String bucketId;

    @NotBlank
    private String filePath;

    @NotBlank
    private String hash;

    @NotNull
    private Long contentLength;
}
