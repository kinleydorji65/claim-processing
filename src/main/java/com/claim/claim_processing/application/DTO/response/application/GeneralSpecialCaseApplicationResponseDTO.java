package com.claim.claim_processing.application.DTO.response.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationBankDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationWorkflowResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralSpecialCaseApplicationResponseDTO {

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

    private Long specialCaseAuthorityId;
    private String specialCaseAuthorityName;

    private String currencyCode;
    private String claimedBy;
    private String unClaimedBy;

    private Long currentStageId;
    private String currentStageName;

    private Long statusId;
    private String statusName;
    
    private ClaimApplicationBankResponseDto bankDetail;
    private List<ClaimApplicationWorkflowResponseDto> workflowDetails;
    private ClaimSpecialCaseApplicationResponseDto claimSpecialCaseApplicationResponseDto;
    private ClaimApplicationVerificationResponseDto verificationDetail;
    private ClaimApplicationApprovalResponseDto approvalDetail;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


}
