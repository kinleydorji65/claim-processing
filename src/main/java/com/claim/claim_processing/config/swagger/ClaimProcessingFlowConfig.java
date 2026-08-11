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

    @Bean
    public GroupedOpenApi specialCaseProcessingFlowApi() {
        return createGroupedApi(
                "Special Case Processing Flow Management",
                "Special Case Processing Flow APIs",
                "/api/claim-application/special-case/**",
                "Special Case Processing Flow"
        );
    }

    @Bean
    public GroupedOpenApi unclaimProcessingApi() {

        return createGroupedApi(
                "Unclaim Processing Management",
                "Unclaim Processing APIs",
                "/api/un-claim-processing/unclaim/**",
                "Unclaim Processing"
        );
    }

    @Bean
    public GroupedOpenApi documentProcessingApi() {

        return createGroupedApi(
                "Claim Document Processing Management",
                "Claim Document Processing APIs",
                "/api/claim-processing-flow/documents/**",
                "Claim Document Processing"
        );
    }

    @Bean
    public GroupedOpenApi validateComponentApi() {

        return createGroupedApi(
                "Validate Component Management",
                "Validate Component APIs",
                "/api/validate/**",
                "Validate Component"
        );
    }

    @Bean
    public GroupedOpenApi wrongRemitanceManagementApi() {

        return createGroupedApi(
                "Wrong Remitance Management",
                "Wrong Remitance APIs",
                "/api/wrong-remitance/contributions/**",
                "Wrong Remitance Management"
        );
    }

    @Bean
    public GroupedOpenApi fullContributionManagementApi() {

        return createGroupedApi(
                "Full Contribution History Management",
                "Full Contribution History APIs",
                "/api/full-contribution-history/**",
                "Full Contribution History Management"
        );
    }

    @Bean
    public GroupedOpenApi fullCurrencyManagementApi() {

        return createGroupedApi(
                "Currency Conversion Management",
                "Currency Conversion APIs",
                "/api/currency/**",
                "Currency Conversion Management"
        );
    }

    @Bean
    public GroupedOpenApi visaDownloadManagementApi() {

        return createGroupedApi(
                "Visa Download Management",
                "Visa Download APIs",
                "/api/visa/download/**",
                "Visa Download Management"
        );
    }
}

