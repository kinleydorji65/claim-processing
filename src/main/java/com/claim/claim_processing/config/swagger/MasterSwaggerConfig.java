package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Collections;

@Configuration
public class MasterSwaggerConfig implements SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
            "NPPF Claim Processing Service API",
            "1.0.0",
            "Claim processing for agency and member",
            "NPPF Development Team",
            "dev@nppf.org.bt"
        );
    }

    // Beneficiary Management APIs
    @Bean
    public GroupedOpenApi beneficiaryMastersApi() {
        return createGroupedApi(
            "Master - Beneficiary Management",
            "Beneficiary APIs",
            "/api/claim/masters/claimant-types/**",
            "Claimant Type"
        );
    }

    // Claim Master Management APIs
    @Bean
    public GroupedOpenApi claimMasterApi() {
        return createGroupedApi(
            "Master - Claim Master Management",
            "Claim Master APIs",
            "/api/claim/masters/account-types/**",
            "Claim Master"
        );
    }

    // Claim Eligibility Management APIs
    @Bean
    public GroupedOpenApi claimEligibilityApi() {
        return createGroupedApi(
            "Master - Claim Eligibility Management",
            "Claim Eligibility APIs",
            "/api/claim-eligibility-category-map/**",
            "Claim Eligibility"
        );
    }

    // Claim Lapsed Refund Management APIs
    @Bean
    public GroupedOpenApi claimLapsedRefundApi() {
        return createGroupedApi(
            "Master - Claim Lapsed Refund Management",
            "Claim Lapsed Refund APIs",
            "/api/master/claim-eligibility-component-map/**",
            "Claim Lapsed Refund"
        );
    }

    // Claim Type Master Management API (First one - renamed)
    @Bean
    public GroupedOpenApi claimTypeMasterApiV1() {
        return createGroupedApi(
            "Master - Claim Type Master Management V1",
            "Claim Type Master APIs V1",
            "/api/master/claim/lapsed-refund/**",
            "Claim Type Master V1"
        );
    }

    // Claim Vesting Rules Management API (First one - renamed)
    @Bean
    public GroupedOpenApi claimVestingRulesApiV1() {
        return createGroupedApi(
            "Master - Claim Vesting Rules Management V1",
            "Claim Vesting Rules APIs V1",
            "/api/master/claims/lapsed-refund-component-map/**",
            "Claim Vesting Rules V1"
        );
    }

    // Claim Reserve Accounts Management API (First one - renamed)
    @Bean
    public GroupedOpenApi claimReserveAccountsApiV1() {
        return createGroupedApi(
            "Master - Claim Reserve Accounts Management V1",
            "Claim Reserve Accounts APIs V1",
            "/api/claims/lapsed-refund-category-map/**",
            "Claim Reserve Accounts V1"
        );
    }

    // Claim Type Master Management API (Second one)
    @Bean
    public GroupedOpenApi claimTypeMasterApiV2() {
        return createGroupedApi(
            "Master - Claim Type Master Management V2",
            "Claim Type Master APIs V2",
            "/api/master/claims/type-master/**",
            "Claim Type Master V2"
        );
    }

    // Claim Vesting Rules Management API (Second one)
    @Bean
    public GroupedOpenApi claimVestingRulesApiV2() {
        return createGroupedApi(
            "Master - Claim Vesting Rules Management V2",
            "Claim Vesting Rules APIs V2",
            "/api/claim/master/vesting-rules/**",
            "Claim Vesting Rules V2"
        );
    }

    // Claim Reserve Accounts Management API (Second one)
    @Bean
    public GroupedOpenApi claimReserveAccountsApiV2() {
        return createGroupedApi(
            "Master - Claim Reserve Accounts Management V2",
            "Claim Reserve Accounts APIs V2",
            "/api/claims/reserve-accounts/**",
            "Claim Reserve Accounts V2"
        );
    }

    // Partial Master Management APIs
    @Bean
    public GroupedOpenApi partialMasterApi() {
        return GroupedOpenApi.builder()
                .group("Master - Partial Master Management")
                .pathsToMatch(
                        "/api/claim/masters/partial-reasons/**",
                        "/api/claim/masters/partial-causes/**",
                        "/api/claim/masters/disaster-types/**",
                        "/api/claim/masters/business-types/**",
                        "/api/claim/masters/house-purchase-types/**"
                )
                .displayName("Partial Master APIs")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Partial Master"));
                    return operation;
                })
                .build();
    }

    // Refund Master Management APIs
    @Bean
    public GroupedOpenApi refundMasterApi() {
        return GroupedOpenApi.builder()
                .group("Master - Refund Master Management")
                .pathsToMatch(
                        "/api/claim/masters/refund-scopes/**",
                        "/api/claim/masters/excess-refund-reasons/**"
                )
                .displayName("Refund Master APIs")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Refund Master"));
                    return operation;
                })
                .build();
    }

    // Common Master Management APIs
    @Bean
    public GroupedOpenApi commonMasterApi() {
        return GroupedOpenApi.builder()
                .group("Master - Common Master Management")
                .pathsToMatch(
                        "/api/claim/masters/claim-sources/**",
                        "/api/claim/masters/submission-channels/**",
                        "/api/claim/master/action-master/**"
                )
                .displayName("Common Master APIs")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Common Master"));
                    return operation;
                })
                .build();
    }

    // Contribution Master Management APIs
    @Bean
    public GroupedOpenApi contributionApi() {
        return GroupedOpenApi.builder()
                .group("Master - Contribution Master Management")
                .pathsToMatch(
                        "/api/claim/masters/schemes/**",
                        "/api/benefit-component-types/**"
                )
                .displayName("Contribution Master APIs")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Contribution Master"));
                    return operation;
                })
                .build();
    }

    // Calculation Master Management APIs
    @Bean
    public GroupedOpenApi calculationMasterApi() {
        return GroupedOpenApi.builder()
                .group("Master - Calculation Master Management")
                .pathsToMatch(
                        "/api/claim/master/calculation-stage/**",
                        "/api/claim/master/calculation-trigger-type/**"
                )
                .displayName("Calculation Master APIs")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList("Calculation Master"));
                    return operation;
                })
                .build();
    }
}