package com.berry.oss.remote;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-12 22:07
 * fileName：WriteShardMo
 * Use：
 */
@Data
public class WriteShardMo {
    String username;
    String bucketName;
    String fileName;
    Integer shardIndex;
    byte[] data;
}
