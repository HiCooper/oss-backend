# 合规保留策略(WORM)

- 支持级别： bucket
- Bucket内的Object在合规保留策略生效期间，可通过设置生命周期规则进行存储类型转化

### 规则说明
OSS允许添加一条基于时间的合规保留策略，保护周期为1天到70年。

假设您在2013年6月1日创建一个名为examplebucket的Bucket，并且在不同时间上传了file1.txt、 file2.txt、file3.txt三个Object。随后，在2014年7月1日创建了保护周期为5年的合规保留策略。有关这三 个Object的具体上传时间以及对应的Object到期时间如下:

| Object名称 | 上传时间 | Object到期时间 |
|------|------|------|
| file1.txt | 2013年6月1日 |2018年5月31日 |
| file2.txt | 2014年7月1日 |2019年6月30日 |
| file3.txt | 2018年9月30日 |2023年9月29日 |

- 生效规则
    - 当基于时间的合规保留策略创建后，该策略默认处于“InProgress”状态，且该状态的有效期为 24 小 时。 在有效期24小时内，此策略对应的Bucket资源处于保护状态。
    - 启动合规保留策略24小时内:若该策略未提交锁定，则Bucket所有者以及授权用户可以删除该策略;若该保留策略已提交锁定，则不允许删除该策略，且无法缩短策略保护周期，仅可以延长保护周期。 
    - 启动合规保留策略24小时后:若超过24小时该保留策略未提交锁定，则该策略自动失效(Failure)。 
    - Bucket内的数据处于被保护状态时，若您尝试删除或修改这些数据，API 将返回 `409 FileImmutabl e` 的错误信息。

- 删除规则
    - 基于时间的合规保留策略是Bucket的一种Metadata属性。当删除某个Bucket时，该Bucket对应的合 规保留策略以及访问策略也会被删除。因此当Bucket为空时，Bucket的所有者可以删除该Bucket，从 而间接删除该Bucket的保留策略。 说明 OSS是目前中国国内唯一通过Cohasset Associates审计认证的云服务，可满足严格的电子 记录保留要求，例如SEC Rule 17a-4(f)、FINRA 4511、CFTC 1.31等合规要求。详情请参见 OSS Cohasset Assessment Report。
    - 启动保留策略24小时内，若该保留策略未提交锁定，则Bucket所有者以及授权用户可以删除该策略。
    - 若Bucket内有文件处于保护周期内，那么您将无法删除保留策略，同时也无法删除Bucket。


### 常见问题
合规保留策略的优势
合规保留策略可提供数据合规存储，数据在合规保留策略保护周期内，任何用户都不能删除和修改。而通 过RAM policy和Bucket Policy保护的数据，则存在被修改和删除可能。
什么情况下需要配置合规保留策略
您需要长期存储且不允许修改或删除的重要数据，如医疗档案、技术文件、合同文书等，可以存放在指定 的Bucket内，并通过开启合规保留策略保护您的重要数据。
是否支持针对Object设置合规保留策略
目前仅支持针对Bucket设置保留策略，不支持针对目录以及单个对象设置合规保留策略。
如何删除已开启合规保留策略的Bucket 若该Bucket内未存储文件，可以直接删除该Bucket。
若该Bucket内已存储文件，且所有文件均已过了保护期，删除该Bucket会提示失败。此时，您可以先 删除该Bucket内所有文件，再删除Bucket。
若该Bucket内已存储文件，且还有文件处于保护期内，无法删除该Bucket。