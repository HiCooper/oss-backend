package com.berry.oss.module.mo;

import com.berry.oss.common.constant.CommonConstant;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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

    /**
     * 规则：
     * 1、只允许小写字母、数字、中划线（-），且不能以短横线开头或结尾
     * 2、3-63 个字符
     * 3. 不允许使用保留关键字，
     */
    @NotBlank
    @Pattern(regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$")
    @Length(min = 3, max = 63)
    private String name;

    private String acl = CommonConstant.AclType.PRIVATE.name();
}
