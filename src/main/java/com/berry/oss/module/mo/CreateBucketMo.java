package com.berry.oss.module.mo;

import com.berry.oss.common.constant.CommonConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:43
 * fileName：CreateBucketMo
 * Use：
 */
@Data
public class CreateBucketMo {

    @NotBlank
    private String name;

    private String acl = CommonConstant.AclType.PRIVATE.name();
}
