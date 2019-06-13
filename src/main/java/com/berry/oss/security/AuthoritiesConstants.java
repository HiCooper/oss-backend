package com.berry.oss.security;

/**
 * Constants for Spring Security authorities.
 *
 * @author HiCooper&seassoon
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    /**
     * 管理员角色
     */
    public static final String ADMIN = "ROLE_ADMIN";

    /**
     * 普通角色
     */
    public static final String USER = "ROLE_USER";

    /**
     * 匿名角色
     */
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

}
