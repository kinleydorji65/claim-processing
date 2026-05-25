package com.claim.claim_processing.rule.formula.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClaimFormulaResponseDto {

    private Long id;

    private String formulaCode;

    private String formulaName;

    private String description;

    private String expressionText;

    private String outputVariableCode;

    private String returnType;

    private Long versionNo;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

    private List<RuleFormulaMapResponseDto> ruleFormulaMaps;

    private List<FormulaComponentMapResponseDto> formulaComponents;
}
