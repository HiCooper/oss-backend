package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 22:29
 * fileName：UpdateObjectAclMo
 * Use：
 */
@Data
public class UpdateObjectAclMo {

    @NotBlank
    private String objectName;

    @NotBlank
    private String acl;
}
