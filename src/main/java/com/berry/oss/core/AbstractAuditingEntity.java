package com.berry.oss.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 *
 * @author HiCooper
 */
@Data
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private Instant createdDate = Instant.now();

    @JsonIgnore
    private String lastModifiedBy;

    @JsonIgnore
    private Instant lastModifiedDate = Instant.now();

}
