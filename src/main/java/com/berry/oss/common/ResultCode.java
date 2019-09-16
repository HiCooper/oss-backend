package com.berry.oss.common;


import com.berry.oss.common.constant.IMessageEnum;

/**
 * @author Berry_Cooper
 */
public enum ResultCode implements IMessageEnum {

    /**
     * 成功
     */
    SUCCESS("200", "SUCCESS"),
    FAIL("400", "FAIL"),
    INTERNAL_SERVER_ERROR("500", "系统异常,故障信息为：{0}"),
    URL_NOT_FOUND("0001", "URL地址不存在"),
    BAD_PASSWORD("403", "密码错误"),
    ERROR_PARAMETER("0002", "请求参数错误"),
    ERROR_SYNTAX("0003", "请求参数语法错误"),
    DATA_NOT_EXIST("D404", "数据不存在"),
    FILTS_DAMAGED_OR_MISSING("F404", "文件损坏或丢失"),
    ACCOUNT_NOT_EXIST("D404", "账户不存在"),
    BUCKET_NOT_EXIST("B404", "Bucket 不存在"),
    USERNAME_OR_PASSWORD_ERROR("D404", "用户名或密码错误"),
    USERNAME_EXIST("D403", "用户名已存在"),
    ACCOUNT_DISABLE("D40401", "账户不可用"),
    ACCOUNT_LOCKED("D40402", "账户已被锁定"),
    ACCESS_KEY_LIMIT_THREE("403", "账户最多能创建3个密钥对"),
    TOKEN_VERIFY_FAIL("TOKEN_VERIFY_FAIL", "身份凭证校验失败"),
    NOT_LOGIN("D404", "用户未登录"),
    MISSING_PARAMETER("D400", "缺少请求参数：{0}"),

    UNAUTHORIZED("401", "Unauthorized"),

    ILLEGAL_ACCESS_TOKEN("403", "ILLEGAL ACCESS TOKEN"),

    PAYMENT_REQUIRED("402", "Payment Required"),

    FORBIDDEN("403", "Forbidden"),

    NOT_FOUND("404", "Not Found"),

    METHOD_NOT_ALLOWED("405", "Method Not Allowed"),

    NOT_ACCEPTABLE("406", "Not Acceptable"),

    PROXY_AUTHENTICATION_REQUIRED("407", "Proxy Authentication Required"),

    REQUEST_TIMEOUT("408", "Request Timeout"),

    ERROR_SERVE("9001", "系统异常,系统无法请求到服务"),
    ERROR_SERVE_TIMEOUT("9002", "系统异常,系统请求服务超时"),
    ERROR_SERVE_CONN_REFUSED("9003", "服务链接拒绝，请稍后再试"),
    ERROR_SERVE_REQUEST("9004", "服务返回信息错误，请联系管理员");


    private String code;

    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMeg() {
        return this.msg;
    }

}
