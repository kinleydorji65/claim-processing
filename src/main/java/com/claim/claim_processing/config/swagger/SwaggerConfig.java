package com.claim.claim_processing.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import java.util.Collections;

public interface SwaggerConfig {
    
    default OpenAPI createOpenAPI(String title, String version, String description, 
                                   String contactName, String contactEmail) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)));
    }
    
    default GroupedOpenApi createGroupedApi(String groupName, String displayName, 
                                            String pathsToMatch, String tagName) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(pathsToMatch)
                .displayName(displayName)
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.setTags(Collections.singletonList(tagName));
                    return operation;
                })
                .build();
    }
}