---
description: >-
  Reed Solomon，利用矩阵的数学特性，对数据进行冗余和恢复；基本思想是将原数据分片 N，再通过分片计算出校验分片 M ，RS 允许（N + M <
  256）中任意 M 个数据分片 丢失，都能恢复原始数据。下面以 N=4，M=2 为例
---

# RS冗余纠错

## 分片大小计算

每个分片都增加 1B 存放当前分片长度，并且保证每个分片大小一致。分片长度计算如下：

```java
// 数据分片数
int DATA_SHARDS = 4;
// 校验分片数
int PARITY_SHARDS = 2;
// 分片总数
int TOTAL_SHARDS = DATA_SHARDS + PARITY_SHARDS;
// 信息头大小
int BYTES_IN_INT = DATA_SHARDS;

// 原始数据大小(最大支持2G)
int fileSize = x
// 4 个 数据分片最终大小为
int storedSize = fileSize + BYTES_IN_INT;
// 每个分片大小
int shardSize = (storedSize + DATA_SHARDS - 1) / DATA_SHARDS;
```

## 生成 2 个校验分片

1. 原始数据分片与校验分片组织在一个 6 行 shardSize 列的二维字节数组中，记为_**shards**_ ，其中前 4 行填充原始数据，后两行填充校验分片数据

![&#x6570;&#x636E;&#x5206;&#x7247;&#x4E0E;&#x6821;&#x9A8C;&#x5206;&#x7247;&#x7EC4;&#x7EC7;&#x65B9;&#x5F0F;](../.gitbook/assets/image%20%282%29.png)

1. 构建一个任意子集方阵可逆的编码矩阵C，它的顶部 4\*4 为一个 单位矩阵

![&#x6784;&#x5EFA;&#x7F16;&#x7801;&#x77E9;&#x9635;C](../.gitbook/assets/image%20%286%29.png)

取编码矩阵最后2 行，得到 一个 二维字节数组 记为 _**matrixRows**_

生成校验快 byte\[2\]\[shardSize\] _**outputs**_

```java
public void codeSomeShards(
            byte[][] matrixRows,
            // shards 6*4 分片数组
            byte[][] inputs, 
            // 数据块长度
            int inputCount,
            // 校验块数组
            byte[][] outputs, 
            // 校验块 数量
            int outputCount,
            // 偏移量
            int offset, 
            // shardSize 分片长度
            int byteCount) {

        final byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < outputCount; iOutput++) {
            final byte[] outputShard = outputs[iOutput];
            final byte[] matrixRow = matrixRows[iOutput];
            {
                final int iInput = 0;
                final byte[] inputShard = inputs[iInput];
                final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] = multTableRow[inputShard[iByte] & 0xFF];
                }
            }
            for (int iInput = 1; iInput < inputCount; iInput++) {
                final byte[] inputShard = inputs[iInput];
                final byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                for (int iByte = offset; iByte < offset + byteCount; iByte++) {
                    outputShard[iByte] ^= multTableRow[inputShard[iByte] & 0xFF];
                }
            }
        }
    }
```

执行 **codeSomeShards** 方法处理后，校验块将填充到 inputs

至此 4 + 2 = 6 个数据分片已经完成。

## 根据不少于4个分片恢复原始数据

