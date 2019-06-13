package com.berry.oss.common.exceptions;

import com.berry.oss.common.constant.IMessageEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Berry_Cooper.
 * @date 2018-03-26
 * Description 异常基类
 */
public class BaseException extends RuntimeException {

    private final Logger logger = LoggerFactory.getLogger(BaseException.class);

    private String code;
    private String msg;

    public BaseException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BaseException(String code, String msg, Throwable ex) {
        super(msg, ex);
        this.code = code;
    }

    public BaseException(IMessageEnum msg) {
        super(msg.getMeg());
        this.msg = msg.getMeg();
        this.code = msg.getCode();
    }

    public BaseException(IMessageEnum msg, Throwable ex) {
        super(msg.getMeg(), ex);
        this.msg = msg.getMeg();
        this.code = msg.getCode();
    }

    public BaseException(Throwable exception) {
        super(exception);
    }

    public String getCode() {
        return code;
    }
}
