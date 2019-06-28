package com.berry.oss.common.constant;

import java.util.regex.Pattern;

/**
 * Application constants.
 */
public final class Constants {

    /**
     * 单个账户最多可创建的密钥对数量 3 个
     */
    public static final int SINGLE_ACCOUNT_ACCESS_KEY_PAIR_MAX = 3;

    /**
     * 文件路径正则
     */
    public static final Pattern FILE_PATH_PATTERN = Pattern.compile("(\\w+/?)+$");

    /**
     * 默认文件路径（根路径 / ）
     */
    public static final String DEFAULT_FILE_PATH = "/";


    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";

    private Constants() {
    }
}
