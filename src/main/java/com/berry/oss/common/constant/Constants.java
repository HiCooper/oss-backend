package com.berry.oss.common.constant;

import java.nio.charset.Charset;
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

    /**
     * 所有都是UTF-8编码
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * 心跳检查url
     */
    public static final String HEALTH_CHECK_URL = "/actuator/health";

    /**
     * 错误状态响应
     */
    public static final String ERROR_STATE_URL = "/error";

    /**
     * 上传接口 授权密钥口令 请求头名字，仅可访问 对象 create 方法
     */
    public static final String ACCESS_TOKEN_KEY = "access_token";

    /**
     * access_token 负载信息长度 3
     */
    public static final int ENCODE_DATA_LENGTH = 3;

    /**
     * sdk 授权密钥口令 通用请求头，授权通过的请求，拥有账户的全部权限
     */
    public static final String OSS_SDK_AUTH_HEAD_NAME = "oss_sdk_authorization";

    /**
     * sdk 口令前缀
     */
    public static final String OSS_SDK_AUTH_PREFIX = "OSS-";

    /**
     * oss_sdk_authorization 负载信息长度 2
     */
    public static final int ENCODE_SDK_DATA_LENGTH = 2;

    public static final String JSON_MIME = "application/json";

    public static final String FORM_MIME = "application/x-www-form-urlencoded";

    public static final String DEFAULT_MIME = "application/octet-stream";


    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";

    private Constants() {
    }
}
