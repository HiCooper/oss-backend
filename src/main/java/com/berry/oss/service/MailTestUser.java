package com.berry.oss.service;

import com.berry.oss.common.constant.Constants;
import com.berry.oss.core.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A user.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MailTestUser extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 6)
    private String langKey;

    @Size(max = 256)
    private String imageUrl;

    @Size(max = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @JsonIgnore
    private String resetKey;

    private Instant resetDate = null;
}
