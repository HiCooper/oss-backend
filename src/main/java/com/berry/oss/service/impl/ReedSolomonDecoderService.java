package com.berry.oss.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.berry.oss.common.utils.HttpClient;
import com.berry.oss.erasure.ReedSolomon;
import com.berry.oss.lock.model.Lock;
import com.berry.oss.lock.service.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Reed-Solomon 4+2
 * <p>
 * foo.txt 分片为 foo.txt.0, foo.txt.1, foo.txt.2, foo.txt.2, foo.txt.4, foo.txt.5
 * 从数据服务读取数据，4 个数据块，2 个校验块
 * 注意: 如果前4个数据块，顺序且正确读出，将跳过剩下校验块的读取和RS纠错算法
 *
 * @author berry_cooper
 */
@Service
public class ReedSolomonDecoderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int DATA_SHARDS = 4;
    private static final int PARITY_SHARDS = 2;
    private static final int TOTAL_SHARDS = 6;
    private static final int BYTES_IN_INT = 4;

    @Resource
    private ReedSolomonEncoderService encoderService;

    private final LockService lockService;

    public ReedSolomonDecoderService(LockService lockService) {
        this.lockService = lockService;
    }


    InputStream readData(String bucket, String shardJson, String objectId) {

        JSONArray jsonArray = JSONArray.parseArray(shardJson);

        final byte[][] shards = new byte[TOTAL_SHARDS][];
        final boolean[] shardPresent = new boolean[TOTAL_SHARDS];
        int shardSize = 0;
        int shardCount = 0;
        // 是否跳过 RS
        boolean skipRs = false;
        final boolean[] check = new boolean[DATA_SHARDS];
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            if (i == DATA_SHARDS && checkAllTrue(check)) {
                // 如果前 DATA_SHARDS 个数据块顺序读出，可以不再读取后两个校验块，并跳过 RS 纠错算法
                skipRs = true;
                break;
            }
            JSONObject shard = jsonArray.getJSONObject(i);
            String url = shard.getString("url");
            String path = shard.getString("path");
            try {
                Map<String, Object> params = new HashMap<>(16);
                params.put("path", URLEncoder.encode(path, StandardCharsets.UTF_8.name()));
                byte[] bytes = HttpClient.doGet(url, params);
                if (bytes == null || bytes.length == 0) {
                    logger.error("读取：{} url：{}, path: {},数据为空", i, url, path);
                    continue;
                }
                if (i < DATA_SHARDS && path.substring(path.lastIndexOf(".") + 1).equals(String.valueOf(i))) {
                    check[i] = true;
                }
                if (shardSize != 0 && bytes.length != shardSize) {
                    logger.error("数据损坏，分片{}大小{}与上一分片大小{}不一致", i, bytes.length, shardSize);
                    return null;
                }
                shardSize = bytes.length;
                shards[i] = bytes;
                shardPresent[i] = true;
                shardCount += 1;
            } catch (Exception e) {
                logger.error(e.getMessage());
                return null;
            }
        }

        if (!skipRs) {
            // 不能跳过，出现了数据非顺序读出或者部分数据丢失
            logger.debug("启用RS纠错数据恢复...");
            // 至少需要 DATA_SHARDS 个用来重构数据
            if (shardCount < DATA_SHARDS) {
                System.out.println("Not enough shards present");
                return null;
            }

            // 1.用空的 buffers 填充 丢失的分片
            for (int i = 0; i < TOTAL_SHARDS; i++) {
                if (!shardPresent[i]) {
                    shards[i] = new byte[shardSize];
                }
            }

            // 2.使用 Reed-Solomon 算法恢复丢失的分片
            ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
            reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);
            logger.debug("RS纠错数据恢复完成!");

            try {
                // 以对象 id 为key 创建锁
                Lock lock = lockService.create(objectId);
                // 3.异步自动修复丢失数据
                encoderService.fixDamageData(bucket, jsonArray, shards, shardPresent, objectId);
                lockService.release(lock.getName(), lock.getValue());
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        // 二维数组转一维
        byte[] allBytes = new byte[shardSize * DATA_SHARDS];
        for (int i = 0; i < DATA_SHARDS; i++) {
            System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
        }
        int fileSize = ByteBuffer.wrap(allBytes).getInt();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(allBytes, BYTES_IN_INT, fileSize);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 检查全为 true
     *
     * @param check 待检查数组
     * @return boolean
     */
    private boolean checkAllTrue(boolean[] check) {
        System.out.println(Arrays.toString(check));
        for (boolean b : check) {
            if (!b) {
                return false;
            }
        }
        return true;
    }
}
