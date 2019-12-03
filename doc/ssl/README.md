# 生成证书：

> 使用阿里云免费SSL证书

改别名 -> tomcat
````shell script
keytool -changealias -keystore domain hicooer.cn.pfx -alias alias -destalias tomcat
````