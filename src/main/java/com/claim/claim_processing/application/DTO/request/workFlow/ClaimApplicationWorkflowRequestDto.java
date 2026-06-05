package com.claim.claim_processing.application.DTO.request.workFlow;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationWorkflowRequestDto {

    private Long claimApplicationId;

    private Integer workflowLevel;

    private Long workflowStageId;

    private Long fromStatusId;

    private Long toStatusId;

    private Long actionId;

    private Long decisionId;

    private String returnReason;

    private String rejectionReason;

    private Long approvalReasonId;

    private String actionBy;

    private Long officeId;

    private String referenceNumber;

    private String remarks;
}