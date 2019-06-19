package com.berry.oss.security.vm;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-05 22:49
 * fileName：UserInfoDTO
 * Use：
 */
@Data
@Accessors(chain = true)
public class UserInfoDTO {

    private Integer id;

    private String username;

    public UserInfoDTO() {

    }

    public UserInfoDTO(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

}
