package com.claim.claim_processing.application.DTO.request.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRequestDto {
    private Long applicationId;
    // ---------------------------------
    // Master References
    // ---------------------------------
    private Long claimTypeId;

    private Long submissionChannelId;

    private Long schemeTypeId;

    private String memberCategoryId;
    private String identityNumber;

    private Long specialCaseAuthorityId;

    private Long currentStageId;

    private Long statusId;

    private Long actionId;

    private BigDecimal numberOfYearInService;

    // ---------------------------------
    // Member Information
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
    private LocalDate pfJoiningDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionJoiningDate;

    // ---------------------------------
    // Claim Information
    // ---------------------------------
    private ActivityEnum onBehalfOfMember; // Y / N

    private String initiatedBy;

    private String currencyCode;

    private String remarks;

    // ---------------------------------
    // Flags
    // ---------------------------------
    private ActivityEnum isSpecialCase;

    private ActivityEnum isActive;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;

    private String updatedBy;

    private Long fromStageId;
    private Long toStageId;
    private Long fromStatusId;
    private Long toStatusId;
    private String reason;
}