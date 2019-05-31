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

    private final static Logger logger = LoggerFactory.getLogger(UndertowConfig.class);

    @Bean
    UndertowServletWebServerFactory embeddedServletContainerFactory() {

        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();

        // 这里也可以做其他配置
        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        logger.info("UndertowHttp2...");

        return factory;
    }

}