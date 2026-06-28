package com.claim.claim_processing.application.DTO.response.workFlow;

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

    private Long verificationStatusId;
    private String verificationStatusName;

    private ActivityEnum requiresRecalculation;
    private ActivityEnum requiresManualReview;

    private String rejectionReason;
    private String verifierRemarks;

    private String rejectedBy;
    private String verifiedBy;
    private Long verifiedByRoleId;
    private String verifiedByRoleName;

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