package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaimApplicationSwaggerConfig implements SwaggerConfig {

    @Bean
    public GroupedOpenApi claimApplicationAPI() {
        return GroupedOpenApi.builder()
                .group("Claim Application API Management")
                .pathsToMatch(
                        "/api/claim/applications/**"
                )
                .displayName("Claim Application APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi normalClaimDetailApi() {
        return GroupedOpenApi.builder()
                .group("Normal Claim Detail API Management")
                .pathsToMatch(
                        "/api/claims/normal-details/**"
                )
                .displayName("Normal Claim Detail APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi partialClaimDetailApi() {
        return GroupedOpenApi.builder()
                .group("Partial Claim Detail API Management")
                .pathsToMatch(
                        "/api/claim/partial-withdrawals/**"
                )
                .displayName("Partial Claim Detail APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi legalRecoveryClaimDetailApi() {
        return GroupedOpenApi.builder()
                .group("Legal Recovery Claim Detail API Management")
                .pathsToMatch(
                        "/api/claim/legal-recoveries/**"
                )
                .displayName("Legal Recovery Claim Detail APIs")
                .build();
    }
}