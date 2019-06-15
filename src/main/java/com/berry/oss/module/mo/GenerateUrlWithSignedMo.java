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

    private String bucket;
    private String objectName;
    private Integer timeout;
}
