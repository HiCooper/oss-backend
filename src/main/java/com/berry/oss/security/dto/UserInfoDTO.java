package com.berry.oss.security.dto;

import com.berry.oss.security.interceptor.AccessKeyPair;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-05 22:49
 * fileName：UserInfoDTO
 * Use：
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserInfoDTO extends AccessKeyPair {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    public UserInfoDTO() {

    }

    public UserInfoDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
