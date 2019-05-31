package com.berry.oss.common.exceptions;

import com.berry.oss.common.Result;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.ResultFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Berry_Cooper.
 * @date 2018-03-26
 * Description 全局统一异常处理
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // http状态码均为200，返回错误信息实体

    /**
     * 自定义异常处理
     *
     * @param req request
     * @param ex  exception
     * @return 响应
     */
    @ExceptionHandler(value = BaseException.class)
    public Result baseExceptionHandler(HttpServletRequest req, BaseException ex) {
        logger.error("请求接口 [{}] 发生错误，错误信息：{}", req.getRequestURI(), ex.getLocalizedMessage());
        return ResultFactory.wrapper(ex);
    }

    /**
     * 未知异常处理
     *
     * @param ex exception
     * @return 响应
     */
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(Exception ex) {
        logger.error("系统异常:{}", ex.toString());
        return ResultFactory.wrapper(ResultCode.FAIL);
    }

    /**
     * 上传异常，比如返回非200状态码
     *
     * @param req request
     * @param ex  exception
     */
    @ExceptionHandler(value = UploadException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result uploadExceptionHandler(HttpServletRequest req, UploadException ex) {
        logger.error("上传接口 [{}] 发生错误，错误信息：{}", req.getRequestURI(), ex.toString());
        return ResultFactory.wrapper(ex);
    }

    // http状态码均为500，不返回实体

    /**
     * 500-服务器内部错误
     *
     * @param req request
     * @param ex  exception
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void runtimeExceptionHandler(HttpServletRequest req, RuntimeException ex) {
        logger.error("接口 [{}] 内部错误:{},位置：{}", req.getRequestURI(), ex.toString(), ex.getStackTrace()[0]);
    }
}
