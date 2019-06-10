package com.berry.oss.service.impl;

import com.berry.oss.erasure.ReedSolomon;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Command-line program that decodes a file using Reed-Solomon 4+2.
 * <p>
 * The file name given should be the name of the file to decode, say
 * "foo.txt".  This program will expected to find "foo.txt.0" through
 * "foo.txt.5", with at most two missing.  It will then write
 * "foo.txt.decoded".
 */
@Service
public class ReedSolomonDecoderService {

    private static final int DATA_SHARDS = 4;
    private static final int PARITY_SHARDS = 2;
    private static final int TOTAL_SHARDS = 6;
    private static final int BYTES_IN_INT = 4;

    public InputStream readData(String shardId) throws IOException {

        // Read in any of the shards that are present.
        // (There should be checking here to make sure the input
        // shards are the same size, but there isn't.)
        final byte[][] shards = new byte[TOTAL_SHARDS][];
        final boolean[] shardPresent = new boolean[TOTAL_SHARDS];
        int shardSize = 0;
        int shardCount = 0;
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            File shardFile = new File("./", "test.png" + "." + i);
            if (shardFile.exists()) {
                shardSize = (int) shardFile.length();
                shards[i] = new byte[shardSize];
                shardPresent[i] = true;
                shardCount += 1;
                InputStream in = new FileInputStream(shardFile);
                in.read(shards[i], 0, shardSize);
                in.close();
                System.out.println("Read " + shardFile);
            }
        }

        // We need at least DATA_SHARDS to be able to reconstruct the file.
        if (shardCount < DATA_SHARDS) {
            System.out.println("Not enough shards present");
            return null;
        }

        // Make empty buffers for the missing shards.
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            if (!shardPresent[i]) {
                shards[i] = new byte[shardSize];
            }
        }

        // Use Reed-Solomon to fill in the missing shards
        ReedSolomon reedSolomon = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);

        // Combine the data shards into one buffer for convenience.
        // (This is not efficient, but it is convenient.)
        byte[] allBytes = new byte[shardSize * DATA_SHARDS];
        for (int i = 0; i < DATA_SHARDS; i++) {
            System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
        }

        // Extract the file length
        int fileSize = ByteBuffer.wrap(allBytes).getInt();

        // Write the decoded file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(allBytes, BYTES_IN_INT, fileSize);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
