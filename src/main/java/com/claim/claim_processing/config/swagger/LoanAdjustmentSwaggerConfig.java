package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanAdjustmentSwaggerConfig implements SwaggerConfig {

    @Bean
    public GroupedOpenApi loanAdjustmentApi() {
        return createGroupedApi(
                "Loan Adjustment Management",
                "Loan Adjustment APIs",
                "/api/claim/loan-details/**",
                "Loan Adjustment"
        );
    }
}
