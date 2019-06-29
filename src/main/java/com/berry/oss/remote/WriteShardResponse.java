package com.berry.oss.remote;

import lombok.Data;

/**
 * Title WriteShardResponse
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/13 12:54
 */
@Data
public class WriteShardResponse {

    private String url;
    private String path;

    public WriteShardResponse(String url, String path) {
        this.url = url;
        this.path = path;
    }
}
