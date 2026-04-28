package com.claim.claim_processing.claim.DTO.response.calculation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationComponentDto {

    private Long id;

    private Long claimApplicationId;
    private Long calculationSummaryId;

    private String beneficiaryIdentifier;

    private Long componentTypeId;
    private String componentTypeName;
    private Integer displayOrder;

    private ActivityEnum isPensionRelated;
    private ActivityEnum isInterestComponent;
    private ActivityEnum isDeductionComponent;

    private BigDecimal baseAmount;
    private BigDecimal eligibleAmount;
    private BigDecimal contributionAmount;
    private BigDecimal balanceAmount;

    private BigDecimal rateApplied;
    private String rateTypeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodFrom;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodTo;
    private Integer contributionMonths;

    private BigDecimal interestAmount;
    private BigDecimal grossComponentAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netComponentAmount;

    private BigDecimal verifiedComponentAmount;
    private BigDecimal approvedComponentAmount;
    private BigDecimal paidComponentAmount;
    private BigDecimal postedComponentAmount;

    private String calculationFormula;
    private String formulaDescription;
    private String ruleReference;
    private String calculationNotes;

    private Long calculationStatusId;

    private ActivityEnum isActive;

    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
