package com.berry.oss.module.mo;

import com.berry.oss.common.constant.CommonConstant;
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
public class UploadObjectBase64Mo {
    @NotBlank
    String bucket;

    /**
     * 不带后缀名（系统自动判断）
     */
    @NotBlank
    String fileName;

    String filePath = "/";

    String acl = CommonConstant.AclType.PRIVATE.name();

    @NotNull
    String data;
}
