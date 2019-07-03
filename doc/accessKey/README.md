# 用户密钥对的生成

## accessKey
随机字符串 长度 22

## accessKeySecret

随机字符串 长度： 31


## 作用
accessKey 用来标志用户
accessKeySecret 用来作为 msg 签名 密钥

## 验证时
同样的方法计算签名，一致则通过