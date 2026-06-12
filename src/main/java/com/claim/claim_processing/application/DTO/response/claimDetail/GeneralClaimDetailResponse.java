package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralClaimDetailResponse {
    private Long id;

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applicationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pfJoiningDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionJoiningDate;

    private String onBehalfOfMember;
    private String initiatedBy;
    private String remarks;

    private ActivityEnum isSpecialCase;
    private ActivityEnum isActive;

    private Long parentClaimApplicationId;
    private String parentApplicationNumber;

    private Long specialCaseAuthorityId;
    private String specialCaseAuthorityName;

    private String currencyCode;

    private Long currentStageId;
    private String currentStageName;

    private Long statusId;
    private String statusName;

    private Long actionId;
    private String actionName;

    private List<ClaimBankResponseDto> bankDetails;
    private ClaimDeductionResponseDto deductionDetail;
    private ClaimCalculationSummaryResponseDto calculationSummary;
    private NormalClaimResponseDto normalClaimDetails;
    private PartialWithdrawalResponseDto partialWithdrawalDetails;
    private BeneficiarySettlementResponseDto beneficiarySettlementDetails;
    private List<ClaimForfeitedComponentResponseDto> forfeitedComponents;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
