package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentSwagerConfig implements SwaggerConfig {
    @Bean
    public GroupedOpenApi documentApi() {
        return createGroupedApi(
            "Document Management",
            "Document Detail APIs",
            "/api/claim/documents/**",
            "Document"
        );
    }
}


// /api/claim/documents