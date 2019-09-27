package com.berry.oss.module.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/9/27 9:49
 * fileName：RefererDetailVo
 * Use：
 */
@Data
public class RefererDetailVo {

    private Boolean allowEmpty;
    /**
     * 多个host，支持模式匹配
     */
    private String whiteList;
}
