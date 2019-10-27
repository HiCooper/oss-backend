package com.berry.oss.module.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019/10/27 21:47
 * fileName：HotObjectStatisVo
 * Use：
 */
@Data
public class HotObjectStatisVo {

    private String fullPath;

    private long count;

    public HotObjectStatisVo(String fullPath, long count) {
        this.fullPath = fullPath;
        this.count = count;
    }
}
