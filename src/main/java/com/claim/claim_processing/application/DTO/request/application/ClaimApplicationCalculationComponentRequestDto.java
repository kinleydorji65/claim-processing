package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationComponentRequestDto {
    private Long ruleEvaluationId;
    private String componentCode;
    private String componentName;
    private String componentType;
    private BigDecimal amount;

    private ActivityEnum isDeduction;
    private String notes;

    private ActivityEnum isActive;
    private String createdBy;
    private Timestamp createdAt;

    private String updatedBy;

    private Timestamp updatedAt;
}
