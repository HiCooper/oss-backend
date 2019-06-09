package com.berry.oss.rpc;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-09 16:47
 * fileName：IShardService
 * Use：
 */
public interface IShardService {

    String putShard(InputStream inputStream);

    InputStream getShard(String shardId);
}
