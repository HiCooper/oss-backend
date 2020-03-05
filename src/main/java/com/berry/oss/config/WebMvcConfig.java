package com.berry.oss.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.berry.oss.security.interceptor.AccessInterceptor;
import com.berry.oss.security.interceptor.AccessProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2018-05-03 15:42
 * fileName：WebMvcConfig
 * Use：
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessProvider accessProvider;

    public WebMvcConfig(AccessProvider accessProvider) {
        this.accessProvider = accessProvider;
    }

    /**
     * 添加类型转换器和格式化器
     *
     * @param registry registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(Date.class, new DateFormatter());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.ALL);
        converter.setSupportedMediaTypes(fastMediaTypes);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setFastJsonConfig(fastJsonConfig);
        converters.add(converter);
    }

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessInterceptor(accessProvider))
                .addPathPatterns("/ajax/**")
                .excludePathPatterns("/index.html", "/", "/auth/login", "/swagger-ui.html");
    }
}