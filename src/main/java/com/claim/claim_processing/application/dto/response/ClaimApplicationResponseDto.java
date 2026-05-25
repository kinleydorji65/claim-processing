package com.claim.claim_processing.application.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationResponseDto {

    private Long id;

    private String applicationNumber;

    // Claim Details
    private Long claimTypeId;
    private String claimTypeName;

    private Long claimSourceId;
    private String claimSourceName;

    private Long submissionChannelId;
    private String submissionChannelName;

    private Long schemeTypeId;
    private String schemeTypeName;

    private Long memberCategoryId;
    private String memberCategoryName;

    // Member Details
    private String employmentType;
    private String memberCode;
    private String nppfNumber;

    private String agencyCode;

    private Long officeId;
    private String officeName;

    // Application Info
    private LocalDate applicationDate;

    private String submittedBy;
    private String initiatedBy;

    private String remarks;

    // Flags
    private Boolean isSpecialCase;

    private Boolean requiresManualReview;

    private Boolean isFinancialCase;

    private Boolean isPaymentRequired;

    private Boolean isPostingRequired;

    private Boolean requiresManualApproval;

    // Related Cases
    private Long parentClaimApplicationId;

    private String relatedCaseReference;

    private Long specialCaseAuthorityId;
    private String specialCaseAuthorityName;

    // Currency
    private String currencyCode;

    // Workflow
    private Long currentStageId;
    private String currentStageName;

    private Long statusId;
    private String statusName;

    private Long actionId;
    private String actionName;

    // Audit
    private String isActive;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
