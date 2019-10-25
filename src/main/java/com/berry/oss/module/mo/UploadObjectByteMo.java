package com.berry.oss.module.mo;

import com.berry.oss.common.constant.CommonConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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

    @Pattern(regexp = "^((?!/).)*$")
    @NotBlank
    String fileName;

    String filePath = "/";

    String acl = CommonConstant.AclType.PRIVATE.name();

    @NotNull
    byte[] data;
}
