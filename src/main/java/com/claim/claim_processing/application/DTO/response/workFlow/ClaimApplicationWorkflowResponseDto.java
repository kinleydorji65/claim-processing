package com.claim.claim_processing.application.DTO.response.workFlow;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationWorkflowResponseDto {

    private Long id;

    private Long claimApplicationId;
    private String applicationNumber;

    private Integer workflowLevel;

    private Long workflowStageId;
    private String workflowStageName;

    private Long fromStatusId;
    private String fromStatusName;

    private Long toStatusId;
    private String toStatusName;

    private Long actionId;
    private String actionName;

    private Long decisionId;
    private String decisionName;

    private String returnReason;

    private String rejectionReason;

    private Long approvalReasonId;
    private String approvalReasonName;

    private String actionBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionAt;

    private Long officeId;
    private String officeName;

    private String referenceNumber;

    private String remarks;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}