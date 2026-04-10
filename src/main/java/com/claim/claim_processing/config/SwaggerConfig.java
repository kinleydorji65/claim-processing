package com.claim.claim_processing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NPPF Registration Service API")
                        .version("1.0.0")
                        .description("Registration Service for Agency and Member Management in NPPF System")
                        .contact(new Contact()
                                .name("NPPF Development Team")
                                .email("dev@nppf.org.bt")));
    }
}
