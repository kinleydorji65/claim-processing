package com.claim.claim_processing.application.DTO.request.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationVerificationRequestDto {
    private Long verificationStatusId;

    private Long memberReviewStatusId;

    private Long bankReviewStatusId;

    private Long documentReviewStatusId;

    private Long contributionReviewStatusId;

    private Long ruleReviewStatusId;

    private Long deductionReviewStatusId;

    private ActivityEnum requiresRecalculation;

    private ActivityEnum requiresManualReview;

    private String returnReason;

    private String rejectionReason;

    private String verifierRemarks;

    private Long actionId;

    private String verifiedBy;

    private Long verifiedByRole;

    private String createdBy;
    private String updatedBy;
}