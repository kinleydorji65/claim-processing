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
                        "/api/claims/reserve-accounts/**",
                        "/api/claim/master/claim-type-rule-map/**"
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
                        "/api/claim/masters/house-purchase-types/**",
                        "/api/partial-withdrawal/reason-cause-map/**",
                        "/api/claim/partial-withdrawal-rule/**"
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
                        "/api/claim/masters/submission-channels/**",
                        "/api/claim/master/action-master/**",
                        "/api/claim/master/deduction-types/**",
                        "/api/claim/master/claim-type-deduction-map/**",
                        "/api/claim/master/common/decisions/**",
                        "/api/common/deduction-reference-types/**",
                        "/api/claim/common/interest-method/**",
                        "/api/claim/common/payee-type/**",
                        "/api/claim/common/review-status/**",
                        "/api/claim/common/rule-type/**",
                        "/api/claim/common/stage/**"
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

    @Bean
    public GroupedOpenApi calculationMasterApi() {
        return GroupedOpenApi.builder()
                .group("calculation Master management")
                .pathsToMatch(
                        "/api/claim/master/calculation-stage/**",
                        "/api/claim/master/calculation-trigger-type/**"
                )
                .displayName("Calculation Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi loanTypeMasterApi() {
        return GroupedOpenApi.builder()
                .group("loan Master management")
                .pathsToMatch(
                        "/api/claim/loan-master/loan-type/**",
                        "/api/claim/loan-master/loan-adjustment-priority/**",
                        "/api/claim/loan-master/loan-status/**"
                )
                .displayName("loan Type Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentMasterApi() {
        return GroupedOpenApi.builder()
                .group("payment Master management")
                .pathsToMatch(
                        "/api/claim/master/payment-mode/**",
                        "/api/claim/master/payment-status/**"
                )
                .displayName("payment Master APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi specialCaseMasterApi() {
        return GroupedOpenApi.builder()
                .group("special case Master management")
                .pathsToMatch(
                        "/api/claim/masters/special-case-authorities/**",
                        "/api/claim/masters/special-case-reasons/**"
                )
                .displayName("special case Master APIs")
                .build();
    }
}
