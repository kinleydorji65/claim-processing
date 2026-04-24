package com.claim.claim_processing.claim.DTO.application.response.workFlow;


import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationWorkflowResponseDto {

    private Long id;

    // -------------------------------
    // BASIC
    // -------------------------------
    private Long claimApplicationId;
    private Integer workflowLevel;

    // -------------------------------
    // STAGE
    // -------------------------------
    private Long workflowStageId;

    // -------------------------------
    // STATUS TRANSITION
    // -------------------------------
    private Long fromStatusId;
    private String fromStatusCode;
    private String fromStatusName;

    private Long toStatusId;
    private String toStatusCode;
    private String toStatusName;

    // -------------------------------
    // ACTION & DECISION
    // -------------------------------
    private Long actionId;
    private String actionCode;
    private String actionName;

    private Long decisionId;
    private String decisionCode;
    private String decisionName;

    // -------------------------------
    // REASONS
    // -------------------------------
    private String returnReason;
    private String rejectionReason;

    private Long approvalReasonId;
    private String approvalReasonCode;
    private String approvalReasonName;

    // -------------------------------
    // ACTION INFO
    // -------------------------------
    private String actionBy;
    private Timestamp actionAt;
    private Long officeId;

    private String referenceNumber;
    private String remarks;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
