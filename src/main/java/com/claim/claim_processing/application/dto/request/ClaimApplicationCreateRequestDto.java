package com.claim.claim_processing.application.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCreateRequestDto {

    private Long claimTypeId;
    private Long claimSourceId;
    private Long submissionChannelId;
    private Long schemeTypeId;
    private Long memberCategoryId;

    private String employmentType;
    private String memberCode;
    private String nppfNumber;
    private String agencyCode;
    private Long officeId;

    private LocalDate applicationDate;

    private String submittedBy;
    private String initiatedBy;
    private String remarks;

    private Boolean isSpecialCase;
    private Boolean requiresManualReview;
    private Boolean isFinancialCase;
    private Boolean isPaymentRequired;
    private Boolean isPostingRequired;

    private Long parentClaimApplicationId;
    private String relatedCaseReference;
    private Long specialCaseAuthorityId;

    private String currencyCode;

    private Boolean requiresManualApproval;

    private Long currentStageId;
    private Long statusId;
    private Long actionId;

    private String createdBy;
}
