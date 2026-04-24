package com.claim.claim_processing.common.DTO.response.arrRule;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrRuleResponseDto {

    private Long id;

    // -------------------------------
    // BASIC INFO
    // -------------------------------
    private String ruleCode;
    private String ruleName;

    private Long schemeTypeId;
    private String memberCategoryId;

    // -------------------------------
    // CREDIT METHOD (FK)
    // -------------------------------
    private Long creditMethodId;
    private String creditMethodCode;
    private String creditMethodName;

    // -------------------------------
    // COMPONENT FLAGS
    // -------------------------------
    private String includePfMc;
    private String includePfEc;
    private String includePfGc;
    private String includePfVc;
    private String includePfInterest;

    private String includePcMc;
    private String includePcEc;
    private String includePcInterest;

    private String isPensionBalanceIncluded;

    // -------------------------------
    // FORMULA & RULE
    // -------------------------------
    private String formulaExpression;
    private String roundingMethodCode;

    private Integer priorityOrder;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String remarks;

    // -------------------------------
    // STATUS
    // -------------------------------
    private ActivityEnum isActive;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
