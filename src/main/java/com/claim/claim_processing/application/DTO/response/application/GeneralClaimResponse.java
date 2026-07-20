package com.claim.claim_processing.application.DTO.response.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralClaimResponse {
    private Long id;
    private String applicationNumber;

    private Long claimTypeId;
    private String claimTypeName;
    private String identityNumber;

    private Long submissionChannelId;
    private String submissionChannelName;

    private Long schemeTypeId;
    private String schemeTypeName;

    private String memberCategoryId;
    private String memberCategoryName;

    private String employmentType;
    private String memberCode;
    private String nppfNumber;
    private String agencyCode;
    private Long officeId;
    private String email;
    private String contactNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applicationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pfStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pfEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionStartDate;

    private ActivityEnum isLoanApplied;
    private ActivityEnum isRentalApplied;

    private String onBehalfOfMember;
    private String initiatedBy;
    private String remarks;

    private ActivityEnum isSpecialCase;
    private ActivityEnum isActive;
    

    private String currencyCode;
    private String claimedBy;
    private String unClaimedBy;

    private Long statusId;
    private String statusName;

    private List<ClaimApplicationBankResponseDto> bankDetails;
    private List<ClaimApplicationWorkflowResponseDto> workflowDetails;
    private ClaimApplicationDeductionResponseDto deductionDetail;
    private ClaimApplicationCalculationSummaryResponseDto calculationSummary;
    private NormalClaimResponseDto normalClaimDetails;
    private LegalRecoveryResponseDto legalRecoveryDetail;
    private PartialWithdrawalResponseDto partialWithdrawalDetails;
    private BeneficiarySettlementResponseDto beneficiarySettlementDetails;
    private List<ClaimApplicationForfeitedComponentResponseDto> forfeitedComponents;
    private ClaimApplicationVerificationResponseDto verificationDetail;
    private ClaimApplicationApprovalResponseDto approvalDetail;
    private AccountingEventResponseDto accountingEventDetail;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
