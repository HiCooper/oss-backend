package com.berry.oss.service.impl;

import com.berry.oss.erasure.ReedSolomon;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
     * 每个数据分片增加 1B 信息头，一共 1 * 4 B
     */
    private static final int BYTES_IN_INT = DATA_SHARDS;

    public void writeData(InputStream inputStream) throws IOException {

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

        // 创建二维字节数组，将 文件字节数组 （allBytes）copy到该数组（shards）
        byte[][] shards = new byte[TOTAL_SHARDS][shardSize];

        // Fill in the data shards
        for (int i = 0; i < DATA_SHARDS; i++) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }

        // 使用 Reed-Solomon 算法计算 2 个奇偶校验分片.
        ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        reedSolomon.encodeParity(shards, 0, shardSize);

        // Write out the resulting files.
        for (int i = 0; i < TOTAL_SHARDS; i++) {
//            this.shardSaveService.writeShard(shards[i]);
        }
    }
}
