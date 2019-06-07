package com.berry.oss.common;

import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.exceptions.UploadException;

/**
 * @author HiCooper&seassoon
 */
public class ResultFactory {

    /**
     * 仅结果状态
     *
     * @return
     */
    public static Result wrapper() {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 数据
     *
     * @param data
     * @param <M>
     * @return
     */
    public static <M> Result<M> wrapper(M data) {
        return new Result<>(data, ResultCode.SUCCESS);
    }

    /**
     * 数据+自定义msg
     *
     * @param data
     * @param msg
     * @param <M>
     * @return
     */
    public static <M> Result<M> wrapper(M data, ResultCode msg) {
        return new Result<>(data, msg);
    }

    /**
     * 自定义msg
     *
     * @param msg
     * @return
     */
    public static Result wrapper(ResultCode msg) {
        return new Result(msg);
    }

    /**
     * 自定义msg + 自定义参数信息info, info用来组合返回msg
     *
     * @param msg
     * @param info
     * @return
     */
    public static Result wrapper(ResultCode msg, String info) {
        return new Result(msg, info);
    }

    /**
     * 异常
     *
     * @param ex
     * @return
     */
    public static Result wrapper(BaseException ex) {
        return new Result(ex);
    }

    public static Result wrapper(UploadException ex) {
        return new Result(ex);
    }

}
