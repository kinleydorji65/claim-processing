package com.claim.claim_processing.application.DTO.request.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationVerificationRequestDto {
    private Long verificationStatusId;

    private ActivityEnum requiresRecalculation;

    private ActivityEnum requiresManualReview;

    private String rejectionReason;

    private String verifierRemarks;

    private String verifiedBy;
    private String rejectedBy;

    private Long verifiedByRoleId;

    private String createdBy;
    private String updatedBy;
}