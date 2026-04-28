package com.claim.claim_processing.claim.DTO.response.application;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.claim.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.claim.DTO.response.payment.ClaimApplicationPaymentResponseDto;
import com.claim.claim_processing.claim.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.claim.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.claim.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationResponseDto {

    private Long id;

    // ---------------------------------
    // Basic Info
    // ---------------------------------
    private String applicationNumber;

    private Long claimTypeId;
    private String claimTypeCode;
    private String claimTypeName;

    private Long claimSourceId;
    private String claimSourceCode;
    private String claimSourceName;

    private Long submissionChannelId;
    private String submissionChannelCode;
    private String submissionChannelName;

    private Long schemeTypeId;
    private String schemeTypeCode;
    private String schemeTypeName;

    private Long memberCategoryId;
    private String memberCategoryCode;
    private String memberCategoryName;

    private String employmentType;
    private String memberCode;
    private String nppfNumber;
    private String agencyCode;
    private Long officeId;

    // ---------------------------------
    // Dates
    // ---------------------------------
    private LocalDate applicationDate;

    // ---------------------------------
    // Users
    // ---------------------------------
    private String submittedBy;
    private String initiatedBy;

    // ---------------------------------
    // Flags
    // ---------------------------------
    private ActivityEnum isSpecialCase;
    private ActivityEnum requiresManualReview;
    private ActivityEnum isFinancialCase;
    private ActivityEnum isPaymentRequired;
    private ActivityEnum isPostingRequired;
    private ActivityEnum requiresManualApproval;
    private ActivityEnum isActive;

    // ---------------------------------
    // Parent Relationship
    // ---------------------------------
    private Long parentClaimApplicationId;

    // ---------------------------------
    // Special Case
    // ---------------------------------
    private Long specialCaseAuthorityId;
    private String specialCaseAuthorityCode;
    private String specialCaseAuthorityName;

    // ---------------------------------
    // Workflow
    // ---------------------------------
    private Long currentStageId;
    private String currentStageCode;
    private String currentStageName;

    private Long statusId;
    private String statusCode;
    private String statusName;

    private Long actionId;
    private String actionCode;
    private String actionName;

    // ---------------------------------
    // Misc
    // ---------------------------------
    private String currencyCode;
    private String remarks;

    // ---------------------------------
    // Children (summaries only - avoid full nesting explosion)
    // ---------------------------------
    private List<ClaimApplicationBankResponseDto> bankDetails;

    private List<ClaimApplicationDeductionResponseDto> deductionDetails;

    private List<ClaimApplicationLoanResponseDto> loanDetails;

    // Optional (enable only if needed)
    private List<ClaimApplicationCalculationSummaryResponseDto> calculationSummaries;
    private List<ClaimApplicationPaymentResponseDto> payments;
    private List<ClaimApplicationVerificationResponseDto> verifications;
    private List<ClaimApplicationApprovalResponseDto> approvals;
    private List<ClaimApplicationWorkflowResponseDto> workflows;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
