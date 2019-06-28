package com.berry.oss.module.mo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-15 20:03
 * fileName：GenerateUrlWithSignedMo
 * Use：
 */
@Data
public class GenerateUrlWithSignedMo {

    /**
     *  bucket name
     */
    private String bucket;
    /**
     * 对象全路径
     */
    private String objectPath;

    /**
     * 有效时长 单位 秒
     */
    private Integer timeout;
}
