package com.claim.claim_processing.common.config;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NPPF Claim Processing Service API")
                        .version("1.0.0")
                        .description("Claim processing for agency and member")
                        .contact(new Contact()
                                .name("NPPF Development Team")
                                .email("dev@nppf.org.bt")));
    }
    @Bean
    public GroupedOpenApi beneficiaryMastersApi() {
        return GroupedOpenApi.builder()
                .group("Beneficiary Management")
                .pathsToMatch("/api/claim/masters/claimant-types/**")
                .displayName("Beneficiary APIs")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Claimant Type"));
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi claimMasterApi() {
        return GroupedOpenApi.builder()
                .group("Claim Master Management")
                .pathsToMatch(
                        "/api/claim/masters/account-types/**",
                        "/api/claim/masters/cessation-types/**",
                        "/api/claim/masters/claim-circumstances/**",
                        "/api/claim/masters/claim-eligibilities/**",
                        "/api/claim/masters/termination-reasons/**",
                        "/api/claim-eligibility-category-map/**",
                        "/api/master/claim-eligibility-component-map/**",
                        "/api/master/claim/lapsed-refund/**",
                        "/api/master/claims/lapsed-refund-component-map/**",
                        "/api/claims/lapsed-refund-category-map/**",
                        "/api/master/claims/type-master/**",
                        "/api/claim/master/vesting-rules/**",
                        "/api/claims/reserve-accounts/**"
                )
                .displayName("Claim Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi partialMasterApi() {
        return GroupedOpenApi.builder()
                .group("Partial Master Management")
                .pathsToMatch(
                        "/api/claim/masters/partial-reasons/**",
                        "/api/claim/masters/partial-causes/**",
                        "/api/claim/masters/disaster-types/**",
                        "/api/claim/masters/business-types/**",
                        "/api/claim/masters/house-purchase-types/**"
                )
                .displayName("Partial Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi refundMasterApi() {
        return GroupedOpenApi.builder()
                .group("Refund Master Management")
                .pathsToMatch(
                        "/api/claim/masters/refund-scopes/**",
                        "/api/claim/masters/excess-refund-reasons/**"
                )
                .displayName("Refund Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi commonMasterApi() {
        return GroupedOpenApi.builder()
                .group("Common Master Management")
                .pathsToMatch(
                        "/api/claim/masters/claim-sources/**",
                        "/api/claim/masters/submission-channels/**"
                )
                .displayName("Common Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi contributionApi() {
        return GroupedOpenApi.builder()
                .group("contribution Master management")
                .pathsToMatch(
                        "/api/claim/masters/schemes/**",
                        "/api/benefit-component-types/**"
                )
                .displayName("Contribution Master APIs")
                .build();
    }
}
