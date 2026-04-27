package com.claim.claim_processing.claim.DTO.workFlow;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ClaimApplicationWorkflowResponseDto {
    private Long id;
    private Long applicationId;

    private Long stageId;
    private String stageName;

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
    private String actionBy;

    private Long officeId;
    private String officeName;

    private String referenceNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionAt;
    private String remarks;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
