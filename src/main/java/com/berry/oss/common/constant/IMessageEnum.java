package com.berry.oss.common.constant;

/**
 * @author Berry_Cooper.
 * @date 2018-03-26
 * Description 消息常量定义
 */
public interface IMessageEnum {
    /**
     * 得到常量key或值
     *
     * @return
     */
    String getCode();

    /**
     * 得到常量的定义或描述
     * 如果国际化,在这里实现
     *
     * @return
     */
    String getMeg();
}
