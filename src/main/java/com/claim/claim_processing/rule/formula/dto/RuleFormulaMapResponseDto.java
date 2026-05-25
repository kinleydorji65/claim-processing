package com.claim.claim_processing.rule.formula.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RuleFormulaMapResponseDto {

    private Long id;

    private Long formulaRuleId;
    private Long conditionId;
    private String subRuleName;
    private String categoryId;
    private String categoryName;

    private String isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}
