package com.claim.claim_processing.rule.ruleGateWay.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRuleMasterResponseDto {

    private Long id;

    private String ruleCode;

    private String ruleName;

    private String description;

    private Long priorityOrder;

    private String stopOnSuccess;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String isActive;
    private Long ruleTypeId;
    private Long loanTypeId;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
    
}
