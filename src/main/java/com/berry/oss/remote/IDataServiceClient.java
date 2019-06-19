package com.berry.oss.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-12 21:31
 * fileName：IDataServiceClient
 * Use：
 */
@FeignClient("OSS-DATA-SERVICE")
public interface IDataServiceClient {

    /**
     * 写分片
     *
     * @param mo
     * @return
     */
    @RequestMapping(value = "/data/write", method = RequestMethod.POST)
    WriteShardResponse writeShard(@RequestBody WriteShardMo mo);

    /**
     * 读分片
     *
     * @param path
     * @return
     */
    @RequestMapping(value = "/data/read", method = RequestMethod.GET)
    byte[] readShard(@RequestParam("path") String path);
}
