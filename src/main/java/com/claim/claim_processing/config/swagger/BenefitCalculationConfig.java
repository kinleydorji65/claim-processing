package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BenefitCalculationConfig implements SwaggerConfig {
    @Bean
    public GroupedOpenApi benefitCalculationApi() {
        return createGroupedApi(
            "Benefit Calculation Management",
            "Benefit Calculation Detail APIs",
            "/api/claim-processing/benefit-calculation/**",
            "Benefit Calculation"
        );
    }
}
