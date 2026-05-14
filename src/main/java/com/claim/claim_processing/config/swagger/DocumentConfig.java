package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentConfig implements SwaggerConfig {
    @Bean
    public GroupedOpenApi documentApi() {
        return createGroupedApi(
            "Document Management",
            "Document Detail APIs",
            "/api/claim-processing/documents/**",
            "Document"
        );
    }
}


// /api/claim/documents