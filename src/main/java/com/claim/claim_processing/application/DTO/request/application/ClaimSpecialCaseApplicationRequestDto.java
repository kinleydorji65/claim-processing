package com.claim.claim_processing.application.DTO.request.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

import com.claim.claim_processing.common.entities.common.activityEnum.CaseTypeEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplicationRequestDto {

    // Member Information
    private String memberCode;
    private String nppfNumber;
    private String identityNumber;

    // Agency Information
    private String agencyCategoryId;
    private String agencyCode;

    // Special Case Information
    @Builder.Default
    private CaseTypeEnum caseType = CaseTypeEnum.CONVERSION_FROM_PENSION_TO_LUMSUM;
    private String caseReason;

    // Amount Details
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;

    // For Pension Conversion
    private String currentBenefitType;
    private String requestedBenefitType;

    // For Forfeited Repayment
    private String forfeitedComponentCodes;

    // Request Information
    private String requestedBy;

    // Reserve Account Reference
    private Long reserveAccountId;
}
