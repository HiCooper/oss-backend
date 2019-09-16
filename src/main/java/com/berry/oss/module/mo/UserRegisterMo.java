package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-09-11 21:52
 * fileName：UserRegisterMo
 * Use：
 */
@Data
public class UserRegisterMo {

    /**
     * 用户名
     */
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$", message = "字母开头，允许5-16字节，允许字母数字下划线")
    private String username;

    /**
     * 密码
     */
    @Pattern(regexp = "^[a-zA-Z]\\w{5,17}$", message = "以字母开头，长度在6~18之间，只能包含字母、数字和下划线")
    private String password;

    /**
     * 昵称
     */
    @Pattern(regexp = "[a-zA-Z0-9_\\u4e00-\\u9fa5]{2,15}", message = "中文，数字字母下划线，长度在2～15之间")
    private String nickName;

    /**
     * 邮箱
     */
    @Email
    private String email;


}
