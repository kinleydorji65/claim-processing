package com.claim.claim_processing.rule.formula.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FormulaComponentMapResponseDto {

    private Long id;

    private String variableCode;

    private Long ruleComponentMapId;

    private Long componentId;
    private String componentName;

    private String sourceType;

    private String isRequired;

    private String isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}
