package com.hanno.prioqueue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hanno.prioqueue.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder()
                .supportedSubmitMethods(new String[]{"get", "post", "put", "patch", "delete"})
                .build();
    }

}
