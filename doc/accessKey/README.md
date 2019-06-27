# 用户密钥对的生成

````
String salt = BCrypt.gensalt();
String accessKey = salt.substring(7);
String hash = BCrypt.hashpw(user.getUsername(), salt);
String accessKeySecret = hash.substring(29); 

````

头部：`$2a$10$`

## accessKey
BCrypt 生成的密码盐 去掉头部

## accessKeySecret

使用密码盐对用户名 hash 得到的值 去掉 头部 和 accessKey部分


## 回归比对

1. 检查 accessKey 是否存在，找到该用户信息
2. 拼接 头部 + accessKey + accessKeySecret 对 用户名进行比对，示例如下：

````
String accessKey = "ls1mJlFnYPPHNf5FSU7lP.";
UserInfo userInfo = dao.getUserInfoByAccessKey(accessKey);
String accessKeySecret = "wEN3eKOr4lBYhirQ7NDLqCbeTHfA3kG";
Strign hash = "$2a$10$" + accessKey + accessKeySecret;
boolean result = BCrypt.checkpw(userInfo.getUsername(), hash)
````

