package com.berry.oss.config;

import io.undertow.UndertowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Berry_Cooper.
 * Description:
 * Date: 2018-03-26
 * Time: 9:52
 */
@Configuration
public class UndertowConfig {

    private static final Logger logger = LoggerFactory.getLogger(UndertowConfig.class);

    private final GlobalProperties globalProperties;

    public UndertowConfig(GlobalProperties globalProperties) {
        this.globalProperties = globalProperties;
    }

    @Bean
    UndertowServletWebServerFactory embeddedServletContainerFactory() {

        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        int httpPort = globalProperties.getHttpPort();
        // 这里也可以做其他配置
        factory.addBuilderCustomizers(builder -> {
            builder.addHttpListener(httpPort, "0.0.0.0");
            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
        });
        logger.info("UndertowHttp2 init successful. http.port:[{}]", httpPort);

        return factory;
    }

}