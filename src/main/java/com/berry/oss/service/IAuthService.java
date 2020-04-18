package com.berry.oss.service;

import com.berry.oss.security.dto.UserInfoDTO;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-30 14:05
 * fileName：IAuthService
 * Use：
 */
public interface IAuthService {

    /**
     * 检查用户 是否拥有权限访问 bucket <br/>
     * 1. 用户组授权，该用户继承用户组权限，<br/>
     * 2. bucket 其他账户授权,授予特定 bucket(对象/目录) 的访问权限
     *
     * @param user       用户
     * @param bucket     bucket name
     * @param objectPath 对象全路径 如 /test/a.png
     * @return 是 or 否
     */
    Boolean checkUserHaveAccessToBucketObject(UserInfoDTO user, String bucket, String objectPath);
}
