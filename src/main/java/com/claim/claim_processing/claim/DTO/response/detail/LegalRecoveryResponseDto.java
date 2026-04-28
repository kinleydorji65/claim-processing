package com.claim.claim_processing.claim.DTO.response.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalRecoveryResponseDto {

    private Long id;

    private Long claimApplicationId;

    // ---------- Case Info ----------
    private String legalCaseReferenceNumber;

    // ---------- Master References ----------
    private Long recoveryReasonId;
    private String recoveryReasonName;

    private Long payeeTypeId;
    private String payeeTypeName;

    private Long schemeTypeId;
    private String schemeTypeName;

    private Long currentStatusId;
    private String currentStatusName;

    private Long loanTypeId;
    private String loanTypeName;

    private Long loanStatusId;
    private String loanStatusName;

    // ---------- Dates ----------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pfJoiningDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionJoiningDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate caseSettlementDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recoveryDate;

    // ---------- Financial ----------
    private BigDecimal recoveryRequestedAmount;

    // ---------- Member Info ----------
    private String memberCode;
    private String nppfNumber;
    private String agencyCode;
    private String employmentType;

    // ---------- Loan Info ----------
    private String loanAccountNumber;

    // ---------- Remarks ----------
    private String remarks;

    // ---------- Audit ----------
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
