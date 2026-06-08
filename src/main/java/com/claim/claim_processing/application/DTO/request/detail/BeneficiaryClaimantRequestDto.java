package com.claim.claim_processing.application.DTO.request.detail;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryClaimantRequestDto {
    private Long beneficiaryClaimantDetailId;
    private Long nomineeId;

    private Long dependentId;

    private Long claimantTypeId;

    private Long relationshipTypeId;

    private String beneficiaryIdentifier;

    private String beneficiaryName;

    private LocalDate dateOfBirth;

    private BigDecimal beneficiarySharePercentage;

    private Long payeeTypeId;

    private ActivityEnum isMemberFamily;

    private ActivityEnum isMinor;

    private String guardianName;

    private String guardianIdentifier;

    private BigDecimal benefitAmount;

    private ActivityEnum isEligible;

    private ActivityEnum isSelected;

    private String remarks;
}