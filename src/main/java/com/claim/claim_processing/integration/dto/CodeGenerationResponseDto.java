package com.claim.claim_processing.integration.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeGenerationResponseDto {
    private boolean success;
    private String message;
    private String generatedCode;
    private String codeType;
    private String prefix;
    private String subCodePrefix;
    private OffsetDateTime generatedAt;
}
