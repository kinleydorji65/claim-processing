package com.claim.claim_processing.application.DTO.response.workFlow;

import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationVerificationResponseDto {

    private Long id;

    private Long claimApplicationId;
    private String applicationNumber;

    private VerificationStatusResponseDto verificationStatus;

    private ActivityEnum requiresRecalculation;
    private ActivityEnum requiresManualReview;

    private ReviewStatusResponseDto memberReviewStatus;
    private ReviewStatusResponseDto bankReviewStatus;
    private ReviewStatusResponseDto documentReviewStatus;
    private ReviewStatusResponseDto contributionReviewStatus;
    private ReviewStatusResponseDto ruleReviewStatus;
    private ReviewStatusResponseDto loanReviewStatus;
    private ReviewStatusResponseDto deductionReviewStatus;

    private String returnReason;
    private String rejectionReason;
    private String verifierRemarks;

    private String verifiedBy;
    private Long verifiedByRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt;

    private String isActive;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}