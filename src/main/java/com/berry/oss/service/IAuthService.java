package com.berry.oss.service;

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
     * 检查用户 是否拥有权限访问 bucket
     * todo 目前没有设置用户组授权，仅检验该 bucket 是否属于 该用户
     * 后续加入用户组授权 以及 bucket 其他账户授权，该用户继承用户组权限，并且 获得 特定 账户授予的 特定 bucket 访问权限
     *
     * @param userId 用户id
     * @param bucket   bucket name
     * @return 是 or 否
     */
    Boolean checkUserHaveAccessToBucket(Integer userId, String bucket);
}
