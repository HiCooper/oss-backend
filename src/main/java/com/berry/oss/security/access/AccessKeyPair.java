package com.berry.oss.security.access;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-29 16:58
 * fileName：AccessKeyPair
 * Use：
 */
@Data
@Accessors(chain = true)
public class AccessKeyPair {

    private String accessKeyId;
    private String accessKeySecret;
}
