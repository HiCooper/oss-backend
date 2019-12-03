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

    /**
     * 是否为单机模式
     */
    private boolean singleton = true;

    /**
     * 是否开启热点数据缓存，单机模式效果不佳
     */
    private boolean hotDataCache = false;

    /**
     * 单机模式下 数据存储路径
     */
    private String dataPath = "./data";

    /**
     * 服务 访问ip
     */
    private String serverIp;

    /**
     * http 访问端口 默认 7077
     */
    private int httpPort = 7077;

    @Data
    public static class Mail {
        private boolean enabled;
        private String from;
        private String baseUrl;
    }
}
