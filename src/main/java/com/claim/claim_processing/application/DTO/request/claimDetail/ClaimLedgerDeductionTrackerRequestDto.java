package com.claim.claim_processing.application.DTO.request.claimDetail;

import lombok.*;

import java.math.BigDecimal;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimLedgerDeductionTrackerRequestDto {

    private Long claimId;

    private String nppfNumber;

    private BigDecimal totalPfAmount;

    private BigDecimal totalPcAmount;

    private BigDecimal totalDeductionAmount;

    private BigDecimal totalDeductedAmount;

    private BigDecimal balanceAmount;

    private ActivityEnum isActive;

    private String completedBy;
}
