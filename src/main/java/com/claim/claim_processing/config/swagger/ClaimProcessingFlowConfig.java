package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaimProcessingFlowConfig implements SwaggerConfig {

    @Bean
    public GroupedOpenApi claimProcessingFlowApi() {
        return createGroupedApi(
                "Claim Processing Flow Management",
                "Claim Processing Flow APIs",
                "/api/claim-processing-flow/**",
                "Claim Processing Flow"
        );
    }
}
