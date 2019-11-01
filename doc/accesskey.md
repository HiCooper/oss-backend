# 用户密钥对的生成

## accessKey

随机字符串 长度 22

## accessKeySecret

随机字符串 长度： 31

## 作用

accessKey 用来标志用户 accessKeySecret 用来作为 msg 签名 密钥

## 验证时

同样的方法计算签名，一致则通过

## 授权使用方式

1. sdk 根据密钥对 生成 上传 token ，由前端发起 oss api 调用，专用于上传 对象文件，token有过期时间
2. sdk 根据密钥对 签名http请求，由后端发起 oss api 调用，每个请求独立签名，不限时间

