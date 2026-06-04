package com.claim.claim_processing.application.DTO.request.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationVerificationRequestDto {

    private Long claimApplicationId;

    private Integer verificationLevel;

    private Long verificationStatusId;
    private Long verificationDecisionId;

    private ActivityEnum isEligible;
    private ActivityEnum isRuleMatched;
    private ActivityEnum isDocumentVerified;
    private ActivityEnum isBankVerified;
    private ActivityEnum isCalculationVerified;
    private ActivityEnum isDeductionChecked;
    private ActivityEnum requiresRecalculation;
    private ActivityEnum requiresManualReview;

    private String returnReason;
    private String rejectionReason;

    private Long memberReviewStatusId;
    private Long bankReviewStatusId;
    private Long documentReviewStatusId;
    private Long contributionReviewStatusId;
    private Long ruleReviewStatusId;
    private Long loanReviewStatusId;
    private Long deductionReviewStatusId;

    private Long finalVerificationDecisionId;

    private String verifierRemarks;
    private String verifiedBy;
    private String verifiedByRole;

    private ActivityEnum isActive;

    private String createdBy;
    private String updatedBy;
}