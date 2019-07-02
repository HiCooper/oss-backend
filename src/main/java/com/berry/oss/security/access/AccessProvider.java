package com.berry.oss.security.access;

import com.berry.oss.common.ResultCode;
import com.berry.oss.common.constant.Constants;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.Auth;
import com.berry.oss.core.service.IAccessKeyInfoDaoService;
import com.berry.oss.security.core.entity.Role;
import com.berry.oss.security.core.service.IUserDaoService;
import com.berry.oss.security.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-29 17:22
 * fileName：AccessProvider
 * Use：
 */
@Component
public class AccessProvider {
    private static final Logger logger = LoggerFactory.getLogger(AccessProvider.class);

    private final IAccessKeyInfoDaoService accessKeyInfoDaoService;

    @Resource
    private IUserDaoService userDaoService;

    public AccessProvider(IAccessKeyInfoDaoService accessKeyInfoDaoService) {
        this.accessKeyInfoDaoService = accessKeyInfoDaoService;
    }

    Authentication getUploadAuthentication(String accessToken, String requestUrl) throws IllegalAccessException {
        String[] data = accessToken.split(":");
        if (data.length != Constants.ENCODE_DATA_LENGTH) {
            throw new BaseException(ResultCode.ILLEGAL_ACCESS_TOKEN);
        }
        String accessKeyId = data[0];
        UserInfoDTO principal = accessKeyInfoDaoService.getUserInfoDTO(accessKeyId);
        if (principal == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }

        // 验证token有效性，请求无法获取错误信息
        Auth.verifyUploadToken(data, principal.getAccessKeySecret(), requestUrl);

        Set<Role> roleList = userDaoService.findRoleListByUserId(principal.getId());
        List<GrantedAuthority> grantedAuthorities = roleList.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(principal, accessToken, grantedAuthorities);
    }

    Authentication getSdkAuthentication(String ossAuth, HttpServletRequest request) throws IllegalAccessException {
        if (!ossAuth.startsWith(Constants.OSS_SDK_AUTH_PREFIX)) {
            throw new BaseException(ResultCode.ILLEGAL_ACCESS_TOKEN);
        }
        String dataStr = ossAuth.substring(4);

        String[] data = dataStr.split(":");
        if (data.length != Constants.ENCODE_SDK_DATA_LENGTH) {
            throw new BaseException(ResultCode.ILLEGAL_ACCESS_TOKEN);
        }
        String accessKeyId = data[0];
        UserInfoDTO principal = accessKeyInfoDaoService.getUserInfoDTO(accessKeyId);
        if (principal == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }

        byte[] body;
        try {
            body = getRequestBody(request);
        } catch (IOException e) {
            throw new BaseException("400", "读取请求体异常");
        }
        String contentType =request.getContentType();
        System.out.println(contentType);

        // 校验 token 签名
        Auth.validRequest(ossAuth, request.getRequestURI(), body, request.getContentType(), accessKeyId, principal.getAccessKeySecret());

        Set<Role> roleList = userDaoService.findRoleListByUserId(principal.getId());
        List<GrantedAuthority> grantedAuthorities = roleList.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(principal, ossAuth, grantedAuthorities);
    }

    private static byte[] getRequestBody(HttpServletRequest request) throws IOException {
        int length = request.getContentLength();
        if (length == -1){
            return null;
        }
        ServletInputStream inputStream = request.getInputStream();
        byte[] body = new byte[length];
        int read = inputStream.read(body, 0, length);
        if (read != length) {
            logger.error("read request body length error");
        }
        return body;
    }
}
