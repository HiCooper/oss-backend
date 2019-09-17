package com.berry.oss.module.mo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
     * 全路径，不义 / 开头，不能出现连续 // 支持中文数字字母短横线下划线
     */
    @NotBlank
    @Length(min = 2, max = 254, message = "路径长度在2～254之间")
    @Pattern(regexp = "^[^/]((?!//)[a-zA-Z0-9/-_\\u4E00-\\u9FA5]+)*[^/]$", message = "路径不符合要求")
    private String folder;
}
