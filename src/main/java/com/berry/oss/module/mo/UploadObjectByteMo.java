package com.berry.oss.module.mo;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/8/16 9:29
 * fileName：UploadObjectByte
 * Use：
 */
@Data
public class UploadObjectByteMo {
    @NotBlank
    String bucket;

    @NotBlank
    String fileName;

    String filePath = "/";

    String acl;

    @NotNull
    byte[] data;
}
