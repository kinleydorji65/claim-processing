package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

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

//     @Bean
//     public GroupedOpenApi partialMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Partial Master Management")
//                 .pathsToMatch(
//                         "/api/claim/masters/business-types/**",
//                         "/api/claim/masters/disaster-types/**",
//                         "/api/claim/masters/house-purchase-types/**",
//                         "/api/partial-reasons/**"
//                 )
//                 .displayName("Partial Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi claimMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Claim Master Management")
//                 .pathsToMatch(
//                         "/api/claim/masters/account-types/**",
//                         "/api/claim/masters/cessation-types/**",
//                         "/api/claim/masters/claim-circumstances/**",
//                         "/api/claim-eligibility-category-map/**",
//                         "/api/master/claim-eligibility-component-map/**",
//                         "/api/claim/masters/claim-eligibilities/**",
//                         "/api/claims/lapsed-refund-category-map/**",
//                         "/api/master/claims/lapsed-refund-component-map/**",
//                         "/api/master/claim/lapsed-refund/**",
//                         "/api/master/claims/type-master/**",
//                         "/api/claim/master/claim-type-rule-map/**",
//                         "/api/claim/vesting-rules/**",
//                         "/api/claims/reserve-accounts/**",
//                         "/api/claim/masters/termination-reasons/**",
//                         "/api/master/vesting-refund-type/**"
//                 )
//                 .displayName("Claim Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi commonMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Common Master Management")
//                 .pathsToMatch(
//                         "/api/claim/common/rule-type/**",
//                         "/api/claim/master/action-master/**",
//                         "/api/claim/masters/claim-sources/**",
//                         "/api/claim/master/claim-type-deduction-map/**",
//                         "/api/claim/master/common/decisions/**",
//                         "/api/common/deduction-reference-types/**",
//                         "/api/claim/master/deduction-types/**",
//                         "/api/claim/common/interest-method/**",
//                         "/api/claim/common/payee-type/**",
//                         "/api/claim/common/review-status/**",
//                         "/api/claim/common/stage/**",
//                         "/api/claim/masters/submission-channels/**"
//                 )
//                 .displayName("Common Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi contributionMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Contribution Master Management")
//                 .pathsToMatch(
//                         "/api/benefit-component-details/**",
//                         "/api/benefit-component-types/**",
//                         "/api/claim/masters/schemes/**",
//                         "/api/master/contribution/component-master/**"
//                 )
//                 .displayName("Contribution Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi paymentMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Payment Master Management")
//                 .pathsToMatch(
//                         "/api/claim/master/payment-mode/**",
//                         "/api/claim/master/payment-status/**"
//                 )
//                 .displayName("Payment Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi loanMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Loan Master Management")
//                 .pathsToMatch(
//                         "/api/claim/loan-master/loan-adjustment-priority/**",
//                         "/api/claim/loan-master/loan-status/**",
//                         "/api/claim/loan-master/loan-type/**"
//                 )
//                 .displayName("Loan Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi refundMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Refund Master Management")
//                 .pathsToMatch(
//                         "/api/claim/masters/excess-refund-reasons/**",
//                         "/api/claim/masters/refund-scopes/**"
//                 )
//                 .displayName("Refund Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi specialCaseMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Special Case Master Management")
//                 .pathsToMatch(
//                         "/api/claim/masters/special-case-authorities/**",
//                         "/api/claim/master/special-case-refund-reason/**"
//                 )
//                 .displayName("Special Case Master APIs")
//                 .build();
//     }

//     @Bean
//     public GroupedOpenApi wrongRemittanceMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Wrong Remittance Master Management")
//                 .pathsToMatch(
//                         "/api/claim/wrong-remittance-error-type/**",
//                         "/api/claim/masters/remittance-reasons/**"
//                 )
//                 .displayName("Wrong Remittance Master APIs")
//                 .build();
//     }
    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder()
                .group("Rule Service APIs")
                .pathsToMatch(
                        "/api/rules/**"
                )
                .displayName("Rule Service APIs")
                .build();
    }

//     @Bean
//     public GroupedOpenApi statusMasterApi() {
//         return GroupedOpenApi.builder()
//                 .group("Status Master Management")
//                 .pathsToMatch(
//                         "/api/claim/approval-status/**",
//                         "/api/claim/calculation-status/**",
//                         "/api/claim/final-payable-review-status/**",
//                         "/api/claim/payment-line-status/**",
//                         "/api/claim/posting-entry-status/**",
//                         "/api/claim/posting-status/**",
//                         "/api/claim/rent-clearance-status/**",
//                         "/api/claim/reversal-status/**",
//                         "/api/claim/rule-evaluation-status/**",
//                         "/api/claim/tax-deposit-status/**",
//                         "/api/claim/verification-status/**"
//                 )
//                 .displayName("Status Master APIs")
//                 .build();
//     }
}