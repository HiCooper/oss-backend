package com.berry.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for whole project.
 * <p>
 * Properties are configured in the application.yml file.
 *
 * @author xueancao
 */
@Component
@ConfigurationProperties(prefix = "global")
@Data
public class GlobalProperties {


    private final Mail mail = new Mail();

    private boolean singleton;
    private String dataPath;

    @Data
    public static class Mail {
        private boolean enabled;
        private String from;
        private String baseUrl;
    }
}
