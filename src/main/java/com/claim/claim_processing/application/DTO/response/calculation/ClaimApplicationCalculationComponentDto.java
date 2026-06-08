package com.claim.claim_processing.application.DTO.response.calculation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationComponentDto {

    private Long id;

    private Long ruleEvaluationId;

    private String componentCode;

    private String componentName;

    private BigDecimal amount;

    private ActivityEnum isDeduction;

    private String notes;

    private ActivityEnum isActive;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}