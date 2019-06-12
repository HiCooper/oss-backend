package com.berry.oss.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

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
     * @throws IOException
     */
    @RequestMapping(value = "/data/write", method = RequestMethod.POST)
    String writeShard(@RequestBody WriteShardMo mo) throws IOException;


    /**
     * 读分片
     *
     * @param path
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/data/read", method = RequestMethod.GET)
    byte[] readShard(@RequestParam("path")String path) throws IOException;
}
