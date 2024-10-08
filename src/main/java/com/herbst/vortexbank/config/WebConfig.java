package com.herbst.vortexbank.config;

import com.herbst.vortexbank.serialization.converter.YamlJacksonHttpConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new YamlJacksonHttpConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        WebMvcConfigurer.super.configureContentNegotiation(
                configurer.favorParameter(true)
                        .ignoreAcceptHeader(false)
                        .useRegisteredExtensionsOnly(false)
                        .defaultContentType(MediaType.APPLICATION_JSON)
                        .mediaType("json", MediaType.APPLICATION_JSON)
                        .mediaType("xml", MediaType.APPLICATION_XML)
                        .mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YML)
        );
    }
}
