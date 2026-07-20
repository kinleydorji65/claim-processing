package com.claim.claim_processing.application.DTO.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.application.DTO.response.application.AccountingEventResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse;
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
public class GeneralSpecialCaseResponse {
    private Long id;
    private String applicationNumber;

    private Long claimTypeId;
    private String claimTypeName;

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
    private String email;
    private String contactNo;
    private Long officeId;

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

    private Long currentStageId;
    private String currentStageName;
    private String createdBy;

    private Long statusId;
    private String statusName;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    private ClaimApplicationBankResponseDto bankDetail;
    private ClaimSpecialCaseResponse specialCaseDetail;
    private List<ClaimApplicationWorkflowResponseDto> workflowDetails;
    private ClaimApplicationVerificationResponseDto verificationDetail;
    private ClaimApplicationApprovalResponseDto approvalDetail;
    private AccountingEventResponseDto accountingEventDetail;
}
