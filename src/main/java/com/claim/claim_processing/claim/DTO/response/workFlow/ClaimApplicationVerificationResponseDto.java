package com.claim.claim_processing.claim.DTO.response.workFlow;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.DTO.response.common.DecisionResponseDto;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.DTO.response.others.StatusResponseDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationVerificationResponseDto {

    private Long id;

    private Long claimApplicationId;
    private Integer verificationLevel;

    private DecisionResponseDto verificationDecision;
    private StatusResponseDTO verificationStatus;

    // ---------- Flags ----------
    private ActivityEnum isEligible;
    private ActivityEnum isRuleMatched;
    private ActivityEnum isDocumentVerified;
    private ActivityEnum isBankVerified;
    private ActivityEnum isCalculationVerified;
    private ActivityEnum isDeductionChecked;

    private ActivityEnum requiresRecalculation;
    private ActivityEnum requiresManualReview;

    // ---------- Reasons ----------
    private String returnReason;
    private String rejectionReason;

    // ---------- Review Statuses ----------
    private ReviewStatusResponseDto memberReviewStatus;
    private ReviewStatusResponseDto bankReviewStatus;
    private ReviewStatusResponseDto documentReviewStatus;
    private ReviewStatusResponseDto contributionReviewStatus;
    private ReviewStatusResponseDto ruleReviewStatus;
    private ReviewStatusResponseDto loanReviewStatus;
    private ReviewStatusResponseDto deductionReviewStatus;

    // ---------- Final decision ----------
    private DecisionResponseDto finalVerificationDecision;

    // ---------- Audit ----------
    private String verifierRemarks;
    private String verifiedBy;
    private String verifiedByRole;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime  verifiedAt;

    private String isActive;

    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime  createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime  updatedAt;
}
