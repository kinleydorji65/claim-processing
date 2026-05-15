package com.claim.claim_processing.config.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RentalAdjustmentSwaggerConfig implements SwaggerConfig {

    @Bean
    public GroupedOpenApi rentalAdjustmentApi() {
        return createGroupedApi(
                "Rental Adjustment Management",
                "Rental Adjustment APIs",
                "/api/claim/rental-details/**",
                "Rental Adjustment"
        );
    }
}