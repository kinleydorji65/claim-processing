package com.claim.claim_processing.application.DTO.request.detail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalRecoveryRequestDto {

    private Long claimApplicationId;

    private String legalCaseReferenceNumber;

    private Long recoveryReasonId;

    private Long payeeTypeId;

    private LocalDate pfJoiningDate;
    private LocalDate pensionJoiningDate;

    private LocalDate caseSettlementDate;
    private LocalDate recoveryDate;

    private BigDecimal recoveryRequestedAmount;

    private String remarks;

    private String memberCode;
    private String nppfNumber;
    private String agencyCode;

    private Long schemeTypeId;

    private String employmentType;

    private Long currentStatusId;

    private String loanAccountNumber;

    private Long loanTypeId;

    private Long loanStatusId;

    private String createdBy;
    private String updatedBy;
}