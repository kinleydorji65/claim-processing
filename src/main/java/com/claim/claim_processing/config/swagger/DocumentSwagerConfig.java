package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentSwagerConfig implements SwaggerConfig {
    @Bean
    public GroupedOpenApi documentApi() {
        return GroupedOpenApi.builder()
                .group("Document API Management")
                .pathsToMatch(
                        "/api/claim/documents/**"
                )
                .displayName("Document APIs")
                .build();
    }
}


// /api/claim/documents