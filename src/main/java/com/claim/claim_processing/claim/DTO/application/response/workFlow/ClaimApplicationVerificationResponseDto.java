package com.claim.claim_processing.claim.DTO.application.response.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationVerificationResponseDto {

    private Long id;

    private Long claimApplicationId;
    private Integer verificationLevel;

    // Verification Status
    private Long verificationStatusId;
    private String verificationStatusName;

    // Verification Decision
    private Long verificationDecisionId;
    private String verificationDecisionName;

    // Verification Flags
    private ActivityEnum isEligible;
    private ActivityEnum isRuleMatched;
    private ActivityEnum isDocumentVerified;
    private ActivityEnum isBankVerified;
    private ActivityEnum isCalculationVerified;
    private ActivityEnum isDeductionChecked;
    private ActivityEnum requiresRecalculation;
    private ActivityEnum requiresManualReview;

    // Reasons
    private String returnReason;
    private String rejectionReason;

    // Section Review Statuses
    private Long memberReviewStatusId;
    private String memberReviewStatusName;

    private Long bankReviewStatusId;
    private String bankReviewStatusName;

    private Long documentReviewStatusId;
    private String documentReviewStatusName;

    private Long contributionReviewStatusId;
    private String contributionReviewStatusName;

    private Long ruleReviewStatusId;
    private String ruleReviewStatusName;

    private Long loanReviewStatusId;
    private String loanReviewStatusName;

    private Long deductionReviewStatusId;
    private String deductionReviewStatusName;

    // Final Verification Decision
    private Long finalVerificationDecisionId;
    private String finalVerificationDecisionName;

    // Verifier Info
    private String verifierRemarks;
    private String verifiedBy;
    private String verifiedByRole;
    private Timestamp verifiedAt;

    // Audit
    private ActivityEnum isActive;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
}
