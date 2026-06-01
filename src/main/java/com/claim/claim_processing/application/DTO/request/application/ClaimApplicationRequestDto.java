package com.claim.claim_processing.application.DTO.request.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRequestDto {

    // ---------------------------------
    // Basic Info / FK IDs
    // ---------------------------------
    private Long claimTypeId;

    private Long claimSourceId;

    private Long submissionChannelId;

    private Long schemeTypeId;

    private ActivityEnum onBehalfOfMember;

    /**
     * In your entity MEMBER_CATEGORY_ID references AgencyCategory.CATEGORY_ID.
     * If CATEGORY_ID is String, keep this as String.
     */
    private String memberCategoryId;

    private Long parentClaimApplicationId;

    private Long specialCaseAuthorityId;

    private Long currentStageId;

    private Long statusId;

    private Long actionId;

    // ---------------------------------
    // Member / Agency Info
    // ---------------------------------
    private String employmentType;

    private String memberCode;

    private String nppfNumber;

    private String agencyCode;

    private Long officeId;

    // ---------------------------------
    // Dates
    // ---------------------------------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applicationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contributionStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contributionEndDate;

    // ---------------------------------
    // Contribution Snapshot
    // ---------------------------------
    private Integer totalContributionMonths;

    private Integer totalContributionYears;

    // ---------------------------------
    // Users
    // ---------------------------------
    private String submittedBy;

    private String initiatedBy;

    // ---------------------------------
    // Flags
    // ---------------------------------
    private ActivityEnum isSpecialCase;

    private ActivityEnum isActive;

    // ---------------------------------
    // Misc
    // ---------------------------------
    private String currencyCode;

    private String remarks;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;

    private String updatedBy;
}