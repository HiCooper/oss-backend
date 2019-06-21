package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-05 22:11
 * fileName：UpdateBucketAclMo
 * Use：
 */
@Data
public class UpdateBucketAclMo {

    @NotBlank
    private String bucketName;

    @NotBlank
    private String acl;
}
