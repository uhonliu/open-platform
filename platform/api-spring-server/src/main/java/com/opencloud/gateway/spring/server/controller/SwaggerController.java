package com.opencloud.gateway.spring.server.controller;

import com.opencloud.common.configuration.OpenCommonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import java.util.Optional;

/**
 * @author liuyadu
 */
@RestController
@RequestMapping("/swagger-resources")
public class SwaggerController {
    /**
     * swagger安全配置
     *
     * @param commonProperties
     * @return
     */
    @Bean
    public SecurityConfiguration security(OpenCommonProperties commonProperties) {
        return new SecurityConfiguration(commonProperties.getClientId(), commonProperties.getClientSecret(), "realm", commonProperties.getClientId(), ",", null, null);
    }


    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration(true, false, 1, 1, ModelRendering.of("schema"), false, DocExpansion.of("list"), false, null, OperationsSorter.of("alpha"), false, TagsSorter.of("alpha"), null);
    }


    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;
    @Autowired(required = false)
    private UiConfiguration uiConfiguration;
    private final SwaggerResourcesProvider swaggerResources;

    @Autowired
    public SwaggerController(SwaggerResourcesProvider swaggerResources) {
        this.swaggerResources = swaggerResources;
    }


    @GetMapping("/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(securityConfiguration).orElse(null), HttpStatus.OK));
    }

    @GetMapping("/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(Optional.ofNullable(uiConfiguration).orElse(new UiConfiguration("/")), HttpStatus.OK));
    }

    @GetMapping("")
    public Mono<ResponseEntity> swaggerResources() {
        return Mono.just((new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK)));
    }
}