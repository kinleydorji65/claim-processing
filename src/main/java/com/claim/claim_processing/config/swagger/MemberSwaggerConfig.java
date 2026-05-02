package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class MemberSwaggerConfig implements SwaggerConfig {

    @Bean
    public GroupedOpenApi memberRegistrationApi() {
        return createGroupedApi(
            "Member Management",
            "Member Detail APIs",
            "/api/claim-processing/members/**",
            "Member Registration"
        );
    }
}
