package com.berry.oss.module.mo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-15 20:03
 * fileName：GenerateUrlWithSignedMo
 * Use：
 */
@Data
public class GenerateDownloadUrlMo {
    /**
     * bucket name
     */
    @NotBlank
    private String bucket;
    /**
     * 对象全路径
     */
    private List<String> objectPath;
}
