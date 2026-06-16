package com.claim.claim_processing.application.DTO.response.claimDetail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimLedgerDeductionTrackerResponseDto {

    private Long id;

    private Long claimId;

    private String applicationNumber;

    private Long claimTypeId;

    private String claimTypeName;

    private String nppfNumber;

    private BigDecimal totalPfAmount;

    private BigDecimal totalPcAmount;

    private BigDecimal totalDeductionAmount;

    private BigDecimal totalDeductedAmount;

    private BigDecimal balanceAmount;

    private ActivityEnum isCompleted;

    private LocalDateTime completedAt;

    private String completedBy;

    private String remarks;

    private ActivityEnum isActive;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
