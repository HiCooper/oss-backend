package com.berry.oss.common.exceptions;


import com.berry.oss.common.constant.IMessageEnum;

/**
 * @author Berry_Cooper.
 * @date 2018-03-26
 * Description 上传异常
 */
public class UploadException extends RuntimeException {

    private String code;
    private String msg;

    public UploadException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public UploadException(String code, String msg, Throwable ex) {
        super(msg, ex);
        this.code = code;
    }

    public UploadException(IMessageEnum msg) {
        super(msg.getMeg());
        this.msg = msg.getMeg();
        this.code = msg.getCode();
    }

    public UploadException(IMessageEnum msg, Throwable ex) {
        super(msg.getMeg(), ex);
        this.msg = msg.getMeg();
        this.code = msg.getCode();
    }

    public UploadException(Throwable exception) {
        super(exception);
    }

    public String getCode() {
        return code;
    }

}
