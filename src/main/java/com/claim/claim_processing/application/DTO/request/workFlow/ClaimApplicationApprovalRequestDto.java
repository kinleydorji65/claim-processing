package com.claim.claim_processing.application.DTO.request.workFlow;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationApprovalRequestDto {

    private Long claimApplicationId;

    private Long approvalStatusId;
    private Long actionId;

    private ActivityEnum requiresManualReview;

    private String approverRemarks;

    private String approvedBy;
    private Long roleId;

    private ActivityEnum isActive;

    private String createdBy;
    private String updatedBy;
}