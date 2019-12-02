package com.berry.oss.service.impl;

import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.config.GlobalProperties;
import com.berry.oss.service.IShardSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Title ShardSaveServiceImpl
 * Description
 * Copyright (c) 2019
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/6/10 18:29
 */
@Service
public class ShardSaveServiceImpl implements IShardSaveService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GlobalProperties globalProperties;

    public ShardSaveServiceImpl(GlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
    }

    @Override
    public String writeShard(String filePath, String bucketName, String fileName, byte[] data) throws IOException {
        if (data == null || data.length <= 0) {
            throw new BaseException("403", "write data is blank");
        }
        long start = System.currentTimeMillis();
        File file = new File(globalProperties.getDataPath(), bucketName + filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // 单机模式下 没有 shardIndex 参数
        String fileFullPath = file.getPath() + "/" + fileName;
        File file1 = new File(fileFullPath);
        if (file1.exists()) {
            Files.delete(Paths.get(fileFullPath));
            logger.debug("旧文件存在，已删除");
        }

        try (FileOutputStream outputStream = new FileOutputStream(file1)) {
            outputStream.write(data);
            logger.debug("write data to:{}", fileFullPath);
        }
        logger.debug("写文件:[{}] 耗时:[{}] ms", fileFullPath, (System.currentTimeMillis() - start));
        return fileFullPath;
    }

    @Override
    public byte[] readShard(String path) throws IOException {
        long start = System.currentTimeMillis();
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            try (FileInputStream in = new FileInputStream(file)) {
                int size = in.available();
                byte[] result = new byte[size];
                int read = in.read(result);
                if (read != size) {
                    throw new BaseException("403", "数据读取不完整，path:" + path);
                }
                logger.debug("读取文件：[{}], 耗时:[{}] ms", path, (System.currentTimeMillis() - start));
                return result;
            }
        }
        throw new BaseException("403", "数据丢失，path:" + path);
    }
}
