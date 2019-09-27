package com.berry.oss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.HttpClient;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.service.IRegionInfoDaoService;
import com.berry.oss.erasure.ReedSolomon;
import com.berry.oss.module.dto.ServerListDTO;
import com.berry.oss.module.dto.WriteShardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command-line program encodes one file using Reed-Solomon 4+2.
 * <p>
 * The one argument should be a file name, say "foo.txt".  This program
 * will create six files in the same directory, breaking the input file
 * into four data shards, and two parity shards.  The output files are
 * called "foo.txt.0", "foo.txt.1", ..., and "foo.txt.5".  Numbers 4
 * and 5 are the parity shards.
 * <p>
 * The data stored is the file size (four byte int), followed by the
 * contents of the file, and then padded to a multiple of four bytes
 * with zeros.  The padding is because all four data shards must be
 * the same size.
 */
@Service
public class ReedSolomonEncoderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 数据分片数
     */
    private static final int DATA_SHARDS = 4;
    /**
     * 奇偶校验分片数
     */
    private static final int PARITY_SHARDS = 2;

    /**
     * 分片总数
     */
    private static final int TOTAL_SHARDS = DATA_SHARDS + PARITY_SHARDS;

    /**
     * 数据分片增加 4B 信息头，存放数据长度信息
     */
    private static final int BYTES_IN_INT = DATA_SHARDS;

    private final IRegionInfoDaoService regionInfoDaoService;

    public ReedSolomonEncoderService(IRegionInfoDaoService regionInfoDaoService) {
        this.regionInfoDaoService = regionInfoDaoService;
    }

    String writeData(String filePath, byte[] data, String fileName, BucketInfo bucketInfo) throws IOException {
        final int fileSize = data.length;

        // 计算每个数据分片大小.  (文件大小 + 4个数据分片头) 除以 4 向上取整
        final int storedSize = fileSize + BYTES_IN_INT;
        final int shardSize = (storedSize + DATA_SHARDS - 1) / DATA_SHARDS;

        List<WriteShardResponse> result = getWriteShardResponses(filePath, fileName, bucketInfo, shardSize, data);
        return JSON.toJSONString(result);
    }

    /**
     * 将输入流，分片 4+2 保存
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param bucketInfo  存储空间
     * @param username    用户名
     * @return 对象唯一标识id
     * @throws IOException
     */
    String writeData(String filePath, InputStream inputStream, String fileName, BucketInfo bucketInfo) throws IOException {

        // Get the size of the input file.  (Files bigger that
        // Integer.MAX_VALUE will fail here!) 最大 2G
        final int fileSize = inputStream.available();

        // 计算每个数据分片大小.  (文件大小 + 4个数据分片头) 除以 4 向上取整
        final int storedSize = fileSize + BYTES_IN_INT;
        final int shardSize = (storedSize + DATA_SHARDS - 1) / DATA_SHARDS;

        // 创建一个 4 个数据分片大小的 buffer
        final int bufferSize = shardSize * DATA_SHARDS;
        final byte[] allBytes = new byte[bufferSize];

        // buffer前4个字节（4B）写入数据长度
        ByteBuffer.wrap(allBytes).putInt(fileSize);

        // 读入文件到 字节数组（allBytes）
        int bytesRead = inputStream.read(allBytes, BYTES_IN_INT, fileSize);
        if (bytesRead != fileSize) {
            throw new IOException("not enough bytes read");
        }
        inputStream.close();

        List<WriteShardResponse> result = getWriteShardResponses(filePath, fileName, bucketInfo, shardSize, allBytes);

        return JSON.toJSONString(result);
    }

    private List<WriteShardResponse> getWriteShardResponses(String filePath, String fileName, BucketInfo bucketInfo, int shardSize, byte[] allBytes) {
        // 创建二维字节数组，将 文件字节数组 （allBytes）copy到该数组（shards）
        byte[][] shards = new byte[TOTAL_SHARDS][shardSize];

        // Fill in the data shards
        for (int i = 0; i < DATA_SHARDS; i++) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }

        // 使用 Reed-Solomon 算法计算 2 个奇偶校验分片.
        ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        reedSolomon.encodeParity(shards, 0, shardSize);

        // 获取该存储空间的 6 个 可用服务器列表
        List<ServerListDTO> serverList = regionInfoDaoService.getServerListByRegionIdLimit(bucketInfo.getRegionId(), TOTAL_SHARDS);
        if (serverList.size() != TOTAL_SHARDS) {
            // 数据写入服务不可用
            logger.error("分布式模式启动，数据写入服务校验失败,至少需要可用服务数：{}\n可用服务有：{}, \n 明细：{}", TOTAL_SHARDS, serverList.size(), JSON.toJSONString(serverList));
            throw new RuntimeException("数据写入服务不可用");
        }

        List<WriteShardResponse> result = new ArrayList<>(16);

        Map<String, Object> params = new HashMap<>(16);
        params.put("bucketName", bucketInfo.getName());
        params.put("filePath", filePath);
        params.put("fileName", fileName);
        // 数据分片分发
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            ServerListDTO server = serverList.get(i);
            params.put("shardIndex", i);
            params.put("data", shards[i]);
            String basePath = "http://" + server.getIp() + ":" + server.getPort();
            String writePath = writeOneShard(basePath + "/data/write", i, params);
            result.add(new WriteShardResponse(basePath + "/data/read", writePath));
        }
        return result;
    }

    /**
     * 异步修复数据分片
     *
     * @param jsonArray    分片信息
     * @param shards       完整的数据分片
     * @param shardPresent 数据丢失信息
     */
    @Async("taskExecutor")
    public void fixDamageData(String bucket, JSONArray jsonArray, byte[][] shards, boolean[] shardPresent, String objectId) {
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            if (!shardPresent[i]) {
                JSONObject shard = jsonArray.getJSONObject(i);
                String readUrl = shard.getString("url");
                String writeUrl = readUrl.replace("read", "write");
                String path = shard.getString("path");
                System.out.println(path);
                // path 由  basePath + bucketName + filePath + fileName 组成
                String[] split = path.split(bucket);
                if (split.length != 2) {
                    logger.error("bucket :{}, index:{} ,path:{}，路径不正确", bucket, i, path);
                    throw new BaseException("403", "自动修复失败");
                }
                String fullFilePath = split[1];
                String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                String filePath = fullFilePath.substring(0, fullFilePath.lastIndexOf("/"));
                System.out.println(filePath);
                System.out.println(fileName);

                Map<String, Object> params = new HashMap<>(16);
                params.put("bucketName", bucket);
                params.put("filePath", filePath);
                params.put("fileName", fileName);
                params.put("shardIndex", i);
                params.put("data", shards[i]);
                writeOneShard(writeUrl, i, params);
                logger.debug("修复对象：{} 数据块：{} 完成, url:{}, path:{}", objectId, i, readUrl, path);
            }
        }
    }

    /**
     * 写单个分片数据
     *
     * @param url    请求api地址
     * @param index  当前分片索引
     * @param params 参数
     * @return 写入路径
     */
    public String writeOneShard(String url, int index, Map<String, Object> params) {
        String writePath = HttpClient.doPost(url, params);
        if (StringUtils.isBlank(writePath)) {
            logger.error("数据写入失败，index:{},服务：{}", index, url);
            throw new RuntimeException("数据写入失败");
        }
        return writePath;
    }
}
