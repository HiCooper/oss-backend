---
description: >-
  密钥对用于通过 SDK 访问存储空间授权，每个用户最多可以创建 3 个密钥对，可以对密钥对进行禁用，启用，删除等操作；密钥对由公开的 accessKey 和
  保密的 accessKeySecret 组成
---

# 密钥对

### 1. 生成

#### accessKey

22 位长度的随机字符串，用来标志用户

#### accessKeySecret

31 位长度的随机字符串， 用做签名的密钥

> 随机字符串组成: \`ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789./\`

### 3. 验证方式

两种方式：

* 方式1：程序 通过 SDK 在后台与 OSS 交互，
* 方式2：程序通过 SDK 生成 上传 token ，其他程序通过 token 直接与 OSS 交互

 SDK 通过密钥对给请求添加签名或签发token，签名中一部分包含 明文的 accessKey，当后台系统校验请求时，根据 明文 accessKey 获取 对应的密钥，给请求再次签名，与请求头中签名进行对比，一致则视为合法请求。

