package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationForfeitedComponentRequestDto {
    private Long forfeitedComponentId;
    private String componentCode;
    private String componentName;
    private String componentType; // FORFEITED
    private BigDecimal amount;
    private String reason;
    private String ruleCode;
    private String subClaimCode;
    private String createdBy;
    private String updatedBy;
}
